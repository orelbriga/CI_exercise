
def terminateApp() {
    sh "./kubectl delete deployment,services -l app=${env.IMAGE_NAME}-${env.TAG}"
}


def deleteImage() {
    sh '''docker image rmi -f orelbriga/hello-world-app:$BUILD_NUMBER
          docker image prune -f  --filter="dangling=true" '''
}