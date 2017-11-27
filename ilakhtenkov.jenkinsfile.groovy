node ("EPBYMIN2033") {
    def repositoryUrl = "https://github.com/MNT-Lab/mntlab-pipeline.git"
    def branch = "ilakhtenkov"
    def GRADLE_HOME = tool name: 'gradle3.3', type: 'hudson.plugins.gradle.GradleInstallation'

    stage('PREPARATION') {
        try {
            step([$class: 'WsCleanup'])
            git branch: branch, url: repositoryUrl
        }
        catch (Exception error){
            println ("PREPARATION Failed")
            postToSlack ("${env.BUILD_TAG} PREPARATION Failed", "#general", "bot.ilakhtenkov")
            throw error
        }
    }
    stage('BUILD') {
        try {
            sh "${GRADLE_HOME}/bin/gradle build"
        }
        catch (Exception error){
            println("BUILD Failed")
            postToSlack ("${env.BUILD_TAG} BUILD Failed. See details:${env.BUILD_URL}", "#general", "bot.ilakhtenkov")
            throw error
        }
    }
    stage('TEST') {
        try {
            parallel junitTest: {
                sh "${GRADLE_HOME}/bin/gradle test"
            }, jacocoTest: {
                sh "${GRADLE_HOME}/bin/gradle jacocoTestReport"
            }, cucumberTest: {
                sh "${GRADLE_HOME}/bin/gradle cucumber"
            }
        }
        catch (Exception error){
            println("TEST Failed")
            postToSlack ("${env.BUILD_TAG} TEST Failed", "#general", "bot.ilakhtenkov")
            throw error
        }
    }
    stage('TRIGGER-CHILD') {
        try {
            build job: 'EPBYMINW2033/MNTLAB-ilakhtenkov-child1-build-job', parameters: [[$class: 'StringParameterValue', name: 'BRANCH_NAME', value: branch]], wait: true
            copyArtifacts(projectName: 'EPBYMINW2033/MNTLAB-ilakhtenkov-child1-build-job', filter: '*_dsl_script.tar.gz')
        }
        catch (Exception error){
            println("TRIGGER-CHILD Failed")
            postToSlack ("${env.BUILD_TAG} TRIGGER-CHILD Failed", "#general", "bot.ilakhtenkov")
            throw error
        }
    }
    stage('PUBLISHING-RESULTS') {
        try {
            sh "tar -xzf  ${branch}_dsl_script.tar.gz"
            sh "tar -czf  pipeline-${branch}-${env.BUILD_NUMBER}.tar.gz ./dsl/dsl_script.groovy ./ilakhtenkov.jenkinsfile.groovy ./build/libs/gradle-simple.jar"
            archiveArtifacts 'pipeline-ilakhtenkov-*.tar.gz'
            sh "curl -v --user 'jenkins:jenkins' --upload-file './pipeline-${branch}-${env.BUILD_NUMBER}.tar.gz' 'http://10.6.205.59:8081/repository/Artifact_storage/com/epam/mntlab/pipeline/gradle-simple-${env.BUILD_NUMBER}/${env.BUILD_NUMBER}/gradle-simple-${env.BUILD_NUMBER}-${env.BUILD_NUMBER}.tar.gz'"
            //sh "curl -v --user 'jenkins:jenkins' --upload-file './pipeline-${branch}-${env.BUILD_NUMBER}.tar.gz' 'http://nexus.local/repository/Artifact_storage/com/epam/mntlab/pipeline/gradle-simple-${env.BUILD_NUMBER}/${env.BUILD_NUMBER}/gradle-simple-${env.BUILD_NUMBER}-${env.BUILD_NUMBER}.tar.gz'"
        }
        catch (Exception error){
            println("PUBLISHING-RESULTS Failed")
            postToSlack ("${env.BUILD_TAG} PUBLISHING-RESULTS Failed", "#general", "bot.ilakhtenkov")
            throw error
        }
    }
    stage('APPROVAL') {
        try {
            input message: 'Do you want to deploy?', ok: 'Yes'
        }
        catch (Exception error){
            println("APPROVAL Failed")
            postToSlack ("${env.BUILD_TAG} APPROVAL Failed", "#general", "bot.ilakhtenkov")
            throw error
        }
    }
    stage('DEPLOYING') {
        try {
            sh "java -jar build/libs/gradle-simple.jar"
            }
        catch (Exception error){
            println("DEPLOYING Failed")
            postToSlack ("${env.BUILD_TAG} DEPLOYING failed", "#general", "bot.ilakhtenkov")
            throw error
        }
    }
    stage('STATUS') {
        println "SUCCESS"
        postToSlack ("${env.BUILD_TAG} Successfully deployed. See details:${env.BUILD_URL} ", "#general", "bot.ilakhtenkov")
    }
}

def postToSlack (String message, String channel, String userName) {
    def webhookUrl = "https://hooks.slack.com/services/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"
    sh "curl -X POST --data-urlencode \'payload={\"channel\": \"${channel}\", \"username\": \"${userName}\", \"text\": \"${message}\", \"icon_emoji\": \":chicken:\"}\' \"${webhookUrl}\""
}
