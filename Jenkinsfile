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
        sh './mnvw verify --file pom.xml'
      }
    }

  }
}