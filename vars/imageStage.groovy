env.REGISTRY = "docker.io"
env.IMAGE_NAME = "hello-world-app"
env.REGISTRY_USER = "orelbriga"
env.REPOSITORY = REGISTRY_USER+"/"+IMAGE_NAME
env.TAG = "${env.BUILD_NUMBER}"

def build() {
    withCredentials([[$class: 'UsernamePasswordMultiBinding',
                      credentialsId: 'dockerhub',
                      usernameVariable: 'DOCKER_HUB_USER',
                      passwordVariable: 'DOCKER_HUB_PASSWORD']]) {
        sh """ echo ${DOCKER_HUB_PASSWORD} | docker login --username ${DOCKER_HUB_USER} --password-stdin
                       docker build -t ${env.REPOSITORY}:${env.TAG} . """
    }
}


def push() {
    sh "docker push ${env.REGISTRY}/${env.REPOSITORY}:${env.TAG}"
}
