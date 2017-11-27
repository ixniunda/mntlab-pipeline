
node {
    def gradleHome
    gradleHome = tool 'gradle3.3'
    env.PATH = "${gradleHome}/bin:${env.PATH}"
    def javaHome
    javaHome = tool 'java8'
    
   stage('Preparation') { // for display purposes
      // Get some code from a GitHub repository
      git([url: 'https://github.com/MNT-Lab/mntlab-pipeline.git', branch: 'ykaratseyeu'])
   }
   stage ('Building code') {
        sh "gradle build"
   }
   stage ('Testing code'){
       
   }
}
