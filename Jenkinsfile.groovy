//new Jenkinsfile


node {
    def gradle_home = tool 'gradle3.3'
    stage('Preparation (Checking out)') {
        checkout scm: [$class: 'GitSCM', branches: [[name: '*/ivauchok']], userRemoteConfigs: [[url: 'https://github.com/MNT-Lab/mntlab-pipeline.git']]]
    }
    stage('Building code') {
        sh "${gradle_home}/bin/gradle clean build"
    }
    stage('Testing code') {
	parallel (
        	phase1: {sh "${gradle_home}/bin/gradle cucumber"},
        	phase2: {sh "${gradle_home}/bin/gradle jacocoTestReport"},
        	phase3: {sh "${gradle_home}/bin/gradle test"})
    }
}
