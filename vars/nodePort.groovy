def nodePort = sh(
            script: './kubectl get svc hello-world-svc-$BUILD_NUMBER -o=jsonpath=\'{.spec.ports[].nodePort}\' ',
            returnStdout: true
    ).trim()

//def call() {
//    sh(
//            script: './kubectl get svc hello-world-svc-$BUILD_NUMBER -o=jsonpath=\'{.spec.ports[].nodePort}\' ',
//            returnStdout: true
//    ).trim()
//}

def CLUSTER_HOST_IP = sh(
        script: './kubectl get pod -n kube-system $(./kubectl get po -n kube-system | grep dns \
                            | awk \'{print $1}\') -o=jsonpath=\'{.status.hostIP}\' ' , returnStdout: true
).trim()