package com.fancycamera

import com.fancycamera.plugins.configureSecurity
import com.fancycamera.plugins.configureSerialization
import com.fancycamera.repository.PhotoRepository
import com.fancycamera.repository.UserRepository
import com.fancycamera.routing.configureRouting
import com.fancycamera.service.JwtService
import com.fancycamera.service.UserService
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val ldapUrl = environment.config.propertyOrNull("ldap.url")?.getString() ?: "ldap://ldap:389"
    val userRepository = UserRepository(ldapUrl)
    val userService = UserService(userRepository)
    val jwtService = JwtService(this, userService, userRepository)
    val photoRepository = PhotoRepository()

    configureSerialization()
    configureSecurity(jwtService)
    configureRouting(jwtService, userService, photoRepository, userRepository)
}
