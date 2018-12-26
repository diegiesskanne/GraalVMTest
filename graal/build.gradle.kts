import groovy.lang.Closure
import net.bytebuddy.build.gradle.AbstractUserConfiguration
import net.bytebuddy.build.gradle.ByteBuddyExtension
import net.bytebuddy.build.gradle.Transformation

plugins {
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

val examplePlugin by configurations.creating

dependencies {
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

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

application {
    mainClassName = "de.eso.graalvm.Main"
}

graal {
    mainClass("de.eso.graalvm.Main")
    outputName("hello-world")
    graalVersion("1.0.0-rc10")
    option("-H:+ReportUnsupportedElementsAtRuntime")
    option("-H:EnableURLProtocols=http")
    option("-H:DynamicProxyConfigurationFiles=/home/sergej/IdeaProjects/prototyping/graal/proxies.json")
}

configure<ByteBuddyExtension> {
    transformation(closureOf<Transformation> {
        // setClassPath(configurations.getByName("implementation"))
        plugin = "de.eso.bytebuddy.ILoggerPlugin"
    })
}