node {
    stage ('Preparation') {
        git branch: 'dbakulin', url: 'https://github.com/ixniunda/mntlab-pipeline.git'
    } 

    stage ('Building code') {
        sh "gradle build"
    }
    
    stage ('Testing code') {
        parallel (
         Unit_test: {
             stage ('Unit tests') {
             sh "gradle test"
            }
         },
         Jacoco_test: {
            stage ('Jacoco tests') {
             sh "gradle jacoco"
            }
         },
         Cucumber_test: { 
            stage ('Cucumber tests') {
             sh "gradle cucumber"
            }
         }
        )
    }
    
    stage ('Triggering job and fetching artefact after finishing') {
        build job: 'MNTLAB-dbakulin-child1-build-job', parameters: [string(name: 'BRANCH_NAME', value: 'dbakulin')]
        copyArtifacts filter: '*.tar.gz', fingerprintArtifacts: true, projectName: 'MNTLAB-dbakulin-child1-build-job'
    }
    
    stage ('Packaging and Publishing results') {
        sh 'tar -xvzf dbakulin_dsl_script.tar.gz'
        sh 'tar -cvzf pipeline-dbakulin-"${BUILD_NUMBER}".tar.gz jobs.groovy Jenkinsfile build/libs/gradle-simple.jar '
        archiveArtifacts '*.tar.gz'
    }
    
    stage ('Asking for manual approval') {
        input 'Deploy artifacts?'
    }
    
    stage ('Deployment') {
        sh 'java -jar build/libs/gradle-simple.jar'
    }
    
    stage ('Sending status') {
        echo 'SUCSESS'
    }
}
