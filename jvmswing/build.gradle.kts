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

graal {
    mainClass("SwingMain")
    outputName("htchip8kt")
    javaVersion("11")
    windowsVsVarsPath("C:\\Program Files (x86)\\Microsoft Visual Studio 14.0\\VC\\bin\\amd64\\vcvars64.bat")
}
tasks.assembleDist { dependsOn(rootProject.tasks.getByName("build")) }