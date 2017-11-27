node {
    stage ('GIT chackout') {
        git([url: 'https://github.com/MNT-Lab/mntlab-pipeline.git', branch: 'kshchura'])
    }
    stage ('BUILD') {
        try {
            def gradlehome = tool "gradle3.3"
            env.PATH = "${gradlehome}/bin:${env.PATH}"
            sh "gradle build"
        } catch (err) {
            println "FAILURE on BUILD stage"
            throw err

        }
    }

    stage ('TESTS') {
        try {
            parallel(
                    cucumber: { sh "gradle cucumber" },
                    jacocoTestReport: { sh "gradle jacocoTestReport" },
                    test: { sh "gradle test" }
            )
        } catch (err) {
            println "FAILURE on TESTS stage"
            throw err
        }
    }
    stage ('child1 JOB') {
        try {
        build job: 'MNTLAB-kshchura-child1-build-job', parameters: [string(name: 'BRANCH', value: 'kshchura')]
        sh "cp /home/jenkins/.jenkins/workspace/MNTLAB-kshchura-child1-build-job/kshchura_dsl_script.tar.gz /home/jenkins/.jenkins/workspace/T11/"

        } catch (err) {
            println "FAILURE on child1 JOB1 stage"
            throw err
        }
    }
    stage ('Packaging and Publishing') {
        try {
        sh ("cp /home/jenkins/.jenkins/workspace/MNTLAB-kshchura-child1-build-job/dsl_main.groovy .")

        sh ("cp build/libs/gradle-simple.jar .")
        sh ("tar -czvf pipeline-kshchura-${BUILD_NUMBER}.tar.gz dsl_main.groovy gradle-simple.jar Jenkinsfile")
        sh ("curl -v -u admin:admin123 --upload-file pipeline-kshchura-${BUILD_NUMBER}.tar.gz  http://172.28.128.9:8081/repository/artifacts/pipeline-kshchura-${BUILD_NUMBER}.tar.gz")
        archiveArtifacts "pipeline-kshchura-${BUILD_NUMBER}.tar.gz"
        } catch (err) {
            println "FAILURE on Packaging and Publishing stage"
            throw err
        }
    }
     stage ('Approval') {
         try {
         input "Deploy to prod?"
         } catch (err) {
             println "FAILURE on Approval stage"
             throw err
         }
   }
    stage ('Deployment') {
        try {
        sh ('java -jar gradle-simple.jar')
        } catch (err) {
            println "FAILURE on Packaging and Publishing stage"
            throw err
        }
    }
//    echo "122222222222222222222"
    //  echo "${result}"
//    echo "Caught: ${err}"
    //  echo "${currentBuild.result}"
    //echo "${currentBuild.displayName}"
//    echo "${currentBuild.currentResult}"
    //   currentBuild.result = 'FAILURE'

}
