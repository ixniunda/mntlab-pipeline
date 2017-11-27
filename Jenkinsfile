node {
    stage ('GIT chackout') {
        git([url: 'https://github.com/MNT-Lab/mntlab-pipeline.git', branch: 'kshchura'])
    }
    stage ('BUILD') {
        def gradlehome = tool "gradle3.3"
        env.PATH = "${gradlehome}/bin:${env.PATH}"
        sh "gradle build"
    }
    stage ('TESTS') {
        parallel (
                cucumber: { sh "gradle cucumber"},
                jacocoTestReport: { sh "gradle jacocoTestReport"},
                test: { sh "gradle test"}
        )
    }
    stage ('child1 JOB') {
        build job: 'MNTLAB-kshchura-child1-build-job', parameters: [string(name: 'BRANCH', value: 'kshchura')]
        sh "cp /home/jenkins/.jenkins/workspace/MNTLAB-kshchura-child1-build-job/kshchura_dsl_script.tar.gz /home/jenkins/.jenkins/workspace/T11/"
        
    }
    stage ('Packaging and Publishing') {
        sh ("cp /home/jenkins/.jenkins/workspace/MNTLAB-kshchura-child1-build-job/dsl_main.groovy .")
        
        sh ("cp build/libs/gradle-simple.jar .")
        sh ("tar -czvf pipeline-kshchura-${BUILD_NUMBER}.tar.gz dsl_main.groovy gradle-simple.jar Jenkinsfile")
        sh ("curl -v -u admin:admin123 --upload-file pipeline-kshchura-${BUILD_NUMBER}.tar.gz  http://172.28.128.9:8081/repository/artifacts/pipeline-kshchura-${BUILD_NUMBER}.tar.gz")
        archiveArtifacts "pipeline-kshchura-${BUILD_NUMBER}.tar.gz"
    }
    stage ('Approval') {
        input "Deploy to prod?"
    }
    stage ('Deployment') {
        sh ('java -jar gradle-simple.jar')
    }
    

}



