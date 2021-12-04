plugins {
    kotlin("jvm")
    application
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

tasks.assembleDist { dependsOn(rootProject.tasks.getByName("build")) }