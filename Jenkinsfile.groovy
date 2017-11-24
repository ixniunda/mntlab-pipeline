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
        	'Cucumber tests': {sh "${gradle_home}/bin/gradle cucumber"},
        	'Jacoco Tests': {sh "${gradle_home}/bin/gradle jacocoTestReport"},
        	'Unit Tests': {sh "${gradle_home}/bin/gradle test"})
    }
}
