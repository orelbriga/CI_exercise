def call() {
    echo "testing call method.."
}



public void agentTemplate(body) {
    podTemplate(
            containers: [
                    containerTemplate(name: 'gradle', image: 'gradle:7.5.1-jdk11-jammy', resourceLimitMemory:'1024Mi', resourceRequestMemory:'512Mi', command: 'cat', ttyEnabled: true),
                    containerTemplate(name: 'docker', image: 'docker', resourceLimitMemory:'1024Mi', resourceRequestMemory:'512Mi', command: 'cat', ttyEnabled: true)],
            volumes: [hostPathVolume(hostPath: '/var/run/docker.sock', mountPath: '/var/run/docker.sock')]) {
        body.call()
    }
}