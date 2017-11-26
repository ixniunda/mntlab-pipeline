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
    stage ('trigger child1') {
        build job: 'MNTLAB-kshchura-child1-build-job', parameters: [string(name: 'BRANCH', value: 'kshchura')]
        sh "cp /home/jenkins/.jenkins/workspace/MNTLAB-kshchura-child1-build-job/kshchura_dsl_script.tar.gz /home/jenkins/.jenkins/workspace/T11/"
        
    }

}
