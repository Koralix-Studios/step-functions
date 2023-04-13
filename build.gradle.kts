plugins {
    id("java")
    id("maven-publish")
    id("signing")
}

project.group = "com.koralix.stepfn"
project.version = "1.1.2"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

java {
    withJavadocJar()
    withSourcesJar()
}

tasks.named<Javadoc>("javadoc") {
    title = "Step Functions - ${version}"
    options {
        overview = file("${rootDir}/javadoc/overview.html").absolutePath
        windowTitle = "step-functions:${project.version}"
        header = file("${rootDir}/javadoc/header.html").readText().replace("@version@", project.version as String)
        locale = "en"
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            pom {
                name.set("com.koralix.stepfn:step-functions")
                description.set("Step Functions is a java library to create functions step-by-step with multi-branch logic.")
                url.set("https://github.com/Koralix-Studios/step-functions")

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://github.com/Koralix-Studios/step-functions/blob/master/LICENSE")
                    }
                }

                developers {
                    developer {
                        name.set("JohanVonElectrum")
                        email.set("johanvonelectrum@gmail.com")
                        organization.set("Koralix Studios")
                        organizationUrl.set("https://www.koralix.com")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/koralix-studios/step-functions.git")
                    developerConnection.set("scm:git:ssh://github.com/koralix-studios/step-functions.git")
                    url.set("https://github.com/Koralix-Studios/step-functions/tree/master")
                }
            }
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/koralix-studios/step-functions")
            credentials {
                username = System.getenv("GITHUB_ACTOR") ?: project.findProperty("gpr.user") as String?
                password = System.getenv("GITHUB_TOKEN") ?: project.findProperty("gpr.key") as String?
            }
        }
        maven {
            name = "sonatype"
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = System.getenv("OSSRH_USERNAME") ?: project.findProperty("ossrh.user") as String?
                password = System.getenv("OSSRH_PASSWORD") ?: project.findProperty("ossrh.password") as String?
            }
        }
    }
}

signing {
    if (
        System.getenv("SIGNING_KEY") != null &&
        System.getenv("SIGNING_PASSWORD") != null
    ) {
        useInMemoryPgpKeys(System.getenv("SIGNING_KEY"), System.getenv("SIGNING_PASSWORD"))
    } else if (
        project.findProperty("signing.keyId") == null ||
        project.findProperty("signing.password") == null ||
        project.findProperty("signing.secretKeyRingFile") == null
    ) {
        println("Signing key is not configured and not available in env")
        return@signing
    } else {
        useGpgCmd()
    }

    println("Signing key is configured")

    sign(publishing.publications["mavenJava"])
}
