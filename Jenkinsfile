#!groovy

node {
    def gradle = tool "gradle3.3"
    env.PATH = "${gradle}/bin:${env.PATH}"
    def BRANCH_NAME = 'abandarovich'
    def JAR = "build/libs/gradle-simple.jar"
    def CHILD_JOB = "MNTLAB-abandarovich-child1-build-job"
    def ARTIFACT = "pipeline-abandarovich-${BUILD_NUMBER}.tar.gz"
    def CHILD_ARTIFACT = "*dsl_script.tar.gz"
    try {
        stage('Git checkout') {
            git branch: 'abandarovich', url: 'https://github.com/MNT-Lab/mntlab-pipeline.git'
        }
    }
    catch (Exception ex) {
        echo "Git checkout failed"
        ex.printStackTrace()
    }
    try {
        stage('Build') {
            sh "gradle build"
        }
    }
    catch (Exception ex) {
        echo "Build failed"
        ex.printStackTrace()
    }

    try {
        stage('Test') {
            parallel 'Cucumber Tests': {
                sh "gradle cucumber"
            },
                    'Jacoco Tests': {
                        sh "gradle jacocoTestReport"
                    },
                    'Unit Tests': {
                        sh "gradle test"
                    }
        }
    }
    catch (Exception ex) {
        echo "Tests failed"
        ex.printStackTrace()
    }
    try {
        stage('Trigger') {
            build job: CHILD_JOB, parameters: [
                    [$class: 'StringParameterValue', name: 'BRANCH_NAME', value: 'abandarovich']
            ], wait: true
        }
    }
    catch (Exception ex) {
        echo "Trigger failed"
        ex.printStackTrace()
    }
    try {
        stage('Package') {
            copyArtifacts(projectName: CHILD_JOB, filter: CHILD_ARTIFACT)
            archiveArtifacts 'Jenkinsfile,' + JAR + ',' + CHILD_ARTIFACT
            sh "tar -cvzf " + ARTIFACT + " Jenkinsfile " + CHILD_ARTIFACT
            sh "curl -v --user 'admin:12345678' --upload-file ./" + ARTIFACT + " http://nexus/repository/task11/com.github.jitpack/pipeline-abandarovich/${BUILD_NUMBER}/" + ARTIFACT
        }
    }
    catch (Exception ex) {
        echo "Packaging failed"
        ex.printStackTrace()
    }
    stage('Approval') {
        input 'Are you sure want to Deploy ' + JAR + '?'
    }
    try {
        stage('Deployment') {
            sh "java -jar " + JAR
        }
    }
    catch (Exception ex) {
        echo "Deployment failed"
        ex.printStackTrace()
    }

    stage('Sending status') {
        echo "Succeeded"
    }
}
