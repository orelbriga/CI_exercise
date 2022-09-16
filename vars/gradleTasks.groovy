def tests() {
    log.info "compiling code + running  tests: "
    sh """chmod +x ./gradlew
          ./gradlew test """
}


def imageJib() {
    sh "./gradlew jib --image=${env.REGISTRY}/${env.REPOSITORY}:${env.TAG}"
}