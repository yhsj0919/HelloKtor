package xyz.yhsj.ktor.plugins

import io.ktor.application.*
import org.koin.dsl.module
import org.koin.ktor.ext.Koin
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import xyz.yhsj.ktor.service.CompanyService
import xyz.yhsj.ktor.service.PermissionService
import xyz.yhsj.ktor.service.UserService

fun Application.configureKoin() {
    //模块依赖注入
    install(Koin) {
        modules(koinModule)
    }
}


val koinModule = module {
    single { KMongo.createClient("mongodb://127.0.0.1:27017").coroutine }

    single { CompanyService(db = get()) }
    single { UserService(db = get()) }
    single { PermissionService(db = get()) }
}