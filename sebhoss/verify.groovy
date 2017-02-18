import groovy.json.JsonSlurper

def org = "sebhoss";
def slurper = new JsonSlurper()
// https://github.com/jenkinsci/job-dsl-plugin/wiki/Job-DSL-Commands#reading-files-from-workspace
def jsonText = readFileFromWorkspace("${org}/projects.json")
def json = slurper.parseText(jsonText)

json.each {
    def project = it
    folder("${org}/${project.name}")
    job("${org}/${project.name}/${project.name}_verify") {
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
            credentialsBinding {
                string('sonar_token', 'sonar.login')
            }
        }
        steps {
            maven {
                goals("clean")
                goals("verify")
                mavenInstallation("maven-latest")
                providedGlobalSettings("repository.metio.wtf")
            }
        }
        steps {
            maven {
                goals("sonar:sonar")
                properties("sonar.host.url": "https://quality.metio.wtf")
                properties("sonar.pitest.mode": "reuseReport")
                properties("sonar.login": '${sonar_token}')
                mavenInstallation("maven-latest")
                providedGlobalSettings("repository.metio.wtf")
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
