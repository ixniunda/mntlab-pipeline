import hudson.model.Run

node {
    def repositoryUrl = "https://github.com/MNT-Lab/mntlab-pipeline.git"
    def branch = "ilakhtenkov"

    stage('PREPARATION') {
        git branch: branch, url: repositoryUrl
    }
    stage('BUILD') {
        try {
            build()
        }
        catch (error){
            println("BUILD Failed")
        }
    }
    echo "Hello world"
}


def build (){
    def rtGradle = Artifactory.newGradleBuild()
    rtGradle.tool = "gradle3.3"
    rtGradle.deployer repo:'ext-release-local', server: server
    rtGradle.resolver repo:'remote-repos', server: server
    buildInfo = rtGradle.run rootDir: "/", buildFile: 'build.gradle', tasks: 'clean build'
}