node {
    def gradle_home = tool 'gradle3.3'
    def java_home = tool 'java8'
    def branch_name = 'ivauchok'
    def folder_name = 'EPBYMINW2473'
    def artifact_name = "pipeline-${branch_name}-${BUILD_NUMBER}.tar.gz"

    stage('Preparation (Checking out)') {
        try {
            checkout scm: [$class: 'GitSCM', branches: [[name: "*/${branch_name}"]], userRemoteConfigs: [[url: 'https://github.com/MNT-Lab/mntlab-pipeline.git']]]
        }
        catch (Exception e){
            slack_notification ("${BUILD_TAG} Failed <Preparation (Checking out)> stage", "#pipeline", "ivauchok")
            throw e
        }
    }
    stage('Building code') {
        try {
            sh "${gradle_home}/bin/gradle clean build"
        }
        catch (Exception e){
            slack_notification ("${BUILD_TAG} Failed <Building code> stage", "#pipeline", "ivauchok")
            throw e
        }
    }
    stage('Testing code') {
        try {
            parallel (
                    'Cucumber tests': {sh "${gradle_home}/bin/gradle cucumber"},
                    'Jacoco Tests': {sh "${gradle_home}/bin/gradle jacocoTestReport"},
                    'Unit Tests': {sh "${gradle_home}/bin/gradle test"})
        }
        catch (Exception e){
            slack_notification ("${BUILD_TAG} Failed <Testing code> stage", "#pipeline", "ivauchok")
            throw e
        }
    }
    stage('Triggering job and fetching artefact after finishing') {
        try {
            build job: "${folder_name}/MNTLAB-${branch_name}-child1-build-job", parameters: [[$class: 'StringParameterValue', name: 'BRANCH_NAME', value: "${branch_name}"]], wait: true
            copyArtifacts(projectName: "${folder_name}/MNTLAB-${branch_name}-child1-build-job", filter: '*dsl_script.tar.gz')
        }
        catch (Exception e){
            slack_notification ("${BUILD_TAG} Failed <Triggering job and fetching artefact after finishing> stage", "#pipeline", "ivauchok")
            throw e
        }
    }
    stage('Packaging and Publishing results') {
        try {
            sh "tar -zxvf ${branch_name}_dsl_script.tar.gz && cp build/libs/gradle-simple.jar gradle-simple.jar && tar -czf pipeline-${branch_name}-${BUILD_NUMBER}.tar.gz jobs.groovy Jenkinsfile.groovy gradle-simple.jar"
            archiveArtifacts "${artifact_name}"

            //Test from remote Jenkins server to Nexus server on Alexandr_Taran machine:
            sh "curl -v --user 'admin:admin123' --upload-file ${artifact_name} http://epbyminw6405:8081/repository/maven-prod/pipeline/pipeline-${branch_name}/${BUILD_NUMBER}/${artifact_name}"

            //Test from remote Jenkins server to Nexus server on my laptop:
            //sh "curl -v --user 'nexus-service-user:123456' --upload-file ${artifact_name} http://10.6.154.84:8081/repository/project-releases/pipeline/pipeline-${branch_name}/${BUILD_NUMBER}/${artifact_name}"

            //Test from local Jenkins server to Nexus server on my laptop:
            //sh "curl -v --user 'nexus-service-user:123456' --upload-file ${artifact_name} http://nexus/repository/project-releases/pipeline/pipeline-${branch_name}/${BUILD_NUMBER}/${artifact_name}"
        }
        catch (Exception e){
            slack_notification ("${BUILD_TAG} Failed <Packaging and Publishing results> stage", "#pipeline", "ivauchok")
            throw e
        }
    }
    stage('Asking for manual approval') {
        try {
            input 'Do you want to deploy gradle-simple.jar?'
        }
        catch (Exception e){
            slack_notification ("${BUILD_TAG} Failed <Asking for manual approval> stage", "#pipeline", "ivauchok")
            throw e
        }
    }
    stage('Deployment') {
        try {
            sh "java -jar gradle-simple.jar"
        }
        catch (Exception e){
            slack_notification ("${BUILD_TAG} Failed <Deployment> stage", "#pipeline", "ivauchok")
            throw e
        }
    }
    stage('Sending status') {
        slack_notification ("${BUILD_TAG} Success deployment!!!", "#pipeline", "ivauchok")
    }
}

def slack_notification (message, channel, username) {
    def slack_url = "https://hooks.slack.com/services/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"
    sh "curl -X POST --data-urlencode \'payload={\"channel\": \"${channel}\", \"username\": \"${username}\", \"text\": \"${message}\", \"icon_emoji\": \":ghost:\"}\' \"${slack_url}\""
}
