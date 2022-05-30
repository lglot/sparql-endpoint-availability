pipeline {
  agent any
  stages {
    stage('Build and Test') {
      agent {
        docker {
          image 'openjdk:17-alpine'
        }

      }
      steps {
        sh './mvnw verify'
      }
    }

  }
}