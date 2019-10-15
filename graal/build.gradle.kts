plugins {
    //id("com.zoltu.application-agent") version "1.0.8"
    id("com.palantir.graal") version "0.6.0-14-g6fa0c0a"
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
    implementation(files("/home/javo6129/.gradle/caches/com.palantir.graal/19.2.0/graalvm-ce-19.2.0/jre/lib/jvmci/jvmci-api.jar"))

    implementation("com.squareup:javapoet:1.11.1")

    //agent(files("/home/sergej/IdeaProjects/prototyping/bytebuddy-agent/build/libs/bytebuddy-agent.jar"))

    implementation(project(":annotation-api"))
    implementation(project(":bytebuddy"))

    implementation("com.google.code.gson:gson:2.8.5")
    implementation("io.github.classgraph:classgraph:4.6.10")

    implementation("io.reactivex.rxjava2:rxjava:2.2.4")
    implementation("io.vavr:vavr:0.9.2")
    implementation("com.google.guava:guava:27.0.1-jre")

    // implementation 'org.graalvm.compiler:compiler:19.2.0.1'
    implementation("org.graalvm.compiler:compiler:19.2.0") {
        setTransitive(false)
    }
    // implementation 'com.oracle.substratevm:svm:19.2.0.1'
    implementation("com.oracle.substratevm:svm:19.2.0") {
        setTransitive(false)
    }
    // implementation 'org.graalvm.sdk:graal-sdk:19.2.0.1'
    implementation("org.graalvm.sdk:graal-sdk:19.2.0") {
        setTransitive(false)
    }
}

application {
    mainClassName = "de.eso.graalvm.Main"
}

graal {
    outputName("hello-world")
    graalVersion("19.2.0")
    // Allow image building with an incomplete class path: report type resolution errors at run time when they are accessed the first time, instead of during image building
    // option("-H:+AllowIncompleteClasspath")
    // Report usage of unsupported methods and fields at run time when they are accessed the first time, instead of as an error during image building
    // option("-JIsProduct=false")
    //option("-H:+ReportUnsupportedElementsAtRuntime")
    // When activating HTTPS -> image will be 10MiB be bigger in size.
    // --enable-https
    // option("-H:EnableURLProtocols=https")
    //option("--initialize-at-build-time=de.eso.api.DSIListener,de.eso.dsi.DSIWLANListener,de.eso.dsi.DSIOnlineListener,io.vavr.Value,io.vavr.Lambda,io.vavr.collection.Seq,io.vavr.collection.Iterator,io.vavr.collection.AbstractIterator,io.vavr.Function1,io.vavr.collection.Traversable,io.vavr.collection.IndexedSeq,io.vavr.collection.Collections,io.vavr.collection.Array,io.vavr.collection.Foldable,de.eso.graalvm.MyService,io.vavr.collection.Array$1,de.eso.dsi.DSIOnlineBase,de.eso.api.ProxyHandle")
    // set IsProduct-System-Property while image-gen
    //option("-DIsProduct=true")
    //option("--verbose")
    option("-H:+TraceClassInitialization")
    option("-agentlib:native-image-agent=config-output-dir=/home/javo6129/Desktop/jaroskram/GraalVMTest/graal/META-INF/native-image")
    // option("--no-server")
    mainClass("de.eso.graalvm.Main")
}

//configure<ByteBuddyExtension> {
//    transformation(closureOf<Transformation> {
//        // setClassPath(configurations.getByName("implementation"))
//        plugin = "de.eso.bytebuddy.ILoggerPlugin"
//    })
//}

//applicationAgent {
//    applyToRun = true
//    applyToTests = false
//    applyToStartScripts = true
//}
