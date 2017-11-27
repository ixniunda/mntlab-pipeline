#!groovy

node {
 def gradle = tool "gradle3.3"
 env.PATH = "${gradle}/bin:${env.PATH}"
 def BRANCH_NAME = 'abandarovich'
 def JAR = "build/libs/gradle-simple.jar"
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
  build job: "MNTLAB-abandarovich-child1-build-job", parameters: [
   [$class: 'StringParameterValue', name: 'BRANCH_NAME', value: 'abandarovich']
  ], wait: true
 }
 stage('Package') {
  copyArtifacts(projectName: "MNTLAB-abandarovich-child1-build-job", filter: '*dsl_script.tar.gz')
  archiveArtifacts 'Jenkinsfile,' + JAR + ',*dsl_script.tar.gz'
  sh "tar -cvzf pipeline-abandarovich-${BUILD_NUMBER}.tar.gz Jenkinsfile *dsl_script.tar.gz"
  sh "curl -v --user 'admin:12345678' --upload-file ./pipeline-abandarovich-${BUILD_NUMBER}.tar.gz http://nexus/repository/task11/com.github.jitpack/pipeline-abandarovich/${BUILD_NUMBER}/pipeline-abandarovich-${BUILD_NUMBER}.tar.gz"
 }
 stage('Approval') {
  //   input 'Are you sure?'
 }
 stage('Deployment') {
  sh "java -jar " + JAR
 }
 stage('Sending status') {
  echo "Succeeded"
 }
}
