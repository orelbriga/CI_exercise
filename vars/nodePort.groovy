def call() {
    sh(
            script: './kubectl get svc hello-world-svc-$BUILD_NUMBER -o=jsonpath=\'{.spec.ports[].nodePort}\' ',
            returnStdout: true
    ).trim()
}
