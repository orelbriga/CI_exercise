def build() {
    withCredentials([[$class: 'UsernamePasswordMultiBinding',
                      credentialsId: 'dockerhub',
                      usernameVariable: 'DOCKER_HUB_USER',
                      passwordVariable: 'DOCKER_HUB_PASSWORD']]) {
        sh """ echo ${DOCKER_HUB_PASSWORD} | docker login --username ${DOCKER_HUB_USER} --password-stdin
                       docker build -t ${env.REPOSITORY}:${env.BUILD_NUMBER} . """
    }
}
