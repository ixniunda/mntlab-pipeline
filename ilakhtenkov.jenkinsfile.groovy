import hudson.model.Run

node {
    def repositoryUrl = "https://github.com/MNT-Lab/mntlab-pipeline.git"
    def branch = "ilakhtenkov"

    stage('PREPARATION') {
        try {
            git branch: branch, url: repositoryUrl
        }
        catch (Exception error){
            println("PREPARATION Failed")
            throw error
        }
    }
    stage('BUILD') {
        try {
            sh "./gradle clean build"
        }
        catch (Exception error){
            println("BUILD Failed")
            throw error
        }
    }
    echo "Hello world"
}


def build (){
    def buildInfo
    def rtGradle = Artifactory.newGradleBuild()
    rtGradle.tool = "gradle3.3"
    rtGradle.deployer repo:'ext-release-local', server: server
    rtGradle.resolver repo:'remote-repos', server: server
    buildInfo = rtGradle.run rootDir: "/", buildFile: 'build.gradle', tasks: 'clean build'
}
