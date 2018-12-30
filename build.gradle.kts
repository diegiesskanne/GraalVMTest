plugins {
    idea
}

group = "de.eso"
version = "1.0-SNAPSHOT"

subprojects {
    apply(plugin = "idea")
    apply(plugin = "java-library")
    idea {
        module {
            isDownloadSources = true
            setDownloadJavadoc(true)
        }
    }

    repositories {
        mavenCentral()
    }

    configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_1_8
    }
}