import hudson.model.Run
import groovy.json.JsonOutput


node {
    def repositoryUrl = "https://github.com/MNT-Lab/mntlab-pipeline.gt"
    def branch = "ilakhtenkov"

    stage('PREPARATION') {
        try {
            git branch: branch, url: repositoryUrl
        }
        catch (Exception error){
            println("PREPARATION Failed")
            //throw error
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
            parallel {
                node {
                    sh "gradle test"
                }
                node {
                    sh "gradle jacocoTestReport"
                }
                node {
                    sh "gradle cucumber"
                }
            }
        }
        catch (Exception error){
            println("TEST Failed")
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
