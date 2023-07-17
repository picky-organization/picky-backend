pipeline{
	agent any
	environment {
	}
	stages {
		stage('Gradle Build picky-backend') {
			steps {
			    sh './gradlew build -x test'
			}
		}
		stage('Test'){
		    steps{
		        sh './gradlew test'
		    }
		}
		stage('Docker build & push'){
		    steps{
		    }
		}
	}
}