import groovy.json.JsonSlurper

def projectCatalog = new File("sebhoss/projects.json")
def slurper = new JsonSlurper()
def jsonText = projectCatalog.getText()
def json = slurper.parseText(jsonText)

json.each {
    def project = it
    folder(project.name)
    project.upstreams.each {
        def upstreamProject = it
        job("${project.name}/${project.name}_with_latest_snapshot_${upstreamProject.name}") {
            blockOnUpstreamProjects()
            logRotator {
                numToKeep(5)
                daysToKeep(7)
            }
            scm {
                github(project.repository)
            }
            triggers {
                upstream("${upstreamProject.name}/${upstreamProject.name}", "SUCCESS")
            }
            steps {
                maven {
                    goals("versions:use-latest-versions")
                    properties("includes": upstreamProject.include)
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
        job("${project.name}/${project.name}_with_latest_stable_${upstreamProject.name}") {
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
                    goals("versions:use-latest-versions")
                    properties("includes": upstreamProject.include)
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
}
