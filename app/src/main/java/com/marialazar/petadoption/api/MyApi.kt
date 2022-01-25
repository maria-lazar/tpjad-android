package com.marialazar.petadoption.api

import com.marialazar.petadoption.api.Result
import com.marialazar.petadoption.model.LoginResponse
import com.marialazar.petadoption.model.Team
import com.marialazar.petadoption.model.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

object MyApi {

    interface Service {
        @GET("/api/teams")
        suspend fun findAllTeams(): List<Team>

        @POST("/login")
        suspend fun login(@Body user: User): LoginResponse
    }

    val service: Service = Api.retrofit.create(Service::class.java)

    suspend fun login(user: User): Result<LoginResponse> {
        try {
            return Result.Success(service.login(user))
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }

    suspend fun getAllTeams(): Result<List<Team>> {
        try {
            return Result.Success(service.findAllTeams())
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }
}