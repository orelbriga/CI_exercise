def call() {
    withKubeConfig([credentialsId: 'secret-jenkins']) {
        log.info "Terminating the app: "
        sh "./kubectl delete deployment,services -l app=${env.IMAGE_NAME}-${env.TAG}"
        sleep 3
        log.info "Delete unused app image: "
        sh "docker image rmi -f ${env.REPOSITORY}:${env.TAG}"
    }
}
