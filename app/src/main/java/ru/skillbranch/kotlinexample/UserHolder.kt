package ru.skillbranch.kotlinexample

import androidx.annotation.VisibleForTesting

object UserHolder {
    private val map = mutableMapOf<String, User>()

    fun registerUser(
        fullName: String,
        email: String,
        password: String
    ): User = User.makeUser(fullName, email = email, password = password)
        .also { user ->
            when {
                !map.containsKey(user.login) -> map[user.login] = user
                else -> throw IllegalArgumentException("A user with this email already exists")
            }
        }

    fun registerUserByPhone(
        fullName: String,
        rawPhone: String
    ): User = User.makeUser(fullName, phone = rawPhone)
        .also { user ->
            when {
                !map.containsKey(user.login) -> map[user.login] = user
                else -> throw IllegalArgumentException("A user with this phone already exists")
            }
        }

    fun loginUser(login: String, password: String): String? =
        if (login.trim().replace("""[^+\d]""".toRegex(), "").matches("""\+\d{11}""".toRegex()))
            map[login.trim().replace("""[^+\d]""".toRegex(), "")]?.let {
                if (it.checkPassword(password)) it.userInfo
                else null
            }
        else map[login.trim()]?.let {
            if (it.checkPassword(password)) it.userInfo
            else null
        }

    fun requestAccessCode(login: String) {
        map[login.trim().replace("""[^+\d]""".toRegex(), "")]?.also {
            val accessCode = it.accessCode
            it.changePassword(accessCode!!, it.generateAccessCode())
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun clearHolder() {
        map.clear()
    }
}

