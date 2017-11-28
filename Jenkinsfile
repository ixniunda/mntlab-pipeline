node('EPBYMINW2466'){
def grdHome = tool 'gradle3.3'
def javaHome = tool 'java8'
    try{
    stage('Checkout'){
        checkout([$class: 'GitSCM', 
        branches: [[name: 'anavitskaya']], 
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
        build job: 'EPBYMINW2466/MNTLAB-anavitskaya-child1-build-job', parameters: [string(name: 'BRANCH_NAME', value: ''), string(name: 'BRANCH_NAME', value: 'anavitskaya')],propagate: true, wait: true
        copyArtifacts filter: 'anavitskaya_dsl_script.tar.gz', fingerprintArtifacts: true, projectName: 'EPBYMINW2466/MNTLAB-anavitskaya-child1-build-job', target: '.'
    }
    } catch(err){echo "Tests fails.";currentBuild.result = 'FAILURE'}
    try{
    stage('Publish results'){
        sh '''
        tar -zxvf anavitskaya_dsl_script.tar.gz groovy_script.groovy
        rm -rf anavitskaya_dsl_script.tar.gz
        mv -f build/libs/gradle-simple.jar .
        tar -zcf pipeline-anavitskaya-"$BUILD_NUMBER".tar.gz Jenkinsfile groovy_script.groovy gradle-simple.jar   
        curl -v --user 'admin:admin123' --upload-file pipeline-anavitskaya-$BUILD_NUMBER.tar.gz http://EPBYMINW2466:8081/repository/maven-prod/
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
