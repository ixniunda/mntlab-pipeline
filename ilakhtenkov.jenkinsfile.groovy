import hudson.model.Run

def String exceptionMessage
try {
    node {
        def repositoryUrl = "https://github.com/MNT-Lab/mntlab-pipeline.git"
        def branch = "ilakhtenkov"

        stage('PREPARATION') {
            exceptionMessage = "PREPARARION FAILED"
            git branch: branch, url: repositoryUrl
        }
        stage('BUILD') {
            exceptionMessage = "BUILD FAILED"
            def rtGradle = Artifactory.newGradleBuild()
            rtGradle.tool = "gradle3.3"
            rtGradle.deployer repo:'ext-release-local', server: server
            rtGradle.resolver repo:'remote-repos', server: server
            buildInfo = rtGradle.run rootDir: "/", buildFile: 'build.gradle', tasks: 'clean build'
        }
        echo "Hello world"
    }
} catch(Exception ex) {
    println exceptionMessage
}

println("Let's move on after the exception");

