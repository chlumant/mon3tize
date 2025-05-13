import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("maven-publish")
    id("signing")
    id("org.jetbrains.dokka") version "2.0.0"
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])

                groupId = "io.github.chlumant"
                artifactId = "mon3tize"
                version = "0.1.0"

                pom {
                    name.set("Mon3tize")
                    description.set("A library for monetizing your app with ads, in-app purchases, and freemium features.")
                    url.set("https://github.com/chlumant/mon3tize")
                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                    developers {
                        developer {
                            id.set("chlumant")
                            name.set("chlumant")
                            email.set("chlumant@cvut.cz")
                        }
                    }
                    scm {
                        connection.set("scm:git:git://github.com/chlumant/mon3tize.git")
                        developerConnection.set("scm:git:ssh://git@github.com:chlumant/mon3tize.git")
                        url.set("https://github.com/chlumant/mon3tize")
                    }
                }
            }
        }

        repositories {
            maven {
                name = "OSSRH"
                url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                credentials {
                    username = findProperty("ossrhUsername") as String
                    password = findProperty("ossrhPassword") as String
                }
            }
        }
    }

    signing {
        useInMemoryPgpKeys(
            findProperty("signing_keyId") as String,
            findProperty("signing_secretKey") as String,
            findProperty("signing_password") as String
        )
        sign(publishing.publications["release"])
    }
}

android {
    namespace = "cz.cvut.fit.chlumant.mon3tize"
    compileSdk = 36

    group = "io.github.chlumant"
    version = "0.1.0"

    defaultConfig {
        minSdk = 26
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlin {
        explicitApi = ExplicitApiMode.Strict
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

dependencies {
    implementation(libs.navigation.compose)
    implementation(libs.datastore)
    implementation(libs.billing)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.play.services.ads)

    implementation(libs.billingclient.billing)

    implementation(libs.jetpack.credentials)
    implementation(libs.androidx.credentials.play.services.auth)



    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)
    implementation(libs.play.services.auth)
    implementation(libs.jetpack.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
}