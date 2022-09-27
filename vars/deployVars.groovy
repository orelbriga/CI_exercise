
def downloadKubectl(Map config = [:]) {
    sh  """wget "https://storage.googleapis.com/kubernetes-release/release/v${config.version}/bin/linux/amd64/kubectl"
           chmod +x ./kubectl"""
}

def appName() {
    sh(
            script: "./kubectl get pod | grep ${env.IMAGE_NAME}-${env.TAG} | awk \'{print \$1}\' ",
            returnStdout: true
    ).trim()
}

def clusterHostIP() {
    sh(
            script: "./kubectl get pod -n kube-system \$(./kubectl get po -n kube-system | grep dns \
                            | awk \'{print \$1}\') -o=jsonpath=\'{.status.hostIP}\' ",
            returnStdout: true
    ).trim()
}

def nodePort() {
    sh(
            script: "./kubectl get svc ${env.IMAGE_NAME}-svc-${env.TAG} -o=jsonpath=\'{.spec.ports[].nodePort}\' ",
            returnStdout: true
    ).trim()
}

def getAppLogs() {
    // def APP = config.appName ?: appName()
    sh (script: "./kubectl logs ${appName()} | tee ${appName()}.log")

}

def podState() {
    sh(
            script: "./kubectl get po | grep ${env.IMAGE_NAME}-${env.TAG} | awk \'{print \$3}\' ",
            returnStdout: true
    ).trim()
}

def getRequest(Map config = [:]) {
    def CLUSTER_HOST_IP = clusterHostIP()
    def IP = config.clusterHostIP ?: clusterHostIP()
    def NODE_PORT = nodePort()
    def PORT = config.nodePort ?: NODE_PORT
    log.info "Sending GET request to the application: "
    retry(3) {
        try {
            sh "sleep 5s"
            def RESPONSE = httpRequest url: "http://${IP}:${PORT}"
            log.info "Content: " + RESPONSE.content
        }
        catch (e) {
            error "GET request failed due to error: $e"
        }
    }
}

def checkPodState() {
    // def APP_POD_NAME = appName()
    // def POD_STATE = podState()
    if (!podState().equals("Running")) {
        error("Application pod ${appName()} is not healthy, check app log")
    }
    else {
        log.info "Application pod ${appName()} is in ${podState()} state!"
    }
}