// modules
import hudson.plugins.git.*
import hudson.model.*

def student='kzalialetdzinau'
def folder="EPBYMINW1969"
def trigged_job='MNTLAB-kzalialetdzinau-child1-build-job'
def artifact_name="${student}_dsl_script.tar.gz"


// paths
def path_to_file="workspace/$folder/$trigged_job"
def path_to_jar="build/libs/gradle-simple.jar"
def path_to_jenkinsfile="scripts/Jenkinsfile"
def path_to_arch="$JENKINS_HOME/jobs/$folder/jobs/$trigged_job/lastSuccessful/archive/$artifact_name"

// success or not success
success='SUCCESS'
fail='FAILURE'
stage_result=success

// notification
def send_to_slack (slack_message){
    sh "curl -X POST -H \'Content-type: application/json\' --data \'{\"text\":\"${slack_message}\"}\' https://hooks.slack.com/services/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"
}

def send_mail (send_message){
    mail bcc: '', body: "Error: $send_message", cc: '', from: '', replyTo: '', subject: '[FAILURE]', to: 'ferrus.gaffer@gmail.com'
}

def handle_exception (error) {
    stage_result=fail
    message="Stage name:"+stage_name+".Error:"+error.message
    send_to_slack(message)  
}

// magic
node('EPBYMINW1969') {
stage('Preparation') {
    
        stage_name='Preparation'
        cleanWs()
        try {
            checkout([$class: 'GitSCM', branches: [[name: "*/$student"]], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/MNT-Lab/mntlab-pipeline.git']]])

        }catch(Exception ERROR){           
            handle_exception(ERROR)
            throw ERROR
        }    
}

if (stage_result == success){
stage('Building code') {
    
        def gradle_home = tool name: 'gradle3.3', type: 'hudson.plugins.gradle.GradleInstallation' 
    stage_name='Building code'
        try {
            parallel ex_build: {sh "$gradle_home/bin/gradle build"},
                    ex_jar: {sh "$gradle_home/bin/gradle jar"}
        }catch (Exception ERROR){            
            handle_exception(ERROR)            
        } 
}}

if (currentBuild.currentResult == 'SUCCESS') {
    stage('Testing code'){
       
        def gradle_home = tool name: 'gradle3.3', type: 'hudson.plugins.gradle.GradleInstallation' 
            echo 'Testing stage'
        try{    
            parallel stream1: {sh "$gradle_home/bin/gradle cucumber"},
                     stream2: {sh "$gradle_home/bin/gradle jacocoTestReport"},
                     stream3: {sh "$gradle_home/bin/gradle test"}
        }catch(Exception ERROR){
        handle_exception(ERROR)
        }

        
}}

stage('Triggering seed job'){
    
    stage_name='Triggering seed job'
        try{
            job=build job: trigged_job, parameters: [string(name: 'BRANCH_NAME', value: student )], wait: true
            
        }catch(Exception ERROR){
        handle_exception(ERROR)
        }finally{
           // ws(path_to_file) {            
            //  def path_to_arch="$JENKINS_HOME/jobs/$folder/jobs/$trigged_job/lastSuccessful/archive/$artifact_name"       
              //  stash includes: "$path_to_arch" , name: 'artifact'
                step([  $class: 'CopyArtifact',
                        filter: "$artifact_name",
                        fingerprintArtifacts: true,
                        projectName: "$trigged_job",
            target: './',                       
                ])
         //   }        
}}

stage('Packaging and Publishing results') {
    
    stage_name='Packaging and Publishing results'
        sh "tar -cvf pipeline-${student}-${BUILD_NUMBER}.tar.gz $artifact_name $path_to_jar $path_to_jenkinsfile"
        sh 'ls -l'
        archive 'pipeline-${student}-${BUILD_NUMBER}.tar.gz'
        try {
            checkout([$class: 'GitSCM', branches: [[name: "*/$student"]], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/MNT-Lab/groovy-scripts.git']]])
        } catch (Exception ERROR) {
            stage_result = fail
            message = "Stage name:" + stage_name + ".Error:" + ERROR.message
            send_mail(message)
        }
        sh 'mkdir ./tmp'
        sh "tar -xf pipeline-${student}-${BUILD_NUMBER}.tar.gz -C ./tmp"
        try {
            sh "curl -v -u nexus-service-user:deploy --upload-file ./tmp/$path_to_jar http://EPBYMINW1969:8081/repository/maven-releases/pipeline-${student}/gradle-simple/${BUILD_NUMBER}/gradle-simple-${BUILD_NUMBER}.jar"
        } catch (Exception ERROR) {
        handle_exception(ERROR)
        } finally {
            stage_result = success
        }
    }

    if (stage_result == success) {
        stage('Deploy') {
            
        stage_name='Deploy' 
                input "Deploy you want to deploy a project?"
                try {
                    sh "java -jar $path_to_jar"
                } catch (Exception ERROR) {
                    handle_exception(ERROR)
                }
                finally {
                    send_to_slack("SUCCESS.Build done")
                
            }
        }
    }
}