package com.example.renteasy.data.model

import com.example.renteasy.utils.Constants

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val role: String = Constants.ROLE_TENANT
)
