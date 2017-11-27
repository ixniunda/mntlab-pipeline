def student = "kshchura"


job("EPBYMINW3093/MNTLAB-${student}-main-build-job") {
    description 'T10 main job'
    parameters {
        choiceParam('BRANCH', ['kshchura', 'master'])
        activeChoiceReactiveParam('JOBS') {
            choiceType('CHECKBOX')
            groovyScript {
                script('["MNTLAB-kshchura-child1-build-job", "MNTLAB-kshchura-child2-build-job", "MNTLAB-kshchura-child3-build-job", "MNTLAB-kshchura-child4-build-job"]')

            }
        }

    }
    steps {
        conditionalSteps {
            condition {
                shell('echo $JOBS | grep -q "child1"')
            }
            runner('Run')
            steps {
                downstreamParameterized {
                    trigger("EPBYMINW3093/MNTLAB-${student}-child1-build-job") {
                        block {
                            buildStepFailure('FAILURE')
                            failure('FAILURE')
                            unstable('UNSTABLE')
                        }
                        parameters {
                            predefinedProp('BRANCH', '${BRANCH}')
                        }
                    }

                }

            }

        }
        conditionalSteps {
            condition {
                shell('echo $JOBS | grep -q "child2"')
            }
            runner('Run')
            steps {
                downstreamParameterized {
                    trigger("EPBYMINW3093/MNTLAB-${student}-child2-build-job") {
                        block {
                            buildStepFailure('FAILURE')
                            failure('FAILURE')
                            unstable('UNSTABLE')
                        }
                        parameters {
                            predefinedProp('BRANCH', '${BRANCH}')
                        }
                    }

                }
            }

        }
        conditionalSteps {
            condition {
                shell('echo $JOBS | grep -q "child3"')
            }
            runner('Run')
            steps {
                downstreamParameterized {
                    trigger("EPBYMINW3093/MNTLAB-${student}-child3-build-job") {
                        block {
                            buildStepFailure('FAILURE')
                            failure('FAILURE')
                            unstable('UNSTABLE')
                        }
                        parameters {
                            predefinedProp('BRANCH', '${BRANCH}')
                        }
                    }

                }
            }
        }
        conditionalSteps {
            condition {
                shell('echo $JOBS | grep -q "child4"')
            }
            runner('Run')
            steps {
                downstreamParameterized {
                    trigger("EPBYMINW3093/MNTLAB-${student}-child4-build-job") {
                        block {
                            buildStepFailure('FAILURE')
                            failure('FAILURE')
                            unstable('UNSTABLE')
                        }
                        parameters {
                            predefinedProp('BRANCH', '${BRANCH}')
                        }
                    }

                }
            }

        }
    }
}

for (i in 1..4) {
    job("EPBYMINW3093/MNTLAB-kshchura-child${i}-build-job") {
        description "T10 child${i} job"
        parameters {
            stringParam('BRANCH')
        }
        parameters {
            activeChoiceReactiveParam('BRANCH') {
                choiceType('CHECKBOX')
                groovyScript {
                    script(''' 
def remote_url="https://github.com/MNT-Lab/mntlab-dsl.git"
def command = [ "/bin/bash", "-c", "git ls-remote --heads " + remote_url + " | awk '{print \\$2}' | sort  -V | sed 's@refs/heads/@@'" ]
def process = command.execute()

def result = process.in.text.tokenize("\\n")

def branches = []
for(i in result) {
        branches.add(i)
}
return branches''')

                }
            }

        }

        scm {
            git{
                remote {
                    url('https://github.com/MNT-Lab/mntlab-dsl')
                }
                branch('*/${BRANCH}')

            }
        }

        steps{
            shell ('echo $BRANCH; export BRANCH_NAME=$BRANCH; chmod +x script.sh; bash script.sh > output.txt; tar -czvf $BRANCH_NAME\\_dsl_script.tar.gz script.sh')
        }
        publishers {
            archiveArtifacts('output.txt, *.tar.gz')
        }
    }

}

