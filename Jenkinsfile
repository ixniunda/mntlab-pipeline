#!groovy

node {
    def gradle = tool "gradle3.3"
    env.PATH = "${gradle}/bin:${env.PATH}"
    def BRANCH_NAME = 'abandarovich'
    stage('Git checkout') {
        echo "Stage 1"
        git branch: 'abandarovich', url: 'https://github.com/MNT-Lab/mntlab-pipeline.git'

    }
    stage('Build') {

        sh "gradle build"
    }
    stage('Test') {
        parallel 'Cucumber Tests': {
            sh "gradle cucumber"
        },
                'Jacoco Tests': {
                    sh "gradle jacocoTestReport"
                },
                'Unit Tests': {
                    sh "gradle jacocoTestReport"
                }
    }
    stage('Trigger') {
        build job: "MNTLAB-abandarovich-child1-build-job", parameters: [[$class: 'StringParameterValue', name: 'BRANCH_NAME', value: 'abandarovich']], wait: true
    }
    stage('Package') {
        copyArtifacts(projectName: "MNTLAB-abandarovich-child1-build-job", filter: '*dsl_script.tar.gz')
        archiveArtifacts 'build/libs/gradle-simple.jar, *dsl_script.tar.gz'
    }
    stage('Approval') {
     //   input 'Are you sure?'
    }
    stage('Deployment') {
        //    sh "java -jar gradle-simple.jar"
    }
    stage('Sending status') {
        echo "Succeeded"
    }
}

