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
    stage('Triggering job and fetching artefact after finishing') {
        build job: 'Ihar Vauchok/MNTLAB-ivauchok-child1-build-job', parameters: [[$class: 'StringParameterValue', name: 'BRANCH_NAME', value: 'ivauchok']], wait: true
        sh "cp ${JENKINS_HOME}/workspace/Ihar\\ Vauchok/MNTLAB-ivauchok-child1-build-job/ivauchok_dsl_script.tar.gz ${JENKINS_HOME}/workspace/pipeline/"
    }
    stage('Packaging and Publishing results') {
        sh "tar -zxvf ivauchok_dsl_script.tar.gz && tar -czf pipeline-ivauchok-${BUILD_NUMBER}.tar.gz ./jobs.groovy ./Jenkinsfile.groovy ./build/libs/gradle-simple.jar"
    archiveArtifacts "pipeline-ivauchok-*.tar.gz"
    }
}
