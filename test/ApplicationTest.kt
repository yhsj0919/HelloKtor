package xyz.yhsj

import io.ktor.http.*
import kotlin.test.*
import io.ktor.server.testing.*
import xyz.yhsj.ktor.module

class ApplicationTest {
    @Test
    fun testRoot() {
        withTestApplication({ module() }) {
            handleRequest(HttpMethod.Get, "/").apply {
//                assertEquals(HttpStatusCode.OK, response.status())
//                assertEquals("HELLO WORLD!", response.content)
            }
        }
    }
}
