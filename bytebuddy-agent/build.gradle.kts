plugins {
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.bytebuddy:byte-buddy-agent:1.9.6")
    implementation("net.bytebuddy:byte-buddy:1.9.6")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.3.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.3.1")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}


tasks.jar {
    manifest {
        attributes(
                "Premain-Class" to "de.eso.bytebuddy.BootstrapAgent",
                "Can-Redefine-Classes" to true,
                "Can-Retransform-Classes" to true,
                "Can-Set-Native-Method-Prefix" to true
        )
    }
}
