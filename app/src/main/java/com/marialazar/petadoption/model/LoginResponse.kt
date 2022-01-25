package com.marialazar.petadoption.model

import com.marialazar.petadoption.model.UserEntity

data class LoginResponse(
    val token: String,
    val user: UserEntity
)