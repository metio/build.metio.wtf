import groovy.json.JsonSlurper

def organization = "sebhoss";
def slurper = new JsonSlurper()
// https://github.com/jenkinsci/job-dsl-plugin/wiki/Job-DSL-Commands#reading-files-from-workspace
def jsonText = readFileFromWorkspace("${organization}/projects.json")
def projects = slurper.parseText(jsonText).findAll { it.pipelines }

projects.each {
    def project = it
    project.pipelines.each {
        def pipeline = it
        folder("${organization}/${project.name}")
        pipelineJob("${organization}/${project.name}/${project.name}-${pipeline.name}") {
            logRotator {
                numToKeep(5)
                daysToKeep(7)
            }
            if (pipeline.trigger == "push") {
                triggers {
                    githubPush()
                }
            } else if (pipeline.trigger.startsWith("cron:")) {
                def schedule = pipeline.trigger.drop(5) // '5' = "cron:".length
                triggers {
                    cron(schedule)
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
