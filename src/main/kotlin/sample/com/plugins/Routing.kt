package sample.com.plugins

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import sample.com.services.CalculateServiceImpl

fun Application.configureRouting() {

    routing {
        get("/{element}") {
            val element = this.context.request.path().split("/").last().toInt()
            call.respondText(CalculateServiceImpl.findElementInList(element).toString())
        }
    }
}
