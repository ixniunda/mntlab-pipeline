
def grdHome = tool 'gradle3.3'
def javaHome = tool 'java8'
node('EPBYMINW6405'){
    try{
    stage('Checkout'){
        checkout([$class: 'GitSCM', 
        branches: [[name: 'ataran']], 
        userRemoteConfigs: [[url: 'https://github.com/MNT-Lab/mntlab-pipeline.git']]]) 
    } 
    } catch(err){echo "Checkout fails.";currentBuild.result = 'FAILURE'}
    try{
    stage('Build'){
        
        sh "'$grdHome/bin/gradle' clean build"
    }
    } catch(err){echo "Building fails.";currentBuild.result = 'FAILURE'}
    try{
    stage('Tests'){
        parallel(
        a: { sh "echo 'Cucumber tests';'$grdHome/bin/gradle' cucumber" },
        b: { sh "echo 'Jaco tests';'$grdHome/bin/gradle' jacocoTestReport" },
        c: { sh "echo 'Simple tests';'$grdHome/bin/gradle' test" }
        )
    }
    } catch(err){echo "Tests fails.";currentBuild.result = 'FAILURE'}
    try{
    stage('Trigger Job'){
        build job: 'EPBYMINW6405/MNTLAB-ataran-child1-build-job', parameters: [string(name: 'BRANCH_NAME', value: ''), string(name: 'BRANCH_NAME', value: 'ataran')],propagate: true, wait: true
        sh "cp -f $JENKINS_HOME/workspace/MNTLAB-ataran-child1-build-job/ataran_dsl_script.tar.gz $JENKINS_HOME/workspace/pipeline_job/"
    }
    } catch(err){echo "Tests fails.";currentBuild.result = 'FAILURE'}
    try{
    stage('Publish results'){
        sh '''
        tar -zxvf ataran_dsl_script.tar.gz groovy_script.groovy
        rm -rf ataran_dsl_script.tar.gz
        mv -f build/libs/gradle-simple.jar .
        tar -zcf pipeline-ataran-"$BUILD_NUMBER".tar.gz Jenkinsfile groovy_script.groovy gradle-simple.jar   
        curl -v --user 'admin:admin123' --upload-file pipeline-ataran-$BUILD_NUMBER.tar.gz http://EPBYMINW6405:8081/repository/maven-prod/
        '''
    }
    } catch(err){echo "Publishing fails.";currentBuild.result = 'FAILURE'}
    
    stage('Approve'){
        input message: 'Are you sure?', ok: 'YES'
    }
    try{
    stage('Deployment'){
        sh "java -jar gradle-simple.jar"
    }
    } catch(err){echo "Deploy fails.";currentBuild.result = 'FAILURE'}
    stage('Final'){
        echo 'Success'
    }
    
}
