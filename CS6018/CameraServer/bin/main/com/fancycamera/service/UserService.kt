package com.fancycamera.service

import com.fancycamera.model.User
import com.fancycamera.repository.UserRepository

class UserService(private val userRepository: UserRepository) {

    fun save(username: String, password: String): Boolean =
        userRepository.save(username, password)

    fun findByUsername(username: String): User? =
        userRepository.findByUsername(username)
}
