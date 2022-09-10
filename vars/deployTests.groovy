def call() {
    withKubeConfig([credentialsId: 'secret-jenkins']) {
        log.info "installing kubectl on the container to check the application's pod state + logs:"
        downloadKubectl(version:"1.24.1")
        sleep 5
        getRequest()
        sleep 3
        getAppLogs()
        archiveArtifacts artifacts: "${env.IMAGE_NAME}-*.log"
        checkPodState()
    }
}