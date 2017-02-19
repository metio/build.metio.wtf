def settings = readFileFromWorkspace("configuration/repository.metio.wtf.xml")

configFiles {
    globalMavenSettingsConfig {
        id('repository.metio.wtf')
        name('repository.metio.wtf')
        comment('Mirrors Maven central with repository.metio.wtf')
        content(settings)
        isReplaceAll(true)
        serverCredentials('sonatype-nexus', 'ossrh')
        serverCredentials('repository.metio.wtf', 'repository.metio.wtf')
        serverCredentials('github', 'build-metio-wtf-github')
    }

}
