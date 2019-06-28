/*
 * DisPay: Discord Currency API
 * Copyright (C) 2019  Brett Bender & Avery Clifton
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

plugins {
    java
    application
    id("com.github.johnrengelman.shadow") version "5.0.0"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_9
    targetCompatibility = JavaVersion.VERSION_1_9
}

application {
    mainClassName = "xyz.dispay.DisPay"
}

repositories {
    jcenter()
    maven(url = "https://jitpack.io")
}

dependencies {
    // https://github.com/DV8FromTheWorld/JDA
    implementation("net.dv8tion:JDA:4.BETA.0_11") {
        // We're not using anything audio related
        exclude(module = "opus-java")
    }
    // https://logback.qos.ch
    implementation("ch.qos.logback:logback-classic:1.2.3")
    // https://github.com/perwendel/spark
    implementation("com.sparkjava:spark-core:2.9.1")
    // https://github.com/google/guava
    implementation("com.google.guava:guava:28.0-jre")
    // https://github.com/stleary/JSON-java
    implementation("org.json:json:20180813")
    // https://github.com/xetorthio/jedis
    implementation("redis.clients:jedis:3.0.1")
    // https://github.com/ronmamo/reflections
	implementation("org.reflections:reflections:0.9.11")
    // https://github.com/jwtk/jjwt
    implementation("io.jsonwebtoken:jjwt-api:0.10.6")
    runtime("io.jsonwebtoken:jjwt-impl:0.10.6")
    runtime("io.jsonwebtoken:jjwt-jackson:0.10.6")
}
