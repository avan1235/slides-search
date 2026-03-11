plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlinxSerialization)
    application
}

application {
    mainClass = "in.procyk.slides.MainKt"
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xexplicit-backing-fields")
    }

    dependencies {
        implementation(projects.slidesParser.core)

        implementation(libs.kotlinx.serialization.json)
    }
}
