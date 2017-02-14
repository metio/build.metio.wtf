import groovy.json.JsonSlurper

def projectCatalog = readFileFromWorkspace('sebhoss/projects.json')
def slurper = new JsonSlurper()
def jsonText = projectCatalog.getText()
def json = slurper.parseText(jsonText)

json.each {
    def project = it
    folder(project.name)
    job("${project.name}/${project.name}_deploy") {
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
        steps {
            maven {
                goals("clean")
                goals("deploy")
                mavenInstallation("maven-latest")
                providedGlobalSettings("repository.metio.wtf")
            }
        }
        steps {
            maven {
                goals("sonar:sonar")
                properties("sonar.host.url": "https://quality.metio.wtf")
                properties("sonar.pitest.mode": "reuseReport")
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

listView("Deployments") {
    description("All jobs that deploy artifacts")
    jobs {
        json.each {
            name("${it.name}/${it.name}_deploy_to_local-nexus")
        }
    }
    recurse(true)
    columns {
        status()
        weather()
        name()
        lastSuccess()
        lastFailure()
        lastDuration()
        buildButton()
    }
}
