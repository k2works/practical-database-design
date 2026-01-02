plugins {
    java
    jacoco
    checkstyle
    pmd
    id("org.springframework.boot") version "4.0.0"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.github.spotbugs") version "6.0.27"
}

group = "com.example.sms"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

// バージョン管理
val mybatisVersion = "4.0.0"
val testcontainersVersion = "1.20.4"

dependencies {
    // === implementation ===
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")

    // Database
    implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter:$mybatisVersion")
    implementation("org.springframework.boot:spring-boot-starter-flyway")
    implementation("org.flywaydb:flyway-database-postgresql")

    // === runtimeOnly ===
    runtimeOnly("org.postgresql:postgresql")

    // === compileOnly ===
    compileOnly("org.projectlombok:lombok")

    // === annotationProcessor ===
    annotationProcessor("org.projectlombok:lombok")

    // === testImplementation ===
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.mybatis.spring.boot:mybatis-spring-boot-starter-test:$mybatisVersion")
    testImplementation(platform("org.testcontainers:testcontainers-bom:$testcontainersVersion"))
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")

    // === testCompileOnly ===
    testCompileOnly("org.projectlombok:lombok")

    // === testAnnotationProcessor ===
    testAnnotationProcessor("org.projectlombok:lombok")

    // === testRuntimeOnly ===
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
    // TestContainers の共有を有効化
    jvmArgs("-Dtestcontainers.reuse.enable=true")
    // テストを順次実行（並列実行しない）
    maxParallelForks = 1
}

// JaCoCo
jacoco {
    toolVersion = "0.8.14" // Java 25 support
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required = true
        html.required = true
    }
    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) {
                exclude(
                    "**/Application.class",
                    "**/Application$*.class"
                )
            }
        })
    )
}

// Checkstyle
checkstyle {
    toolVersion = "10.20.2"
    configFile = file("${rootDir}/config/checkstyle/checkstyle.xml")
    isIgnoreFailures = false
}

// SpotBugs (Java 25 対応: 4.9.7+)
spotbugs {
    ignoreFailures = false
    excludeFilter = file("${rootDir}/config/spotbugs/exclude.xml")
    toolVersion = "4.9.8"
}

tasks.withType<com.github.spotbugs.snom.SpotBugsTask> {
    reports.create("html") {
        required = true
    }
    reports.create("xml") {
        required = true
    }
}

// PMD (Java 25 対応: 7.16.0+)
pmd {
    toolVersion = "7.16.0"
    isConsoleOutput = true
    ruleSetFiles = files("${rootDir}/config/pmd/ruleset.xml")
    ruleSets = listOf()
    isIgnoreFailures = false
}

// カスタムタスク: TDD用の継続的テスト実行
tasks.register<Test>("tdd") {
    description = "Run tests in TDD mode (always executes)"
    group = "verification"
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
    outputs.upToDateWhen { false }
}

// カスタムタスク: 品質チェック全実行
tasks.register("qualityCheck") {
    description = "Run all quality checks (Checkstyle, PMD, SpotBugs)"
    group = "verification"
    dependsOn("checkstyleMain", "checkstyleTest", "pmdMain", "pmdTest", "spotbugsMain", "spotbugsTest")
}

// カスタムタスク: すべてのテストと品質チェックを実行
tasks.register("fullCheck") {
    description = "Run all tests and quality checks"
    group = "verification"
    dependsOn("test", "qualityCheck", "jacocoTestReport")
}

// カスタムタスク: Seed データを投入（default プロファイルで実行）
tasks.register<JavaExec>("seedData") {
    group = "application"
    description = "Seed データを投入する（default プロファイル）"
    mainClass.set("com.example.sms.Application")
    classpath = sourceSets["main"].runtimeClasspath
}
