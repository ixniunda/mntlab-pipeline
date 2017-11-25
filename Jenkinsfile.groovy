node {
    notifyStarted()
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
        sh "tar -zxvf ivauchok_dsl_script.tar.gz && cp build/libs/gradle-simple.jar gradle-simple.jar && tar -czf pipeline-ivauchok-${BUILD_NUMBER}.tar.gz jobs.groovy Jenkinsfile.groovy gradle-simple.jar"
    archiveArtifacts "pipeline-ivauchok-*.tar.gz"
    sh "curl -v --user 'nexus-service-user:123456' --upload-file 'pipeline-ivauchok-${BUILD_NUMBER}.tar.gz' 'http://nexus/repository/project-releases/pipeline/pipeline-ivauchok/${BUILD_NUMBER}/pipeline-ivauchok-${BUILD_NUMBER}.tar.gz'"
    }
    stage('Asking for manual approval') {
        input 'Do you want to deploy gradle-simple.jar?'
    }
    stage('Deployment') {
        sh "java -jar gradle-simple.jar"
    }
    stage('Sending status') {
        notifySuccessful()
    }
}

def notifyStarted() {
    slackSend (color: '#FFFF00', message: "STARTED: Job '${JOB_NAME} [${BUILD_NUMBER}]' (${BUILD_URL})")
    emailext (
            subject: "STARTED: Job '${JOB_NAME} [${BUILD_NUMBER}]'",
            body: """<p>STARTED: Job '${JOB_NAME} [${BUILD_NUMBER}]':</p>
         <p>Check console output at "<a href="${BUILD_URL}">${JOB_NAME} [${BUILD_NUMBER}]</a>"</p>""",
            recipientProviders: [[$class: 'DevelopersRecipientProvider']]
    )
}

def notifySuccessful() {
    slackSend (color: '#00FF00', message: "SUCCESSFUL: Job '${JOB_NAME} [${BUILD_NUMBER}]' (${BUILD_URL})")
    emailext (
            subject: "SUCCESSFUL: Job '${JOB_NAME} [${BUILD_NUMBER}]'",
            body: """<p>SUCCESSFUL: Job '${JOB_NAME} [${BUILD_NUMBER}]':</p>
         <p>Check console output at "<a href="${BUILD_URL}">${JOB_NAME} [${BUILD_NUMBER}]</a>"</p>""",
            recipientProviders: [[$class: 'DevelopersRecipientProvider']]
    )
}
