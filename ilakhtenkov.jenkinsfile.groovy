import hudson.model.Run
import groovy.json.JsonOutput


node (master) {
    def repositoryUrl = "https://github.com/MNT-Lab/mntlab-pipeline.git"
    def branch = "ilakhtenkov"

    stage('PREPARATION') {
        try {
            step([$class: 'WsCleanup'])
            git branch: branch, url: repositoryUrl
        }
        catch (Exception error){
            println("PREPARATION Failed")
            throw error
        }
    }
    stage('BUILD') {
        try {
            sh "gradle clean build"
        }
        catch (Exception error){
            println("BUILD Failed")
            throw error
        }
    }
    stage('TEST') {
        try {
            parallel junitTest: {
                sh "gradle test"
            }, jacocoTest: {
                sh "gradle jacocoTestReport"
            }, cucumberTest: {
                sh "gradle cucumber"
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
            /*step (
                    [$class: 'CopyArtifact',
                     filter: '*_dsl_script.tar.gz',
                     projectName: 'EPBYMINW2033/MNTLAB-ilakhtenkov-child1-build-job',
                     selector: [$class: 'MultiJobBuildSelector']]
            )*/
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
            sh "curl -v --user 'jenkins:jenkins' --upload-file './pipeline-${branch}-${env.BUILD_NUMBER}.tar.gz' 'http://nexus.local/repository/Artifact_storage/com/epam/mntlab/gradle-simple-${env.BUILD_NUMBER}/${env.BUILD_NUMBER}/gradle-simple-${env.BUILD_NUMBER}-${env.BUILD_NUMBER}tar.gz'"
        }
        catch (Exception error){
            println("PUBLISHING-RESULTS")
            throw error
        }
    }
}


def build (){
    def buildInfo
    def rtGradle = Artifactory.newGradleBuild()
    rtGradle.tool = "gradle3.3"
    rtGradle.deployer repo:'ext-release-local', server: server
    rtGradle.resolver repo:'remote-repos', server: server
    buildInfo = rtGradle.run rootDir: "/", buildFile: 'build.gradle', tasks: 'clean build'
}

/*def notifySlack(text, channel) {
    def slackURL = 'https://slack.com/api/rtm.'
    def payload = JsonOutput.toJson([text      : text,
                                     channel   : channel,
                                     username  : "jenkins",
                                     icon_emoji: ":jenkins:"])
    sh "curl -X POST --data-urlencode \'payload=${payload}\' ${slackURL}"
}*/