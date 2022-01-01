plugins {
    kotlin("jvm")
    application
    id("com.palantir.graal") version "0.10.0"
}

repositories {
    mavenCentral()
}
dependencies {
    implementation(project(":"))
}

application {
    mainClass.set("SwingMain")
}

val jniConfigFile = projectDir.resolve("jni-config.json")
val reflectConfigFile = projectDir.resolve("reflect-config.json")
val resourceConfigFile = projectDir.resolve("resource-config.json")
val graalDistributionVersion = "21.3.0"
graal {
    graalVersion(graalDistributionVersion)
    mainClass("SwingMain")
    outputName("htchip8kt")
    javaVersion("11")
    windowsVsVarsPath("C:\\Program Files\\Microsoft Visual Studio\\2022\\Community\\VC\\Auxiliary\\Build\\vcvars64.bat")
    option("--no-fallback")
    option("-H:JNIConfigurationFiles=${jniConfigFile.absolutePath}")
    option("-H:ReflectionConfigurationFiles=${reflectConfigFile.absolutePath}")
    option("-H:ResourceConfigurationFiles=${resourceConfigFile.absolutePath}")
}
tasks.assembleDist { dependsOn(rootProject.tasks.getByName("build")) }

tasks.nativeImage {
    inputs.files(jniConfigFile, reflectConfigFile, resourceConfigFile)
}

val buildDirGraalConfigFolder = buildDir.resolve("graalconfig")
val gatherGraalInfo by tasks.registering(Exec::class) {
    group = "graal"
    dependsOn(tasks.getByName("installDist"))
    environment("JAVA_HOME", "C:\\Users\\hanno\\.gradle\\caches\\com.palantir.graal\\$graalDistributionVersion\\11\\graalvm-ce-java11-$graalDistributionVersion")
    environment("JAVA_OPTS", "-agentlib:native-image-agent=config-output-dir=$buildDirGraalConfigFolder")
    executable(buildDir.resolve("install/jvmswing/bin/jvmswing.bat").absolutePath)
    doLast {
        listOf(
            buildDirGraalConfigFolder.resolve("jni-config.json"),
            buildDirGraalConfigFolder.resolve("reflect-config.json"),
            buildDirGraalConfigFolder.resolve("resource-config.json"),
        ).forEach {
            it.copyTo(projectDir, overwrite = true)
        }
    }
}

distributions {
    create("htchip8kt") {
        distributionBaseName.set("htchip8kt")
        contents {
            from(buildDir.resolve("graal"))
        }
    }
}
