def giturl='https://github.com/MNT-Lab/mntlab-pipeline.git'
def BRANCH_NAME='*/uhramovich'
node {
    stage('Preparation'){
checkout([$class: 'GitSCM', branches: [[name: BRANCH_NAME]], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: giturl]]])
}
    stage('Build code'){
    mvnHome = tool 'gradle3.3'
    sh "'${mvnHome}/bin/gradle' build"
    }   
    stage('Testing code'){
        parallel(
           
            cucumber: {sh "'${mvnHome}/bin/gradle' cucumber"},
           
            junit: {sh "'${mvnHome}/bin/gradle' test"},
         
            jacoco: {sh "'${mvnHome}/bin/gradle' jacoco"}
            
            
            )
    }
    stage('Triggering job and fetching artefact after finishing'){
        def job = build job: 'MNTLAB-uhramovich-child1-build-job' ,parameters: [string(name: 'BRANCH_NAME', value: 'uhramovich')]
        sh "cp $JENKINS_HOME/workspace/MNTLAB-uhramovich-child1-build-job/uhramovich_dsl_script.tar.gz $JENKINS_HOME/workspace/pipeline-job/"
    }
    stage('Packaging and Publishing results'){
        sh "cd $JENKINS_HOME/workspace/pipeline-job/"
        sh "tar zxvf uhramovich_dsl_script.tar.gz"
        sh "cp build/libs/gradle-simple.jar . "
        sh "tar -czvf pipeline-uhramovich-'$BUILD_NUMBER'.tar.gz jobs.groovy gradle-simple.jar Jenkinsfile"
        archiveArtifacts artifacts: 'pipeline-uhramovich-${BUILD_NUMBER}.tar.gz'
    }
    
}
