node {
   def grdHome = tool 'gradle3.3'
   stage('Preparation') { // for display purposes
      // Get some code from a GitHub repository
      checkout([$class: 'GitSCM', branches: [[name: '*/amakhnach']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/MNT-Lab/mntlab-pipeline.git']]])
   }
   
   stage('Build') {
      // Run the gradle build
      
      sh "'${grdHome}/bin/gradle' build"
   }
   //stage('Results') {
    //  junit '**//*target/surefire-reports/TEST-*.xml'
      //archive 'target/*.jar'
   //}
   stage('Testing Code') {
       parallel (
           
           cucumber: {
               sh "'${grdHome}/bin/gradle' cucumber"
           },
           jacoco: {
               sh "'${grdHome}/bin/gradle' jacocoTestReport"
           },
           unit: {
               sh "'${grdHome}/bin/gradle' test"
           }
       )
   }
   
   stage('Triggering job and fetching artefact after finishing') {
       build job: 'MNTLAB-amakhnach-child1-build-job', parameters: [string(name: 'BRANCH_NAME', value: 'amakhnach')]

   }
}
