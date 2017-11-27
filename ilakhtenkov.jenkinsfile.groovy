



node {
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
            throw error
        }
    }
    stage('BUILD') {
        try {
            sh "${GRADLE_HOME}/bin/gradle clean build"
        }
        catch (Exception error){
            println("BUILD Failed")
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
            throw error
        }
    }
    stage('PUBLISHING-RESULTS') {
        try {
            sh "tar -xzf  ${branch}_dsl_script.tar.gz"
            sh "tar -czf  pipeline-${branch}-${env.BUILD_NUMBER}.tar.gz ./dsl/dsl_script.groovy ./ilakhtenkov.jenkinsfile.groovy ./build/libs/gradle-simple.jar"
            archiveArtifacts 'pipeline-ilakhtenkov-*.tar.gz'
            //curl -v --user 'jenkins:jenkins' --upload-file "./pipeline-${branch}-${env.BUILD_NUMBER}.tar.gz" "http://epbyminw2033.minsk.epam.com:8081/repository/Artifact_storage/com/epam/mntlab/gradle-simple-${env.BUILD_NUMBER}/${env.BUILD_NUMBER}/gradle-simple-${env.BUILD_NUMBER}-${env.BUILD_NUMBER}tar.gz"
            sh "curl -v --user 'jenkins:jenkins' --upload-file './pipeline-${branch}-${env.BUILD_NUMBER}.tar.gz' 'http://nexus.local/repository/Artifact_storage/com/epam/mntlab/pipeline/gradle-simple-${env.BUILD_NUMBER}/${env.BUILD_NUMBER}/gradle-simple-${env.BUILD_NUMBER}-${env.BUILD_NUMBER}.tar.gz'"
        }
        catch (Exception error){
            println("PUBLISHING-RESULTS Failed")
            throw error
        }
    }
    stage('APPROVAL') {
        try {
            input message: 'Do you want to deploy?', ok: 'Yes'
        }
        catch (Exception error){
            println("APPROVAL Failed")
            throw error
        }
    }
    stage('DEPLOYING') {
        try {
            sh "java -jar build/libs/gradle-simple.jar"
            }
        catch (Exception error){
            println("DEPLOYING Failed")
            throw error
        }
    }
    stage('STATUS') {
        println "SUCCESS"
        postToSlack ("SUCCESS", "#general", "bot.ilakhtenkov")
    }
}
def postToSlack (String message, String channel, String userName) {
    def webhookUrl = "https://hooks.slack.com/services/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"
    sh "curl -X POST --data-urlencode \'payload={\"channel\": \"${channel}\", \"username\": \"${userName}\", \"text\": \"${message}\", \"icon_emoji\": \":chicken:\"}\' \"${webhookUrl}\""
}


/*def postToSlack(String url, String postContent) {
    def connection = url.toURL().openConnection()
    connection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded")
    connection.setRequestMethod("POST")
    connection.doOutput = true
    connection.outputStream.withWriter{
        it.write(postContent)
        it.flush()
    }
    connection.connect()

    try {
        connection.content.text
    } catch (IOException e) {
        try {
            ((HttpURLConnection)connection).errorStream.text
        } catch (Exception ignored) {
            throw e
        }
    }
}*/


/*def build (){
    def buildInfo
    def rtGradle = Artifactory.newGradleBuild()
    rtGradle.tool = "gradle3.3"
    rtGradle.deployer repo:'ext-release-local', server: server
    rtGradle.resolver repo:'remote-repos', server: server
    buildInfo = rtGradle.run rootDir: "/", buildFile: 'build.gradle', tasks: 'clean build'
}*/
