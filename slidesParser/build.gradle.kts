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
        implementation(projects.sharedLogic)

        implementation(libs.kotlinx.coroutinesSwing)
        implementation(libs.kotlinx.serialization.json)
        implementation(libs.apache.poi.ooxml)
    }
}
