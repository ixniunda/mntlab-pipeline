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
stage('Build'){
            steps {
                echo "Stage 3"
                 parallel 'Cucumber Tests':{
                 node{
                 sh "gradle cucumber"    
                 }
                 }, 'Jacoco Tests':{
                 node{
                 sh "gradle jacocoTestReport"      
                 }
                },
                'Unit Tests':{
                 node{
                 sh "gradle jacocoTestReport"      
                 }
                }
            }
        }        
        stage('Archive') {
            steps {
                echo "Stage 4"
                archiveArtifacts 'build/libs/gradle-simple.jar'

            }
        }
    }
}

