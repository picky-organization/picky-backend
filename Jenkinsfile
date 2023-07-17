pipeline{
	agent any
	stages {
		stage('Gradle Build picky-backend') {
			steps {
			    sh 'chmod +x gradlew'
			    sh './gradlew build -x test'
			}
		}
		stage('Test'){
		    steps{
		        sh './gradlew test'
		    }
		}
	}
}