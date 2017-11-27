
de {
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
       parallel(
          cucumber:{ sh "gradle cucumber"},
          jacocoTestReport:{ sh "gradle jacocoTestReport"},
          test:{ sh "gradle test"}
       )
   }
   stage ('Triggering job and fetching artefact after finishing'){
       build job: 'MNTLAB-ykaratseyeu-child1-build-job', parameters: [string(name: 'BRANCH_NAME', value: 'ykaratseyeu'), string(name: 'BRANCH_NAME', value: 'ykaratseyeu')]
   }
}
