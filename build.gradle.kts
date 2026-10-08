plugins {
    id("java-library")
    id("maven-publish")
}

repositories {
    mavenCentral()
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(findProperty("java") as String))
}

dependencies {
    compileOnly("io.netty:netty-all:4.2.19.Final")
    compileOnly("org.jspecify:jspecify:1.0.0")
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

extensions.configure<PublishingExtension> {
    repositories {
        maven {
            name = "modoruReleases"
            url = uri("https://repository.modoru.fun/releases")

            credentials {
                username = System.getenv("MODORU_USERNAME") ?: ""
                password = System.getenv("MODORU_TOKEN") ?: ""
            }
        }
    }

    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            artifact(sourcesJar.get())

            group = project.group.toString();
            artifactId = "network-protocol"
            version = project.version.toString()
        }
    }
}