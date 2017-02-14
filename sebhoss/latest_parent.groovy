import groovy.json.JsonSlurper

def projectCatalog = new File("sebhoss/projects.json")
def slurper = new JsonSlurper()
def jsonText = projectCatalog.getText()
def json = slurper.parseText(jsonText).findAll { it.mbp }

json.each {
    def project = it
    folder(project.name)
    job("${project.name}/${project.name}_with_latest_snapshot_parent") {
        blockOnUpstreamProjects()
        logRotator {
            numToKeep(5)
            daysToKeep(7)
        }
        scm {
            github(project.repository)
        }
        triggers {
            upstream("maven-build-process/maven-build-process_deploy", "SUCCESS")
            cron("@daily")
        }
        steps {
            maven {
                goals("versions:update-parent")
                properties("generateBackupPoms": false)
                properties("allowSnapshots": true)
                mavenInstallation("maven-latest")
                providedGlobalSettings("repository.metio.wtf")
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
        publishers {
            irc {
                strategy("ALL")
                notificationMessage("SummaryOnly")
            }
        }
    }
    job("${project.name}/${project.name}_with_latest_stable_parent") {
        blockOnUpstreamProjects()
        logRotator {
            numToKeep(5)
            daysToKeep(7)
        }
        scm {
            github(project.repository)
        }
        triggers {
            cron("@daily")
        }
        steps {
            maven {
                goals("versions:update-parent")
                properties("generateBackupPoms": false)
                mavenInstallation("maven-latest")
                providedGlobalSettings("repository.metio.wtf")
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
        publishers {
            irc {
                strategy("ALL")
                notificationMessage("SummaryOnly")
            }
        }
    }
}
