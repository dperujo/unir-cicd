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
                def userEmail = sh(script: 'git log -1 --format=%ae', returnStdout: true).trim()
                echo "Job '${jobName}' (Build ${buildNumber}) has failed!"
                emailext body: "Job '${jobName}' (Build ${buildNumber}) has failed!",
                    subject: "Pipeline Failure: ${jobName} (Build ${buildNumber})",
                    to: userEmail
            }
        }
    }
}
