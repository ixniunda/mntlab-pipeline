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
    }
    }
