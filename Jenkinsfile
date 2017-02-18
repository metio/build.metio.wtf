node {
    stage("configuration") {
        jobDsl targets: ['configFiles.groovy'].join('\n')
    }

    stage("sebhoss") {
        jobDsl scriptText: 'folder("sebhoss")'
        jobDsl targets: 'sebhoss/pipelines.groovy'

        jobDsl targets: ['sebhoss/verify.groovy',
            'sebhoss/views.groovy'].join('\n'),
               removedJobAction: 'DELETE',
               removedViewAction: 'DELETE',
               ignoreMissingFiles: true
    }

}
