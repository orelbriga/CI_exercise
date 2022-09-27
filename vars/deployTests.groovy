def call() {
    script{
        withKubeConfig([credentialsId: 'secret-jenkins']) {
            log.info "installing kubectl on the container to check the application's pod state + logs:"
            deployVars.downloadKubectl(version:"1.24.1")
            deployVars.getRequest()
            sh "sleep 3s"
            deployVars.getAppLogs()
            archiveArtifacts artifacts: "${env.IMAGE_NAME}-*.log"
            deployVars.checkPodState()
        }
    }
}

