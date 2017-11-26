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
        sh "cp -f $JENKINS_HOME/workspace/MNTLAB-ataran-child1-build-job/ataran_dsl_script.tar.gz $JENKINS_HOME/workspace/pipeline_job/"
    }
    stage('Publish results'){
        sh '''
        tar -zxvf ataran_dsl_script.tar.gz groovy_script.groovy
        rm -rf ataran_dsl_script.tar.gz
        mv -f build/libs/gradle-simple.jar .
        tar -zcf pipeline-ataran-"$BUILD_NUMBER".tar.gz Jenkinsfile groovy_script.groovy gradle-simple.jar   
        curl -v --user 'admin:admin123' --upload-file pipeline-ataran-$BUILD_NUMBER.tar.gz http://172.28.128.5:8081/repository/maven-prod/
        '''
    }
    stage('Approve'){
        input message: 'Are you sure?', ok: 'YES'
    }
    stage('Deployment'){
        sh "java -jar gradle-simple.jar"
    }
    
}
