node('EPBYMINW1766') {
    try{
   def grdHome = tool 'gradle3.3'
   //def javaHome = tool 'java8'
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
        sh 'cp ../MNTLAB-amakhnach-child1-build-job/amakhnach_dsl_script.tar.gz ./;tar -xvf amakhnach_dsl_script.tar.gz'
   }
   
   stage('Packaging and Publishing results') {
       sh "tar -cvzf pipeline-amakhnach-'$BUILD_NUMBER'.tar.gz dsl.groovy Jenkinsfile build/libs/gradle-simple.jar"
       archiveArtifacts 'pipeline-amakhnach-${BUILD_NUMBER}.tar.gz'
       sh "groovy -DVersionID='$BUILD_NUMBER' upload.groovy"
   }
   
   stage('Asking for manual approval') {
       input 'Proceed/Abort'
   }
   
   stage('Deployment') {
       sh "java -jar build/libs/gradle-simple.jar"
   }
   
   stage('Sending status') {
       currentBuild.result = 'SUCCESS'
   }
    } catch(Exception err){
        currentBuild.result = 'FAILURE'
    }
    echo "RESULT: ${currentBuild.result}"
    
}
