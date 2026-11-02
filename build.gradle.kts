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
    implementation("io.netty:netty-codec-native-quic:4.2.19.Final:linux-x86_64")
    implementation("io.netty:netty-codec-native-quic:4.2.19.Final:linux-aarch_64")
    implementation("io.netty:netty-codec-native-quic:4.2.19.Final:osx-x86_64")
    implementation("io.netty:netty-codec-native-quic:4.2.19.Final:osx-aarch_64")
    implementation("io.netty:netty-codec-native-quic:4.2.19.Final:windows-x86_64")
    implementation("org.jspecify:jspecify:1.0.0")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.junit.platform:junit-platform-launcher")
    testImplementation("io.netty:netty-pkitesting:4.2.19.Final")
}

tasks {
    test {
        useJUnitPlatform()
    }
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

extensions.configure<PublishingExtension> {
    repositories {
        maven {
            name = "modoruSnapshots"
            url = uri("https://repository.modoru.fun/snapshots")

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

            group = "fun.modoru"
            artifactId = "musubi"
            version = System.getenv("COMMIT")
        }
    }
}