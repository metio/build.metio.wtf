configFiles {
    globalMavenSettingsConfig {
        id('repository.metio.wtf')
        name('repository.metio.wtf') {
        comment('Mirrors Maven central with repository.metio.wtf')
        content(readFileFromWorkspace('configuration/repository.metio.wtf.xml'))
        isReplaceAll()
    }
}
