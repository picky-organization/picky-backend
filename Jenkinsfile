pipeline{
    agent any
    stages{
        stage('Prepare'){
            steps {
                sh 'chmod +x ./gradlew'
                sh './gradlew clean'
            }
        }
        stage('Build') {
            steps {
                sh './gradlew build -x test'
            }
        }
        stage('Test') {
            steps {
                sh './gradlew test'
            }
        }
        stage('Deploy Prepare'){
            steps{
                sh 'docker build . -t picky-backend'
                sh 'docker stop picky-backend-container||true'
                sh 'docker rm picky-backend-container||true'
            }
        }

        stage('Deploy') {
            steps {
                sh 'docker run -d -p 80:9090 --env TOKEN_SECRET_KEY --env ACCESS_TOKEN_EXPIRED_MILLISECONDS --env REFRESH_TOKEN_EXPIRED_MILLISECONDS --name picky-backend-container picky-backend'
            }
        }
    }
}