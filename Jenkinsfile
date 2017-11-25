node{
    stage('Checkout'){
    checkout([$class: 'GitSCM', 
    branches: [[name: 'ataran']], 
    userRemoteConfigs: [[url: 'https://github.com/MNT-Lab/mntlab-pipeline.git']]
])
     stage('Build'){
         def gradleHome = tool 'gradle3.3'
        sh "gradle clean build"
    }
    }    
    
 







   
}
