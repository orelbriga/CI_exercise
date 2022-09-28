def call () {
    env.REPOSITORY = env.REGISTRY_USER+"/"+env.IMAGE_NAME
    env.TAG = "${env.BUILD_NUMBER}"
    String buildResult

    def POD_LABEL = "agent-${env.JOB_NAME}-${env.BUILD_NUMBER}"
    podTemplate(label: POD_LABEL,  yaml: libraryResource('com/ci-task/podTemplates/agent-ci-cd.yaml'))
    {
        node(POD_LABEL) {
            try {
                stage('Git checkout') {
                    checkout([$class: 'GitSCM', branches: [[name: "*/${params.BRANCH}"]], userRemoteConfigs: [[credentialsId: 'github-private',\
                              url: 'https://github.com/orelbriga/hello-world.git']]])
                }
                stage('Gradle: Tests') {
                    container('gradle') {
                        sh "mkdir -p /gradlePV/gradle-cache/.gradle/caches"
                        if (params.CACHE) {
                            log.info "copying gradle cache from PV"
                            sh "cp -r /gradlePV/gradle-cache/.gradle/caches/. ~/.gradle/caches"
                        }
                        log.info "copying build-cache data from PV:"
                        sh "mkdir -p /gradlePV/gradle-cache/gradle-build-cache"
                        def exists = sh(script: "test -d build-cache && echo '1' || echo '0'", returnStdout:true).trim()
                        if (exists == '0'){
                            sh "mkdir build-cache"
                        }
                        sh "cp -r /gradlePV/gradle-cache/gradle-build-cache/. build-cache"

                        try {
                            log.info "compiling code + running  tests: "
                            sh "chmod +x ./gradlew"
                            sh  "./gradlew --build-cache test "
                        }
                        catch (e) {
                            error("some of the tests have failed - $e ")
                        }
                        finally {
                            log.info "copying most updated build-cache data to mounted path:"
                            sh "cp -r build-cache/. /gradlePV/gradle-cache/gradle-build-cache"
                            log.info "copying most updated gradle cache data to mount path:"
                            sh "cp -r ~/.gradle/caches/. /gradlePV/gradle-cache/.gradle/caches"
                            log.info "creating Junit report based on test results + HTML Report"
                            junit 'build/test-results/test/*.xml'
                            publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: false, reportDir: 'build/reports/tests/test',\
                            reportFiles: 'index.html', reportName: 'HTML Report', reportTitles: '', useWrapperFileDirectly: true])
                        }
                    }
                }
                stage('Gradle JIB: Build docker image & push to registry') {
                    container('gradle') {
                        try {
                            withCredentials([[$class: 'UsernamePasswordMultiBinding',
                                              credentialsId: 'dockerhub',
                                              usernameVariable: 'DOCKER_HUB_USER',
                                              passwordVariable: 'DOCKER_HUB_PASSWORD']]) {
                                sh(
                                        """ ./gradlew jib \
                                        -Djib.to.image=${env.REGISTRY}/${env.REPOSITORY}:${env.TAG} \
                                        -Djib.to.auth.username=$DOCKER_HUB_USER \
                                        -Djib.to.auth.password=$DOCKER_HUB_PASSWORD """)
                            }
                        }
                        catch (e) {
                            error "Failed to build / push the image with Jib plugin due to error: $e"
                        }
                    }
                }
                stage('Deploy app to k8s') {
                    container('docker') {
                        log.info "deploy the app to the k8s cluster using yaml files - with kube-config as an authenticator: "
                        kubernetesDeploy(configs: 'config.yaml', kubeconfigId: 'k8sconfig')
                    }
                }
                stage('Deployment Tests') {
                    container('docker') {
                        try {
                            log.info "Running deployment tests"
                            script {
                                withKubeConfig([credentialsId: 'secret-jenkins']) {
                                    log.info "installing kubectl on the container to check the application's pod state + logs:"
                                    deployVars.downloadKubectl(version: "1.24.1")
                                    deployVars.getRequest()
                                    sh "sleep 3s"
                                    deployVars.getAppLogs()
                                    archiveArtifacts artifacts: "${env.IMAGE_NAME}-*.log"
                                    deployVars.checkPodState()
                                }
                                buildResult = "SUCCESS"
                            }
                        }
                        catch (e) {
                            buildResult = "FAILURE"
                            error  "Deployment tests failed due to the error: ${e}"
                        }
                        finally {
                            if (buildResult == 'SUCCESS') {
                                log.info "Deployment tests passed successfully"
                                log.info "Cleanup: Terminate the app + delete unused image"
                                withKubeConfig([credentialsId: 'secret-jenkins']) {
                                    log.info "Terminating the app: "
                                    sh "./kubectl delete deployment,services -l app=${env.IMAGE_NAME}-${env.TAG}"
                                    sh "sleep 3s"
                                    log.info "Delete unused app image: "
                                    sh "docker image rmi -f ${env.REPOSITORY}:${env.TAG}"
                                }
                            }
                            else {
                                log.info "keeping the app alive for investigation"
                            }
                        }
                    }
                }
            }
            catch (e) {
                error "Pipeline failed due to the error: ${e}"
            }
        }
    }
}
