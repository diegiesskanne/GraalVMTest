plugins {
    id("com.palantir.graal") version "0.2.0-13-gb76f6cb"
    application
}

buildscript {
    repositories {
        jcenter()
    }
}

repositories {
    mavenCentral()
}

dependencies {
}

application {
    mainClassName = "TopTen"
}

tasks.compileJava {
    options.setFork(true)
    options.fork(Pair("executable", "/home/sergej/.gradle/caches/com.palantir.graal/1.0.0-rc10/graalvm-ce-1.0.0-rc10/bin/javac"))
}

graal {
    outputName("hello-world")
    graalVersion("1.0.0-rc10")

    // option("-H:+UseStackBasePointer")
    // option("-H:+ReportUnsupportedElementsAtRuntime")
    // option("-H:+AllowIncompleteClasspath")

    // option("-R:+PrintGCSummary")
    // option("-R:+PrintGC")
    // option("-R:+VerboseGC")

    option("--verbose")
    option("--no-server")
    mainClass("TopTen")
}
