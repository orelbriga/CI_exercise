def call() {
    script{
        withKubeConfig([credentialsId: 'secret-jenkins']) {
            log.info "installing kubectl on the container to check the application's pod state + logs:"
            deployVars.downloadKubectl(version:"1.24.1")
            sleep 5
            deployVars.getRequest()
            sleep 3
            deployVars.getAppLogs()
            archiveArtifacts artifacts: "${env.IMAGE_NAME}-*.log"
            deployVars.checkPodState()
        }
    }
}

