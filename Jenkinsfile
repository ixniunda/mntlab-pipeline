def notifySlack(status, extra = "") {
def header = "*${status}*: Job *${env.JOB_NAME}* [${env.BUILD_NUMBER}]\n  *job*: ${env.BUILD_URL}"
def colors = [
    'STARTED': 'warning',
    'FAILED': 'danger',
    'SUCCESSFUL': 'good'
]
slackSend(
    teamDomain: 'Rupert-',
    channel: '#jenkins-kadiara',
    tokenCredentialId: 'xoxp-277200285029-277282124433-277286186065-99d789f1583cb1fe3e0c423486a14443',
    color: colors[status],
    message: "${header}\n${extra}"
)
}

node () {

  def GRADLE_HOME="${tool 'gradle 4.3'}"

  environment {
    env.JAVA_HOME="${tool 'jdk-8u51'}"
    env.PATH="${env.JAVA_HOME}/bin:${env.PATH}"
  }


  	 stage('Preparation') {
    	    try {
             checkout  changelog: false, poll: false, scm: [$class: 'GitSCM', branches: [[name: '*/adudko']],
                        doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [],
                        userRemoteConfigs: [[url: 'https://github.com/MNT-Lab/mntlab-pipeline.git']]]
          }

         catch (e) {
            notifySlack('FAILED', "```stage: ${stage}```")
            throw e
         }
     }

    stage('Building code') {

    	     try {
    	          sh "'${GRADLE_HOME}/bin/gradle' build"
    	     }

         catch (e) {
            notifySlack('FAILED', "```stage: ${stage}```")
            throw e
         }
    }

    stage('Testing code') {
        try {
       parallel (
          cucumber : { sh "'${GRADLE_HOME}/bin/gradle' cucumber" },
          jacoco   : { sh "'${GRADLE_HOME}/bin/gradle' jacocoTestReport" },
          unit     : { sh "'${GRADLE_HOME}/bin/gradle' test" }
          )
        }

     catch (e) {
        notifySlack('FAILED', "```stage: ${stage}```")
        throw e
      }
    }

    stage('Triggering job and fetching artefact after finishing') {

           try {
             build job: 'EPBYMINW2472/MNTLAB-adudko-child1-build-job', parameters: [string(name: 'BRANCH_NAME', value: 'adudko')]
             copyArtifacts(projectName: 'EPBYMINW2472/MNTLAB-adudko-child1-build-job', filter: '*dsl_script.tar.gz');
           }

           catch (e) {
              notifySlack('FAILED', "```stage: ${stage}```")
              throw e
           }
      }

      stage('Packaging and Publishing results') {

           try {
             sh "tar -cvzf pipeline-adudko-'$BUILD_NUMBER'.tar.gz dsl.groovy Jenkinsfile build/libs/gradle-simple.jar"
             archiveArtifacts 'pipeline-adudko-${BUILD_NUMBER}.tar.gz'
             sh "curl -v --user 'admin:admin123' --upload-file './pipeline-adudko-${BUILD_NUMBER}.tar.gz' 'http://nexus/repository/project-releases/asd/name2-2/2/name2-2-2.tar.gz'"
           }

           catch (e) {
              notifySlack('FAILED', "```stage: ${stage}```")
              throw e
           }
      }

      // stage('Asking for manual approva') {
      //
      //      try {
      //
      //      }
      //
      //      catch (e) {
      //         notifySlack('FAILED', "```stage: ${stage}```")
      //         throw e
      //      }
      // }
      //
      // stage('Deployment') {
      //
      //      try {
      //
      //      }
      //
      //      catch (e) {
      //         notifySlack('FAILED', "```stage: ${stage}```")
      //         throw e
      //      }
      // }
      //
      // stage('Sending status') {
      //
      //      try {
      //
      //      }
      //
      //      catch (e) {
      //         notifySlack('FAILED', "```stage: ${stage}```")
      //         throw e
      //      }
      // }

}
