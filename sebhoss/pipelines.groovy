import groovy.json.JsonSlurper

def org = "sebhoss";
def slurper = new JsonSlurper()
// https://github.com/jenkinsci/job-dsl-plugin/wiki/Job-DSL-Commands#reading-files-from-workspace
def jsonText = readFileFromWorkspace("${org}/projects.json")
def json = slurper.parseText(jsonText).findAll { it.pipelines }

json.each {
    def project = it
    project.pipelines.each {
        def pipeline = it
        folder("${org}/${project.name}")
        pipelineJob("${org}/${project.name}/${project.name}-${pipeline.name}") {
            logRotator {
                numToKeep(5)
                daysToKeep(7)
            }
            scm {
                git {
                    branch(pipeline.branch)
                    remote {
                        github(project.repository)
                        credentials('build-metio-wtf-github')
                    }
                }
            }
            if (pipeline.trigger == "push") {
                triggers {
                    githubPush()
                }
            }
            definition {
                cpsScm {
                    scm {
                        git {
                            branch(pipeline.branch)
                            remote {
                                github(project.repository)
                                credentials('build-metio-wtf-github')
                            }
                        }
                    }
                    scriptPath(pipeline.path)
                }
            }
        }
    }
}
