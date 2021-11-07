plugins {
    kotlin("multiplatform") version "1.5.31"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        val jvmMain by creating {
            dependencies {
                implementation("com.github.weisj:darklaf-core:2.7.3")
            }
        }
        val jvmTest by creating {
            dependsOn(commonTest)
            dependencies {
                implementation("org.junit.jupiter:junit-jupiter-api:5.3.1")
                runtimeOnly("org.junit.jupiter:junit-jupiter-engine:5.3.1")
            }
        }
    }
    jvm()
}

tasks.getByName("jvmTest", Test::class) {
    useJUnitPlatform()
}
