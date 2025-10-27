package com.example.camera.server.service

import com.example.camera.server.model.User
import com.example.camera.server.routing.request.LoginRequest
import com.example.camera.server.routing.request.UserRequest
import com.example.camera.server.util.hashPassword
import at.favre.lib.crypto.bcrypt.BCrypt
import java.util.concurrent.ConcurrentHashMap

class UserService {

    /** username -> hashedPassword */
    private val users = ConcurrentHashMap<String, String>()

    /**
     *
     *  userRoute(post)：
     *   val createdUser = userService.save(userRequest) ?: respond(BadRequest)
     */
    fun save(user: UserRequest): User? {
        val username = user.username.trim()
        if (username.isEmpty()) return null
        return if (register(user)) User(username = username) else null
    }

    /**
     * if user exist, true
     */
    fun register(request: UserRequest): Boolean {
        val username = request.username.trim()
        if (username.isEmpty()) return false
        if (users.containsKey(username)) return false

        val hashed = hashPassword(request.password)
        users[username] = hashed
        return true
    }

    /**
     *
     */
    fun validateCredentials(login: LoginRequest): Boolean {
        return validateCredentials(login.username, login.password)
    }

    /**
     *
     */
    fun validateCredentials(username: String, password: String): Boolean {
        val stored = users[username] ?: return false
        return BCrypt.verifyer().verify(password.toCharArray(), stored).verified
    }

    /**
     *
     */
    fun getAllUsers(): List<User> =
        users.keys.map { User(username = it) }
}
