import groovy.json.JsonSlurper

def org = "sebhoss";
def slurper = new JsonSlurper()
def jsonText = readFileFromWorkspace("${org}/projects.json")
def json = slurper.parseText(jsonText)

listView("Failure") {
    description('All failing jobs')
    jobs {
        def project = it
        json.each {
            name("${org}/${project.name}/${project.name}_verify")
        }
        name("${org}")
    }
    jobFilters {
        status {
            status(Status.UNSTABLE, Status.FAILED, Status.ABORTED)
            matchType(MatchType.EXCLUDE_UNMATCHED)
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

listView("Success") {
    description('All successful jobs')
    jobs {
        json.each {
            name("${org}/${it.name}/${it.name}")
        }
    }
    jobFilters {
        status {
            status(Status.STABLE)
            matchType(MatchType.EXCLUDE_UNMATCHED)
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
