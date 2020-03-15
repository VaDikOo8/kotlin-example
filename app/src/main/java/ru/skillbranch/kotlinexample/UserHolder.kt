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
    ): User =
        if (rawPhone.isPhoneType()) {
            User.makeUser(fullName, phone = rawPhone)
                .also { user ->
                    when {
                        !map.containsKey(user.login) -> map[user.login] = user
                        else -> throw IllegalArgumentException("A user with this phone already exists")
                    }
                }
        } else throw IllegalArgumentException(
            "Enter a valid phone number starting with a + and containing 11 digits"
        )

    fun loginUser(login: String, password: String): String? =
        map[login.format()]?.let {
            if (it.checkPassword(password)) it.userInfo
            else null
        }

    fun requestAccessCode(login: String) {
        map[login.toPhoneType()]?.also {
            val accessCode = it.accessCode
            it.changePassword(accessCode!!, it.generateAccessCode())
        }
    }

    fun importUsers(list: List<String>): List<User> {
        val userList = arrayListOf<User>()
        for (user in list) {
            val userData: List<String> = user.split(";").map { it.trim() }
            userList.add(
                User.makeUser(
                    userData[0],
                    email = userData[1].ifBlank { null },
                    phone = userData[3].ifBlank { null },
                    passHashWithSalt = userData[2].ifBlank { null }
                ).also { user ->
                    when {
                        !map.containsKey(user.login) -> map[user.login] = user
                        else -> throw IllegalArgumentException("A user with this data already exists")
                    }
                    println("userInfo: \n ${user.userInfo}")
                }
            )
        }
        return userList
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun clearHolder() {
        map.clear()
    }

    private fun String.isPhoneType(): Boolean {
        return this.toPhoneType().matches("""\+\d{11}""".toRegex())
    }

    private fun String.toPhoneType() = this.trim().replace("""[^+\d]""".toRegex(), "")

    private fun String.format() =
        if (this.isPhoneType()) this.toPhoneType()
        else this.trim()
}

