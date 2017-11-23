node {
    stage('Preparation'){
checkout([$class: 'GitSCM', branches: [[name: '*/uhramovich']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/MNT-Lab/mntlab-pipeline.git']]])
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
        
    }
    }
