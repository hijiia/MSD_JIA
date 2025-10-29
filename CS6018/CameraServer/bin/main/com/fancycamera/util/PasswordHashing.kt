package com.fancycamera.util

import at.favre.lib.crypto.bcrypt.BCrypt

fun hashPassword(password: String): String =
    BCrypt.withDefaults().hashToString(14, password.toCharArray())

fun verifyPassword(password: String, hashedPassword: String): Boolean =
    BCrypt.verifyer().verify(password.toCharArray(), hashedPassword).verified
