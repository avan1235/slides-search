import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import java.lang.System.getenv

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinxSerialization)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xexplicit-backing-fields")
    }

    dependencies {
        implementation(projects.app.sharedUi)
        implementation(projects.slidesParser.core)

        implementation(compose.desktop.currentOs)
        implementation(libs.kotlinx.coroutinesSwing)
        implementation(libs.kotlinx.serialization.json)

        implementation(libs.compose.uiToolingPreview)
        implementation(libs.androidx.lifecycle.viewmodelCompose)

        implementation(libs.lucene.core)
        implementation(libs.lucene.analysis.common)
        implementation(libs.lucene.queryparser)
    }
}

compose.desktop {
    application {
        mainClass = "in.procyk.slides.MainKt"
        version = getenv("VERSION") ?: "1.0.0"

        nativeDistributions {
            targetFormats(TargetFormat.Msi, TargetFormat.Deb, TargetFormat.Dmg)
            packageName = "SlidesSearch"

            windows {
                menu = true
                upgradeUuid = "2e52be3f-064e-48df-b516-0664e7678416"
//                iconFile.set(projectDir.resolve("src/jvmMain/resources/ic_launcher.ico"))
            }

            linux {
//                iconFile.set(projectDir.resolve("src/jvmMain/resources/ic_launcher.png"))
            }

            macOS {
                bundleID = "in.procyk.slides"
                appStore = false
//                iconFile.set(projectDir.resolve("src/jvmMain/resources/ic_launcher.icns"))
                signing {
                    sign.set(false)
                }
            }
        }
    }
}