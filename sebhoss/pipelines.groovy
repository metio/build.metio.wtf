import groovy.json.JsonSlurper

def org = "sebhoss";
def slurper = new JsonSlurper()
// https://github.com/jenkinsci/job-dsl-plugin/wiki/Job-DSL-Commands#reading-files-from-workspace
def jsonText = readFileFromWorkspace("${org}/projects.json")
def json = slurper.parseText(jsonText)

json.each {
    def project = it
    project.pipelines.each {
        def pipeline = it
        folder("${org}/${project.name}")
        pipelineJob(${org}/${project.name}-${pipeline.name}) {
            logRotator {
                numToKeep(5)
                daysToKeep(7)
            }
            scm {
                github(project.repository)
            }
            triggers {
                githubPush()
            }
            wrappers {
                preBuildCleanup()
            }
            definition {
                cps {
                    script(readFileFromWorkspace(pipeline.path))
                    sandbox()
                }
            }
            publishers {
                irc {
                    strategy("ALL")
                    notificationMessage("SummaryOnly")
                }
            }
        }
    }
}