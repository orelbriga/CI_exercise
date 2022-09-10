import org.yaml.snakeyaml.events.NodeEvent

def downloadKubectl(Map config = [:]) {
    sh  """wget "https://storage.googleapis.com/kubernetes-release/release/v${config.version}/bin/linux/amd64/kubectl"
           chmod +x ./kubectl"""
}


def appName() {
    sh(
            script: "./kubectl get pod | grep ${env.IMAGE_NAME}-${env.TAG}-* | \
                       awk \'{print \$1}\' ",
            returnStdout: true
    ).trim()
}


def clusterHostIP() {
    sh(
            script: "./kubectl get pod -n kube-system \$(./kubectl get po -n kube-system | grep dns \
                            | awk \'{print \$1}\') -o=jsonpath=\'{.status.hostIP}\' ", returnStdout: true
    ).trim()
}


def nodePort() {
    sh(
            script: './kubectl get svc hello-world-app-svc-$BUILD_NUMBER -o=jsonpath=\'{.spec.ports[].nodePort}\' ',
            returnStdout: true
    ).trim()
}


def getAppLogs(Map config = [:]) {
    def APP_POD_NAME = appName()
    def APP = config.appName ?: APP_POD_NAME
    sh "./kubectl logs ${APP} | tee ${APP}.log"

}


def podState() {
    sh(
            script: './kubectl get po | grep hello-world-app-$BUILD_NUMBER-* | \
                                    awk \'{print $3}\'',
            returnStdout: true
    ).trim()
}



def getRequest(Map config = [:]) {
    def CLUSTER_HOST_IP = clusterHostIP()
    def IP = config.clusterHostIP ?: CLUSTER_HOST_IP
    def NODE_PORT = nodePort()
    def PORT = config.nodePort ?: NODE_PORT
    echo "Sending GET request to the application: "
    def RESPONSE = httpRequest "http://${IP}:${PORT}"
    println("Content: " + RESPONSE.content)
}
