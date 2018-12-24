plugins {
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}