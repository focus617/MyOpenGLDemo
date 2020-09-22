
plugins {

    `kotlin-dsl`

    //kotlin("jvm") version "1.3.72"
    id("org.jetbrains.kotlin.jvm") version "1.3.72"

    // Apply the java-library plugin to add support for Java Library
    `java-library`

    // Using the Plugin Development plugin for writing plugins
    id("java-gradle-plugin")

    // used for distribution
    `maven-publish`

}

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}


//设置项目的依赖库
dependencies {
    implementation(gradleApi())
    implementation(kotlin("stdlib"))
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation(gradleKotlinDsl())

    // Git
    implementation("org.eclipse.jgit:org.eclipse.jgit:5.6.1.202002131546-r")
    implementation("com.jcraft:jsch.agentproxy.jsch:0.0.9")
    implementation("com.jcraft:jsch.agentproxy.usocket-jna:0.0.9")
    implementation("com.jcraft:jsch.agentproxy.sshagent:0.0.9")

    // Use JUnit test framework
    testImplementation("junit:junit:4.13")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.3.72")
    testImplementation("org.jetbrains.kotlin", "kotlin-test-junit", "1.3.72")
    testImplementation(gradleTestKit())
}

//指定Jar中央仓库
repositories {
    maven {
        url = uri("https://repo.gradle.org/gradle/libs")
    }
    // Use jcenter for resolving dependencies.
    jcenter()

    // 阿里云的国内镜像仓库
    //maven(url="http://maven.aliyun.com/nexus/content/groups/public/")
    //mavenLocal()
    // You can declare any Maven/Ivy/file repository here.

}

sourceSets {
    test{
        java{
            srcDirs("src/test/kotlin")
        }
    }
}
