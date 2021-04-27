package xyz.yhsj.ktor.plugins

import io.ktor.application.*
import xyz.yhsj.ktor.redis.Redis

/**
 * Redis
 */
fun Application.configureRedis() {
    install(Redis) {
        url = "redis://127.0.0.1:6379/0?timeout=15s"
    }
}