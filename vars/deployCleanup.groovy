
def terminateApp() {
    sh './kubectl delete deployment,services -l app=hello-world-app-${BUILD_NUMBER}'
}


def deleteImage() {
    sh '''docker image rmi -f orelbriga/hello-world-app:$BUILD_NUMBER
          docker image prune -f  --filter="dangling=true" '''
}