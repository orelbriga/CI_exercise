#!groovy
def downloadKubectl(Map config = [:]) {
    sh  """wget "https://storage.googleapis.com/kubernetes-release/release/v${config.version}/bin/linux/amd64/kubectl"
           chmod +x ./kubectl"""
}


def getRequest(Map config = [:]) {
    def clusterHostIP = sh(
            script: "./kubectl get pod -n kube-system \$(./kubectl get po -n kube-system | grep dns \
                            | awk \'{print \$1}\') -o=jsonpath=\'{.status.hostIP}\' ", returnStdout: true).trim()

    def nodePort = sh(
            script: "./kubectl get svc ${env.IMAGE_NAME}-svc-${env.TAG} -o=jsonpath=\'{.spec.ports[].nodePort}\' ",
            returnStdout: true).trim()

    def IP = config.IP ?: clusterHostIP
    def PORT = config.PORT ?: nodePort
    log.info "Sending GET request to the application: "
    retry(3) {
        try {
            sh "sleep 5s"
            def RESPONSE = httpRequest timeout:10, url: "http://${IP}:${PORT}"
            log.info "Content: " + RESPONSE.content
        }
        catch (e) {
            error "GET request failed due to error: $e"
        }
    }
}


def getAppLogs() {
    sh (script: "./kubectl logs deploy/${env.IMAGE_NAME}-${env.TAG} | tee ${env.IMAGE_NAME}-${env.TAG}.log")
}


def checkPodState() {
    def podName = sh(
            script: "./kubectl get pod | grep ${env.IMAGE_NAME}-${env.TAG} | awk \'{print \$1}\' ",
            returnStdout: true
    ).trim()

    def podState = sh(
            script: "./kubectl get po | grep ${env.IMAGE_NAME}-${env.TAG} | awk \'{print \$3}\' ",
            returnStdout: true).trim()
    if (podState != "Running") {
        error("Application pod ${podName} is not healthy, check app log")
    }
    else {
        log.info "Application pod ${podName} is in ${podState} state!"
    }
}