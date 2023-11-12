plugins {
    kotlin("jvm") version "1.9.10"
    `java-library`
    `maven-publish`
}

group = "com.judas"
version = "1.0"

publishing {
    publications {
        create<MavenPublication>("sgf4k") {
            groupId = rootProject.group.toString()
            artifactId = "sgf4k"
            version = rootProject.version.toString()

            pom {
                name = "SGF4K"
                description = "Full Kotlin SGF tools"
                url = "https://github.com/Judas/sgf4k"
                licenses {
                    license {
                        name = "The Apache License, Version 2.0"
                        url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
                    }
                }
                developers {
                    developer {
                        id = "Judas"
                        name = "Jules Tréhorel"
                        email = "jules@trehorel.bzh"
                    }
                }
            }

            from(components["java"])
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/Judas/sgf4k")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

java {
    withSourcesJar()
}

tasks.jar {
    manifest {
        attributes(
            mapOf(
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version
            )
        )
    }
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}
