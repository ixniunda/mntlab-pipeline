node {
   def grdHome
   stage('Preparation') { // for display purposes
      // Get some code from a GitHub repository
      checkout([$class: 'GitSCM', branches: [[name: '*/amakhnach']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/MNT-Lab/mntlab-pipeline.git']]])
   }
   
   stage('Build') {
      // Run the gradle build
      grdHome = tool 'gradle3.3'
      sh "'${grdHome}/bin/gradle' build"
   }
   //stage('Results') {
    //  junit '**//*target/surefire-reports/TEST-*.xml'
      //archive 'target/*.jar'
   //}
}
