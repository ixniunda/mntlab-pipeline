node ('EPBYMINW1766') {
    //try{
   def grdHome = tool 'gradle3.3'
   def javaHome = tool 'java8'
   try{
   stage('Preparation') { // for display purposes
      // Get some code from a GitHub repository
      checkout([$class: 'GitSCM', branches: [[name: '*/amakhnach']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/MNT-Lab/mntlab-pipeline.git']]])
   } } catch (Exception err){
       sh """curl -X POST -H 'Content-type: application/json' --data '{"text":"Pipeline FAILED at Preparation step"}' https://hooks.slack.com/services/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"""
   }
   
   try{
   stage('Build') {
      // Run the gradle build
      
      sh "'${grdHome}/bin/gradle' build"
   } } catch (Exception err){
       sh """curl -X POST -H 'Content-type: application/json' --data '{"text":"Pipeline FAILED at Build step"}' https://hooks.slack.com/services/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"""
   }
   //stage('Results') {
    //  junit '**//*target/surefire-reports/TEST-*.xml'
      //archive 'target/*.jar'
   //}
   
   try{
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
   } } catch (Exception err){
       sh """curl -X POST -H 'Content-type: application/json' --data '{"text":"Pipeline FAILED at Testing Code step"}' https://hooks.slack.com/services/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"""
   }
   
   try{
   stage('Triggering job and fetching artefact after finishing') {
       build job: 'MNTLAB-amakhnach-child1-build-job', parameters: [string(name: 'BRANCH_NAME', value: 'amakhnach')]
        copyArtifacts(projectName: 'EPBYMINW1766/MNTLAB-amakhnach-child1-build-job', filter: '*dsl_script.tar.gz');
	sh 'tar -xvf amakhnach_dsl_script.tar.gz'
	//sh 'cp ../MNTLAB-amakhnach-child1-build-job/amakhnach_dsl_script.tar.gz ./;tar -xvf amakhnach_dsl_script.tar.gz'
   } } catch (Exception err){
       sh """curl -X POST -H 'Content-type: application/json' --data '{"text":"Pipeline FAILED at Triggering Other Job step"}' https://hooks.slack.com/services/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"""
   }
   
   try{
   stage('Packaging and Publishing results') {
       sh "tar -cvzf pipeline-amakhnach-'$BUILD_NUMBER'.tar.gz dsl.groovy Jenkinsfile build/libs/gradle-simple.jar"
       archiveArtifacts 'pipeline-amakhnach-${BUILD_NUMBER}.tar.gz'
       sh "/home/student/Downloads/groovy-2.4.12/bin/groovy -DVersionID='$BUILD_NUMBER' upload.groovy"
   } } catch (Exception err){
       sh """curl -X POST -H 'Content-type: application/json' --data '{"text":"Pipeline FAILED at Packaging and Publishing step"}' https://hooks.slack.com/services/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"""
   }
   
   try {
   stage('Asking for manual approval') {
       input 'Proceed/Abort'
   } } catch (Exception err){
       sh """curl -X POST -H 'Content-type: application/json' --data '{"text":"Pipeline FAILED at Deployment step"}' https://hooks.slack.com/services/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"""
   }
   
   try{
   stage('Deployment') {
       sh '"${javaHome}/java -jar build/libs/gradle-simple.jar"'
   } } catch (Exception err){
       sh """curl -X POST -H 'Content-type: application/json' --data '{"text":"Pipeline FAILED at Deployment step"}' https://hooks.slack.com/services/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"""
   }
   
   stage('Sending status') {
       currentBuild.result = 'SUCCESS'
       sh """curl -X POST -H 'Content-type: application/json' --data '{"text":"Pipeline SUCCEEDED"}' https://hooks.slack.com/services/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"""
   }
    //} catch(Exception err){
    //    currentBuild.result = 'FAILURE'
    //}
    //echo "RESULT: ${currentBuild.result}"
    
}
