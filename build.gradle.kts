plugins {
    id("com.android.library") version "8.8.2" apply false
    id("org.jetbrains.kotlin.android") version "2.0.21" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21" apply false
    id("com.google.devtools.ksp") version "2.0.21-1.0.27" apply false
}

// Publish coordinates for :sniffer (read by sniffer/build.gradle.kts)
extra["publicationVersion"] = project.findProperty("VERSION_NAME")?.toString() ?: "0.1.0"
extra["publicationGroup"] = project.findProperty("GROUP")?.toString() ?: "dev.sniffer"

tasks.register("publishToMavenLocal") {
    group = "publishing"
    description = "Publishes Sniffer to Maven Local (~/.m2/repository)"
    dependsOn(":sniffer:publishReleasePublicationToMavenLocal")
}
