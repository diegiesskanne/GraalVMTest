import groovy.lang.Closure
import net.bytebuddy.build.gradle.AbstractUserConfiguration
import net.bytebuddy.build.gradle.ByteBuddyExtension
import net.bytebuddy.build.gradle.Transformation

plugins {
    id("com.zoltu.application-agent") version "1.0.8"
    id("com.palantir.graal") version "0.2.0-13-gb76f6cb"
    application
}

buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath("net.bytebuddy:byte-buddy-gradle-plugin:1.9.6")
    }
}
apply(plugin = "net.bytebuddy.byte-buddy")

repositories {
    mavenCentral()
}

// val examplePlugin by configurations.creating

dependencies {
    implementation("com.squareup:javapoet:1.11.1")

    agent(files("/home/sergej/IdeaProjects/prototyping/bytebuddy-agent/build/libs/bytebuddy-agent.jar"))

    implementation(project(":annotation-api"))
    implementation(project(":bytebuddy"))

    implementation("com.google.code.gson:gson:2.8.5")
    implementation("io.github.classgraph:classgraph:4.6.10")

    implementation("io.reactivex.rxjava2:rxjava:2.2.4")
    implementation("io.vavr:vavr:0.9.2")
    implementation("com.google.guava:guava:27.0.1-jre")

//    implementation("org.graalvm.compiler:compiler:1.0.0-rc10") {
//        setTransitive(false)
//    }

    implementation("com.oracle.substratevm:svm:1.0.0-rc10") {
        setTransitive(false)
    }
    implementation("org.graalvm.sdk:graal-sdk:1.0.0-rc10") {
        setTransitive(false)
    }
}

application {
    mainClassName = "de.eso.graalvm.Main"
}

graal {
    mainClass("de.eso.graalvm.Main")
    outputName("hello-world")
    graalVersion("1.0.0-rc10")
    // Allow image building with an incomplete class path: report type resolution errors at run time when they are accessed the first time, instead of during image building
    // option("-H:+AllowIncompleteClasspath")
    // Report usage of unsupported methods and fields at run time when they are accessed the first time, instead of as an error during image building
    option("-H:+ReportUnsupportedElementsAtRuntime")
    // When activating HTTPS -> image will be 10MiB be bigger in size.
    // --enable-https
    // option("-H:EnableURLProtocols=https")
    option("--verbose")
    option("--no-server")
}

configure<ByteBuddyExtension> {
    transformation(closureOf<Transformation> {
        // setClassPath(configurations.getByName("implementation"))
        plugin = "de.eso.bytebuddy.ILoggerPlugin"
    })
}

applicationAgent {
    applyToRun = true
    applyToTests = false
    applyToStartScripts = true
}