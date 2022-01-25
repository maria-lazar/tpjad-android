package com.marialazar.petadoption.model

data class UserEntity(
    val id: String,
    val badgeNumber: String,
    val name: String,
    val role: UserType,
    val team: Team
)
