package org.example.liferpg

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform