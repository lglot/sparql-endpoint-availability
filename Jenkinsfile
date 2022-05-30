pipeline {
  agent any
  stages {
    stage('Build and Test') {
      agent {
        docker {
          image 'jdk-11'
        }

      }
      steps {
        sh './mvnw verify'
      }
    }

  }
}