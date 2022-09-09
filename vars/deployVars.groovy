def downloadKubectl(Map config = [:]) {
    sh  """wget "https://storage.googleapis.com/kubernetes-release/release/v${config.version}/bin/linux/amd64/kubectl"
           chmod +x ./kubectl"""
}


def appName() {
    sh(
            script: './kubectl get pod | grep hello-world-app-$BUILD_NUMBER-* | \
                                  awk \'{print $1}\'',
            returnStdout: true
    ).trim()
}


def podState() {
    sh(
            script: './kubectl get po | grep hello-world-app-$BUILD_NUMBER-* | \
                                    awk \'{print $3}\'',
            returnStdout: true
    ).trim()
}


def clusterHostIP() {
    sh(
            script: './kubectl get pod -n kube-system $(./kubectl get po -n kube-system | grep dns \
                            | awk \'{print $1}\') -o=jsonpath=\'{.status.hostIP}\' ', returnStdout: true
    ).trim()
}


def nodePort() {
    sh(
            script: './kubectl get svc hello-world-svc-$BUILD_NUMBER -o=jsonpath=\'{.spec.ports[].nodePort}\' ',
            returnStdout: true
    ).trim()
}


def getAppLogs(Map config = [:]) {
    try {
        config.appName ?: appName()
        sh "./kubectl logs ${config.appName} | tee ${config.appName}.log"
    }
    catch (Exception e) {
        error("Mandatory paramater 'appName' cannot be empty")
    }
}

def getRequest(Map config = [:]) {
    echo "Sending GET request to the application: "
    def RESPONSE = httpRequest "http://${config.clusterHostIP}:${config.nodePort}"
    println("Content: "+RESPONSE.content)
}
