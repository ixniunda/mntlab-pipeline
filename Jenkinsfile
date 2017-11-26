node {
    stage ('GIT chackout') {
      git([url: 'https://github.com/MNT-Lab/mntlab-pipeline.git', branch: 'kshchura'])
    }
    stage ('BUILD') {
        def gradlehome = tool "gradle3.3"
        env.PATH = "${gradlehome}/bin:${env.PATH}"
        sh "gradle build"
    }
    def task = [:]
    tasks ["task1"] = {
        stage ("task1") {
            node {
                sh "gradle cucumber"
            }
        }
    }
    tasks ["task2"] = {
        stage ("task2") {
            node {
                sh "gradle jacocoTestReport"
            }
        }
    }
    tasks ["task3"] = {
        stage ("task3") {
            node {
                sh "gradle test"
            }
        }
    }
}

