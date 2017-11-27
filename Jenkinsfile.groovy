node {
    notifyStarted()

    def gradle_home = tool 'gradle3.3'
    def branch_name = 'ivauchok'
    def folder_name = 'EPBYMINW2473'
    def artifact_name = "pipeline-${branch_name}-${BUILD_NUMBER}.tar.gz"

    stage('Preparation (Checking out)') {
        checkout scm: [$class: 'GitSCM', branches: [[name: "*/${branch_name}"]], userRemoteConfigs: [[url: 'https://github.com/MNT-Lab/mntlab-pipeline.git']]]
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
        build job: "${folder_name}/MNTLAB-${branch_name}-child1-build-job", parameters: [[$class: 'StringParameterValue', name: 'BRANCH_NAME', value: "${branch_name}"]], wait: true
        copyArtifacts(projectName: "${folder_name}/MNTLAB-${branch_name}-child1-build-job", filter: '*dsl_script.tar.gz')
        //sh "cp ${JENKINS_HOME}/workspace/Ihar\\ Vauchok/MNTLAB-${branch_name}-child1-build-job/${branch_name}_dsl_script.tar.gz ${JENKINS_HOME}/workspace/pipeline/"
    }
    stage('Packaging and Publishing results') {
        sh "tar -zxvf ${branch_name}_dsl_script.tar.gz && cp build/libs/gradle-simple.jar gradle-simple.jar && tar -czf pipeline-${branch_name}-${BUILD_NUMBER}.tar.gz jobs.groovy Jenkinsfile.groovy gradle-simple.jar"
        archiveArtifacts "${artifact_name}"
        sh "curl -v --user 'nexus-service-user:123456' --upload-file ${artifact_name} http://nexus/repository/project-releases/pipeline/pipeline-${branch_name}/${BUILD_NUMBER}/${artifact_name}"
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
}

def notifySuccessful() {
    slackSend (color: '#00FF00', message: "SUCCESSFUL: Job '${JOB_NAME} [${BUILD_NUMBER}]' (${BUILD_URL})")
}
