#!groovy

node {
 def gradle = tool "gradle3.3"
 env.PATH = "${gradle}/bin:${env.PATH}"
 def BRANCH_NAME = 'abandarovich'
 def JAR = "build/libs/gradle-simple.jar"
 def CHILD_JOB="MNTLAB-abandarovich-child1-build-job"
 def ARTIFACT="pipeline-abandarovich-${BUILD_NUMBER}.tar.gz"
 def CHILD_ARTIFACT="*dsl_script.tar.gz"	
 stage('Git checkout') {
  git branch: 'abandarovich', url: 'https://github.com/MNT-Lab/mntlab-pipeline.git'
 }
 stage('Build') {
  sh "gradle build"
 }
 stage('Test') {
  parallel 'Cucumber Tests': {
    sh "gradle cucumber"
   },
   'Jacoco Tests': {
    sh "gradle jacocoTestReport"
   },
   'Unit Tests': {
    sh "gradle jacocoTestReport"
   }
 }

 stage('Trigger') {
  build job: CHILD_JOB, parameters: [
   [$class: 'StringParameterValue', name: 'BRANCH_NAME', value: 'abandarovich']
  ], wait: true
 }
 stage('Package') {
  copyArtifacts(projectName: CHILD_JOB, filter: CHILD_ARTIFACT)
  archiveArtifacts 'Jenkinsfile,' + JAR + ',' + CHILD_ARTIFACT
  sh "tar -cvzf "+ARTIFACT+" Jenkinsfile "+ CHILD_ARTIFACT
  sh "curl -v --user 'admin:12345678' --upload-file ./"+ARTIFACT+" http://nexus/repository/task11/com.github.jitpack/pipeline-abandarovich/${BUILD_NUMBER}/"+ARTIFACT
 }
 stage('Approval') {
  input 'Are you sure want to Deploy '+JAR+'?'
 }
 stage('Deployment') {
  sh "java -jar " + JAR
 }
 stage('Sending status') {
  echo "Succeeded"
 }
}
