def nodeName = 'EPBYMINW1374'
node("${nodeName}") {
    try{
        def gradleHome
        gradleHome = tool 'gradle3.3'
        env.PATH = "${gradleHome}/bin:${env.PATH}"
        def javaHome
        javaHome = tool 'java8'
        def branchName = 'ykaratseyeu'

        stage('Preparation') {
            // Get some code from a GitHub repository
            git([url: 'https://github.com/MNT-Lab/mntlab-pipeline.git', branch: "${branchName}"])
        }
        stage ('Building code') {
            sh "gradle build"
        }
        stage ('Testing code'){
            parallel(
                    cucumber:{ sh "gradle cucumber"},
                    jacocoTestReport:{ sh "gradle jacocoTestReport"},
                    test:{ sh "gradle test"}
            )
        }
        stage ('Triggering job and fetching artefact after finishing'){
            build job: "${nodeName}/MNTLAB-${branchName}-child1-build-job", parameters: [string(name: 'BRANCH_NAME', value: "${branchName}")], propagate: true, wait: true
            copyArtifacts(projectName: "${nodeName}/MNTLAB-${branchName}-child1-build-job", filter: "${branchName}_dsl_script.tar.gz")


        }
        stage ('Packaging and Publishing results'){
            sh "tar xzvf ${branchName}_dsl_script.tar.gz jobs.groovy; cp build/libs/gradle-simple.jar gradle-simple.jar; tar czvf pipeline-${branchName}-${BUILD_NUMBER}.tar.gz jobs.groovy Jenkinsfile gradle-simple.jar"
            sh "curl -v -u jenkins:jenkins --upload-file pipeline-${branchName}-${BUILD_NUMBER}.tar.gz http://172.28.128.3:8081/repository/arifacts/pipeline/${BUILD_NUMBER}/pipeline-${branchName}-${BUILD_NUMBER}.tar.gz"
            archiveArtifacts "pipeline-${branchName}-${BUILD_NUMBER}.tar.gz"
            //nexusArtifactUploader artifacts: [[artifactId: 'pipeline', classifier: '', file: "pipeline-${branchName}-${BUILD_NUMBER}.tar.gz", type: 'tar.gz']], credentialsId: '4fcc9128-744c-4ad0-8726-a7990142ac25', groupId: 'pipeline', nexusUrl: '172.28.128.3:8081/', nexusVersion: 'nexus3', protocol: 'http', repository: 'arifacts', version: '$BUILD_NUMBER'
        }
        stage ('Asking for manual approval'){
            input 'Do you want to deploy gradle-simple.jar?'

        }
        stage ('Deployment'){
            try{
                currentBuild.result = 'SUCCESS'
                sh ("java -jar gradle-simple.jar")

            }catch (Exception err) {
                currentBuild.result = 'FAILURE'
            }
        }
        stage ('Result'){
            echo "RESULT: ${currentBuild.result}"
        }

    }
    catch (Exception | hudson.AbortException err) {
        print("Failed on stage: ${stage}")
        if(err.class == hudson.AbortException) {
            println("ABORTED!!!! ${err}")
        } else {println("SGW!!! ${err.getProperties()}")}
        throw err
    }
}
