plugins {
    idea
}

group = "de.eso"
version = "1.0-SNAPSHOT"

subprojects {
    apply(plugin = "idea")
    idea {
        module {
            isDownloadSources = true
            setDownloadJavadoc(true)
        }
    }
}