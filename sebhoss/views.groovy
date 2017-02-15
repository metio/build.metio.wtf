import groovy.json.JsonSlurper

def org = "sebhoss";
def slurper = new JsonSlurper()
// https://github.com/jenkinsci/job-dsl-plugin/wiki/Job-DSL-Commands#reading-files-from-workspace
def jsonText = readFileFromWorkspace("${org}/projects.json")
def json = slurper.parseText(jsonText)

listView("Failure") {
    description('All failing jobs')
    jobs {
        regex("/${org}/.+/.+/")
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
        regex("${org}/.+/.+")
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
