pipeline {
    agent any 

    stages {
        stage('Git checkout') { 
            steps { 
               echo "Stage 1"
               git branch: 'abandarovich', url: 'https://github.com/MNT-Lab/mntlab-pipeline.git'
            }
        }
        stage('Test'){
            steps {
                echo "Stage 2"
            }
        }
        stage('Deploy') {
            steps {
                echo "Stage 3"
            }
        }
    }
}
