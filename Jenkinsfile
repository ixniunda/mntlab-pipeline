//Import
import hudson.model.*
import jenkins.model.*

//Configuration
confGit = "https://github.com/MNT-Lab/mntlab-pipeline.git"
confBranch = "amurzich"
confJob = "EPBYMINW3088/MNTLAB-amurzich-child1-build-job"


node {
    stage("Preparation") {
        try {
            git branch: confBranch, url: confGit
        }
        catch (Exception error) {
            println("Can't get $confBranch branch from $confGit repository.")
        }
    }
    stage("Build") {
        try {
            gradle build
        }
        catch (Exception error) {
            println("Can't build $confGit/$confBranch project.")
        }
    }
    stage("Test") {
        try {
            parallel a: { sh "/opt/gradle/bin/gradle test" },
                b: { sh "/opt/gradle/bin/gradle jacocoTestReport" },
                c: { sh "/opt/gradle/bin/gradle cucumber" }
        }
        catch (Exception error) {
            println("Tests failed.")
        }
    }
    stage("ChildJob") {
        try {
            build job: confJob,
                parameters: [[$class: 'StringParameterValue', name: 'BRANCH_NAME', value: confBranch]],
                wait: true
            copyArtifacts(projectName: confJob, filter: "*-dsl-${BUILD_NUMBER}.tar.gz")
        }
        catch (Exception error) {
            println("Job $confJob failed.")
        }
    }
    stage("Publishing") {
        try {
            sh "tar -xvzf artifact-dsl-${BUILD_NUMBER}.tar.gz"
            sh "tar -cvzf pipeline-${confBranch}-${BUILD_NUMBER}.tar.gz ./dsl.groovy ./Jenkinsfile ./build/libs/gradle-simple.jar"
            sh "curl -v -u jenkins:jenkins --upload-file ./pipeline-${confBranch}-${BUILD_NUMBER}.tar.gz http://epbyminw3088:8081/repository/maven-custom/"
        }
        catch (Exception error) {
            println("Publishing failed.")
        }
    }
    stage("Approval") {
        try {
            input message: "Do you approve deployment of artifact: gradle-simple.jar?", ok: "y"
        }
        catch (Exception error) {
            println("Approval failed.")
        }
    }
    stage("Deployment") {
        try {
            sh "java -jar build/libs/gradle-simple.jar"
        }
        catch (Exception error) {
            println("Can't deploy build/libs/gradle-simple.jar")
        }
        finally {
            sh 'echo "SUCCESS"'
        }
    }
}