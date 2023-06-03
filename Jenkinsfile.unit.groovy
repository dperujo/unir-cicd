pipeline {
    agent {
        label 'docker'
    }
    stages {
        stage('Build') {
            steps {
                echo 'Building stage!'
                sh 'make build'
            }
        }
        stage('Unit tests') {
            steps {
                sh 'make test-unit'
                archiveArtifacts artifacts: 'results/*.xml'
            }
        }
        stage('API tests') {
            steps {
                sh 'make test-api'
                archiveArtifacts artifacts: 'results/*.xml'
            }
        }
        stage('E2E tests') {
            steps {
                sh 'make test-e2e'
                archiveArtifacts artifacts: 'results/*.xml'
            }
        }
    }
    post {
        always {
            junit 'results/*_result.xml'
            cleanWs()
        }
        failure {
            script {
                def jobName = env.JOB_NAME
                def buildNumber = env.BUILD_NUMBER
                def user = currentBuild.getBuildCauses()[0].userId
                def userEmail = "correo@gmail.com"
                echo "Se enviará correo a '${user}'"
                echo "BODY: Trabajo '${jobName}' (Ejecución ${buildNumber}) ha fallado!"
                echo "SUBJECT Pipeline fallida: ${jobName} (Ejecución ${buildNumber})"
                emailext body: "Trabajo '${jobName}' (Ejecución ${buildNumber}) ha fallado!",
                    subject: "Pipeline fallida: ${jobName} (Ejecución ${buildNumber})",
                    to: userEmail
            }
        }
    }
}
