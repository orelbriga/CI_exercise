@Library("shared-library") _
def POD_LABEL = "agent-${env.JOB_NAME}-${env.BUILD_NUMBER}"
podTemplate(label: POD_LABEL,  yaml: libraryResource('com/ci-task/podTemplates/agent-python-zip.yaml'))
{
    node(POD_LABEL) {
        stage('Check python file and OS validation') {
            containerLog 'centospy'
        }
        stage('Run py script') {
            container('centospy') {
                try {
                    sh 'python3 /tmp/zip_job.py'
                    archiveArtifacts artifacts: '*.zip', onlyIfSuccessful: true
                }
                catch (e) {
                    error "Py script was aborted due to the following error: $e"
                }
            }
        }
    }
}
