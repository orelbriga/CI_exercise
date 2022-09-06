def call() {
    sh(
            script: './kubectl get pod | grep hello-world-app-$BUILD_NUMBER-* | \
                                  awk \'{print $1}\'',
            returnStdout: true
    ).trim()
}