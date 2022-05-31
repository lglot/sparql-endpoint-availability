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

    stage('Set commit status') {
      steps {
        setGitHubPullRequestStatus(state: 'SUCCESS', message: 'Build and test eseguito', context: 'Jenkins Pipeline')
      }
    }

  }
}