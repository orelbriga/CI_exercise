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
                    container('gradle') {
                        checkout([$class: 'GitSCM', branches: [[name: '*/draft']], userRemoteConfigs: [[credentialsId: 'github-private',\
                                  url: 'https://github.com/orelbriga/hello-world.git']]])
                    }
                }
                stage('Gradle: Tests') {
                    container('gradle') {
                        try {
                            log.info "compiling code + running  tests: "
                            sh "chmod +x ./gradlew"
                            sh "mkdir -p /gradlePV/tmp-gradle-cache"
                            sh "mkdir build-cache"
                            sh script: " \\cp -r /gradlePV/tmp-gradle-cache/* build-cache"
                            sh  "./gradlew --build-cache test "
                            sh "\\cp -r build-cache/* /gradlePV/tmp-gradle-cache"
                        }
                        catch (e) {
                            error("some of the tests have failed - $e ")
                        }
                        finally {
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
                                        script: """ ./gradlew jib \
                                        -Djib.to.image=${env.REGISTRY}/${env.REPOSITORY}:${env.TAG} \
                                        -Djib.to.auth.username=$DOCKER_HUB_USER \
                                        -Djib.to.auth.password=$DOCKER_HUB_PASSWORD """, returnStdout: true)
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
                            deployTests()
                            buildResult = "SUCCESS"
                        }
                        catch (e) {
                            buildResult = "FAILURE"
                            error  "Deployment tests failed due to the error: ${e}"
                        }
                        finally {
                            if (buildResult == 'SUCCESS') {
                                log.info "Deployment tests passed successfully"
                                log.info "Cleanup: Terminate the app + delete unused image"
                                deployCleanup()
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
