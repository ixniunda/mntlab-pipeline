
node{
    stage('Checkout'){
    checkout([$class: 'GitSCM', 
    branches: [[name: 'ataran']], 
    userRemoteConfigs: [[url: 'https://github.com/MNT-Lab/mntlab-pipeline.git']]
])
    }    
    stage('Build'){
        
        sh "/opt/gradle/bin/gradle clean build"
    }
    stage('Tests'){
        parallel(
        a: { sh "echo 'Cucumber tests';/opt/gradle/bin/gradle cucumber" },
        b: { sh "echo 'Jaco tests';/opt/gradle/bin/gradle jacocoTestReport" },
        c: { sh "echo 'Simple tests';/opt/gradle/bin/gradle test" }
        )
    }
    stage('Trigger Job'){
        build job: 'MNTLAB-ataran-child1-build-job', parameters: [string(name: 'BRANCH_NAME', value: ''), string(name: 'BRANCH_NAME', value: 'ataran')],propagate: true, wait: true
        
    }
    
}
