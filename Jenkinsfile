def giturl='https://github.com/MNT-Lab/mntlab-pipeline.git'
def BRANCH_NAME='*/uhramovich'
node ('EPBYMINW2467'){
    stage('Preparation'){
try{checkout([$class: 'GitSCM', branches: [[name: BRANCH_NAME]], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: giturl]]])}
catch(Exception e){
                echo "GITSCM failed"
        sh  ''' curl -X POST -H 'Content-type: application/json' --data '{"text": "GITSCM failed"}' https://hooks.slack.com/services/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 '''
    System.exit(1);
            }
}
    stage('Build code'){
    mvnHome = tool 'gradle3.3'
    try{sh "'${mvnHome}/bin/gradle' build"}
    catch(Exception e){
        echo "build failed"
        sh  ''' curl -X POST -H 'Content-type: application/json' --data '{"text": "Build failed"}' https://hooks.slack.com/services/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 '''
    System.exit(1);
        
    }
    }   
    stage('Testing code'){
        parallel(
           
           cucumber: { try{sh "'${mvnHome}/bin/gradle' cucumber"} catch(Exception e){
                echo "build failed"
        sh  ''' curl -X POST -H 'Content-type: application/json' --data '{"text": "cucumber failed"}' hhttps://hooks.slack.com/services/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 '''
    System.exit(1);
            }}
            ,
           
            junit: {try{sh "'${mvnHome}/bin/gradle' test"}catch(Exception e){
                echo "build failed"
        sh  ''' curl -X POST -H 'Content-type: application/json' --data '{"text": "junit failed"}' https://hooks.slack.com/services/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 '''
    System.exit(1);
            }},
         
            jacoco: {try{sh "'${mvnHome}/bin/gradle' jacocoTestReport"}catch(Exception e){
                echo "build failed"
        sh  ''' curl -X POST -H 'Content-type: application/json' --data '{"text": "jacoco failed"}' https://hooks.slack.com/services/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 '''
    System.exit(1);
            }}
            
            
            )
    }
    try{stage('Triggering job and fetching artefact after finishing'){
        def job = build job: 'MNTLAB-uhramovich-child1-build-job' ,parameters: [string(name: 'BRANCH_NAME', value: 'uhramovich')]
        copyArtifacts(projectName: 'EPBYMINW2467/MNTLAB-uhramovich-child1-build-job', filter: '*dsl_script.tar.gz');
        //sh "cp $JENKINS_HOME/workspace/MNTLAB-uhramovich-child1-build-job/uhramovich_dsl_script.tar.gz $JENKINS_HOME/workspace/pipeline-job/"
    }}
	catch(Exception e){
                echo "build failed"
        sh  ''' curl -X POST -H 'Content-type: application/json' --data '{"text": "TTriggering job and fetching artefact stage failed"}' https://hooks.slack.com/services/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 '''
    System.exit(1);
            }


    try{stage('Packaging and Publishing results'){
        //sh "cd $JENKINS_HOME/workspace/EPBYMINW2467/pipeline-job/"
        sh "tar zxvf uhramovich_dsl_script.tar.gz"
        //sh "cp build/libs/gradle-simple.jar . "
        sh "tar -czvf pipeline-uhramovich-'$BUILD_NUMBER'.tar.gz jobs.groovy build/libs/gradle-simple.jar Jenkinsfile"
        archiveArtifacts artifacts: 'pipeline-uhramovich-${BUILD_NUMBER}.tar.gz'
        sh "curl -v --user 'admin:admin123' --upload-file pipeline-uhramovich-'$BUILD_NUMBER'.tar.gz http://10.6.205.47:8081//repository/maventask-release/pipeline-uhramovich-'$BUILD_NUMBER'.tar.gz"
        //nexusArtifactUploader artifacts: [[artifactId: 'pipeline-uhramovich-${BUILD_NUMBER}', classifier: '', file: 'pipeline-uhramovich-${BUILD_NUMBER}.tar.gz', type: 'tar.gz']], credentialsId: '7f29ad7d-7922-461e-a412-e23a6f428d79', groupId: 'pipe', nexusUrl: '50.50.50.50:8081', nexusVersion: 'nexus3', protocol: 'http', repository: 'maventask-release', version: '$BUILD_NUMBER'

    }}
catch(Exception e){
                echo "build failed"
        sh  ''' curl -X POST -H 'Content-type: application/json' --data '{"text": "Packaging and Publishing stage failed"}' https://hooks.slack.com/services/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 '''
    System.exit(1);
            }


  try{stage('Asking for approval'){  
  input message: 'Do you want to proceed to the Deployment?' 
  }}
catch(Exception e){
                echo "build failed"
        sh  ''' curl -X POST -H 'Content-type: application/json' --data '{"text": "Deployment approval is declined"}' https://hooks.slack.com/services/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 '''
    System.exit(1);
            }

  try{stage('Deployment'){
      sh "java -jar build/libs/gradle-simple.jar"
  }}
catch(Exception e){
                echo "build failed"
        sh  ''' curl -X POST -H 'Content-type: application/json' --data '{"text": "Deployment stage failed"}' https://hooks.slack.com/services/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 '''
    System.exit(1);
            }
  stage('Sending status'){
    sh  ''' curl -X POST -H 'Content-type: application/json' --data '{"text": "Application has been deployed!"}' https://hooks.slack.com/services/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 '''
  }
}
