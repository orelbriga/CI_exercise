def call() {
    sh(
            script: './kubectl get pod | grep hello-world-app-$BUILD_NUMBER-* | \
                                  awk \'{print $1}\'',
            returnStdout: true
    ).trim()
}


def CLUSTER_HOST_IP = sh(
        script: './kubectl get pod -n kube-system $(./kubectl get po -n kube-system | grep dns \
                            | awk \'{print $1}\') -o=jsonpath=\'{.status.hostIP}\' ', returnStdout: true
).trim()


