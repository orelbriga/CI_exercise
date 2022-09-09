def appName() {
    sh(
            script: './kubectl get pod | grep hello-world-app-$BUILD_NUMBER-* | \
                                  awk \'{print $1}\'',
            returnStdout: true
    ).trim()
}


def podState = sh(
        script: './kubectl get po | grep hello-world-app-$BUILD_NUMBER-* | \
                                    awk \'{print $3}\'',
        returnStdout: true
).trim()


def clusterHostIP = sh(
        script: './kubectl get pod -n kube-system $(./kubectl get po -n kube-system | grep dns \
                            | awk \'{print $1}\') -o=jsonpath=\'{.status.hostIP}\' ', returnStdout: true
).trim()


def nodePort = sh(
        script: './kubectl get svc hello-world-svc-$BUILD_NUMBER -o=jsonpath=\'{.spec.ports[].nodePort}\' ',
        returnStdout: true
).trim()