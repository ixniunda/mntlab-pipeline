#!groovy

node {
    def gradle = tool "gradle3.3"
    env.PATH = "${gradle}/bin:${env.PATH}"
    stage('Git checkout') {
        echo "Stage 1" git branch: 'abandarovich', url: 'https://github.com/MNT-Lab/mntlab-pipeline.git'

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
    stage('Archive') {

        echo "Stage 3"
        archiveArtifacts 'build/libs/gradle-simple.jar'


    }
}

