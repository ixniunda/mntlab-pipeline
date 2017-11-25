#!groovy

pipeline {
    agent any 
tools {
     gradle "gradle3.3"
     }
    stages {
        stage('Git checkout') { 
            steps { 
               echo "Stage 1"
               git branch: 'abandarovich', url: 'https://github.com/MNT-Lab/mntlab-pipeline.git'
            }
        }
        stage('Build'){
            steps {
                echo "Stage 2"
                sh "gradle build"
            }
        }
        stage('Archive') {
            steps {
                echo "Stage 3"
                archiveArtifacts 'build/libs/gradle-simple.jar'

            }
        }
    }
}
