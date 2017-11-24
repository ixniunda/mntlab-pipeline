//new Jenkinsfile


node {
    stage('Preparation (Checking out)') {
        checkout scm: [$class: 'GitSCM', branches: [[name: '*/ivauchok']], userRemoteConfigs: [[url: 'https://github.com/MNT-Lab/mntlab-pipeline.git']]]
    }
    stage('Building code') {
                sh 'gradle clean build'
    }
}
