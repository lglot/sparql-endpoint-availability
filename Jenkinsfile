pipeline {
  agent any
  stages {
    stage('Build and Test') {
      agent {
        docker {
          image 'maven:3.6.3'
        }

      }
      steps {
        sh './mvnw package --file pom.xml'
      }
    }

  }
}
