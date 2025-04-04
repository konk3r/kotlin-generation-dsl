import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

import java.io.File
import java.io.FileInputStream
import java.util.*

val compileKotlin: KotlinCompile by tasks

val kotlinpoetVersion: String by project
val kotlinVersion: String by project
val googleAutoServiceVersion: String by project

val exposedVersion: String by project
val assertKVersion: String by project

val prop = Properties().apply {
    load(FileInputStream(File(project.gradle.gradleUserHomeDir, "local.properties")))
}

plugins {
    `java-library`
    `maven-publish`
    signing
    kotlin("jvm") version "2.1.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.9.0")
    implementation("com.squareup:kotlinpoet:$kotlinpoetVersion")
    implementation("com.squareup:kotlinpoet-metadata:$kotlinpoetVersion")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")

    testImplementation("com.github.tschuchortdev:kotlin-compile-testing:1.4.9")
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:$assertKVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlinVersion")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "11"
            freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
        }
    }

    withType<Test> {
        minHeapSize = "512m"
        maxHeapSize = "4096m"
        jvmArgs = listOf("-XX:MaxPermSize=1024m")
    }
}

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
    dependsOn("javadoc")
    from(tasks.javadoc.get().destinationDir)
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

publishing {
    repositories {
        maven {
            name = "local"
            isAllowInsecureProtocol = true
            url = uri("http://localhost:8081/repository/maven-local")
        }
        maven {
            name = "sonatype"
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = prop.getProperty("newOssrhUsername")
                password = prop.getProperty("newOssrhPassword")
            }
        }
    }
    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["java"])

            group = "com.casadetasha"
            artifactId = "kotlin-generation-dsl"
            version = "2.1.0-alpha3"

            artifact(sourcesJar.get())
            artifact(javadocJar.get())

            pom {
                name.set("CDT Kotlin Generation DSL")
                description.set("GENERATE KOTLIN FOR KONSUMPTION. KONSUME. KONSUME. KONSUME.")
                url.set("http://www.sproutes.io")

                scm {
                    connection.set("scm:git://github.com/konk3r/kotlin-generation-dsl")
                    developerConnection.set("scm:git:git@github.com:konk3r/kotlin-generation-dsl.git")
                    url.set("https://github.com/konk3r/kotlin-generation-dsl")
                }

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                developers {
                    developer {
                        id.set("gabriel")
                        name.set("Gabriel Spencer")
                        email.set("gabriel@casadetasha.dev")
                    }
                }
            }
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}
