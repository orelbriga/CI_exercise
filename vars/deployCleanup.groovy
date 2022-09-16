
def terminateApp() {
    sh "./kubectl delete deployment,services -l app=${env.IMAGE_NAME}-${env.TAG}"
}


def deleteImage() {
    sh "docker image rmi -f ${env.REPOSITORY}:${env.TAG}"
}