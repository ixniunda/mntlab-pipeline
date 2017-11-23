node {
    def repositoryUrl = "https://github.com/MNT-Lab/mntlab-pipeline.git"
    def branch = "ilakhtenkov"

    stage('Clone sources') {
        git branch: branch, url: repositoryUrl
    }
    stage('Artifactory configuration') {
        rtGradle.tool = "gradle3.3"
        //rtGradle.deployer repo:'ext-release-local', server: server
        //rtGradle.resolver repo:'remote-repos', server: server
        buildInfo = rtGradle.run rootDir: "/", buildFile: 'build.gradle', tasks: 'clean build'
    }

    echo "Hello world"
}

