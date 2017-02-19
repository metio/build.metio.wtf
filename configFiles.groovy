def settings = readFileFromWorkspace("configuration/repository.metio.wtf.xml")

configFiles {
    globalMavenSettingsConfig {
        id('repository.metio.wtf')
        name('repository.metio.wtf')
        comment('Mirrors Maven central with repository.metio.wtf')
        content(settings)
        isReplaceAll(true)
        serverCredentialMappings {
            serverCredentialMapping {
                serverId('sonatype-nexus')
                credentialsId('ossrh')
            }
            serverCredentialMapping {
                serverId('repository.metio.wtf')
                credentialsId('repository.metio.wtf')
            }
            serverCredentialMapping {
                serverId('github')
                credentialsId('build-metio-wtf-github')
            }
        }
    }

}
