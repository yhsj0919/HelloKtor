package xyz.yhsj.ktor.koin

import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import xyz.yhsj.ktor.service.CompanyService
import xyz.yhsj.ktor.service.PermissionService
import xyz.yhsj.ktor.service.AdminService
import xyz.yhsj.ktor.service.UserService

val koinModule = module {
    single { KMongo.createClient("mongodb://127.0.0.1:27017").coroutine }

    single { AdminService(db = get()) }
    single { CompanyService(db = get()) }
    single { UserService(db = get()) }
    single { PermissionService(db = get()) }
}