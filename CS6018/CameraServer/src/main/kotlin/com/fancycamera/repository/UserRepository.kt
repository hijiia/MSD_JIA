package com.fancycamera.repository

import com.fancycamera.model.User
import com.fancycamera.service.LoginRequest
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.UserPasswordCredential
import io.ktor.server.auth.ldap.ldapAuthenticate
import java.util.*
import javax.naming.Context
import javax.naming.directory.BasicAttribute
import javax.naming.directory.BasicAttributes
import javax.naming.directory.InitialDirContext
import javax.naming.ldap.LdapName

class UserRepository(private val ldapUrl: String) {

    private val baseDn = "dc=fancycamera,dc=msd,dc=localhost"
    private val adminDn = "cn=admin,$baseDn"
    private val adminPassword = "fancycamerapass"

    /**
     * Find a user by username in LDAP
     */
    fun findByUsername(username: String): User? {
        println("UserRepository.findByUsername: $username")
        try {
            val dc = InitialDirContext(Hashtable<String, Any?>().apply {
                this[Context.INITIAL_CONTEXT_FACTORY] = "com.sun.jndi.ldap.LdapCtxFactory"
                this[Context.PROVIDER_URL] = ldapUrl
                // The OpenLDAP container doesn't allow anonymous searches
                // so we must log in as admin here, just to do a query
                this[Context.SECURITY_CREDENTIALS] = adminPassword
                this[Context.SECURITY_PRINCIPAL] = adminDn
            })

            val answer = dc.search(
                baseDn,
                BasicAttributes(true).apply {
                    put(BasicAttribute("cn", username))
                },
                arrayOf("cn")
            )

            val answerList = answer.toList()
            println("LDAP search result for '$username': ${answerList.size} entries found")

            return if (answerList.size == 1) {
                val foundUsername = answerList.first().attributes["cn"].toString()
                // Create a User object with minimal info - LDAP stores the password
                // Use nameUUIDFromBytes to generate consistent UUID from username
                User(
                    id = UUID.nameUUIDFromBytes(foundUsername.toByteArray()),
                    username = foundUsername,
                    hashedPassword = "" // Password is stored in LDAP, not in our model
                )
            } else {
                null
            }
        } catch (ex: Exception) {
            println("LDAP search failed: ${ex.message}")
            println("Stack trace: ${ex.stackTraceToString()}")
            return null
        }
    }

    /**
     * Save a new user to LDAP
     */
    fun save(username: String, password: String): Boolean {
        try {
            // Admin context - log in as admin
            val env = Hashtable<String?, Any?>()
            env[Context.INITIAL_CONTEXT_FACTORY] = "com.sun.jndi.ldap.LdapCtxFactory"
            env[Context.PROVIDER_URL] = ldapUrl
            env[Context.SECURITY_CREDENTIALS] = adminPassword
            env[Context.SECURITY_PRINCIPAL] = adminDn

            val dirContext = InitialDirContext(env)

            // Create info about the new user
            val cn = username
            val userDn = LdapName("cn=$cn,$baseDn")
            val attributes = BasicAttributes(true).apply {
                put(BasicAttribute("cn", cn))
                put(BasicAttribute("sn", "User")) // Surname is required for inetOrgPerson
                put("userPassword", password)
                put(BasicAttribute("objectClass").apply {
                    add("inetOrgPerson")
                })
            }

            // Add that user info to LDAP
            dirContext.createSubcontext(userDn, attributes)
            println("Successfully created LDAP user: $username")
            return true

        } catch (ex: Exception) {
            println("Create user failed: ${ex.message}")
            println("Stack trace: ${ex.stackTraceToString()}")
            return false
        }
    }

    /**
     * Authenticate a user against LDAP
     */
    fun ldapAuth(loginRequest: LoginRequest): UserIdPrincipal? {
        val pwdCred = UserPasswordCredential(loginRequest.username, loginRequest.password)
        val userDn = nameToDN(loginRequest.username).toString()
        println("Attempting LDAP authentication for: $userDn")

        return try {
            ldapAuthenticate(pwdCred, ldapUrl, userDn)
        } catch (ex: Exception) {
            println("LDAP authentication failed: ${ex.message}")
            null
        }
    }

    private fun nameToDN(name: String) = LdapName("cn=$name,$baseDn")
}
