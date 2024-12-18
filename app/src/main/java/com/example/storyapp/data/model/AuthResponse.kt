package com.example.storyapp.data.model

import com.google.gson.annotations.SerializedName

data class RegisterResponse(

    @field:SerializedName("error") val error: Boolean,

    @field:SerializedName("message") val message: String
)

data class LoginResponse(

    @field:SerializedName("error") val error: Boolean? = null,

    @field:SerializedName("message") val message: String? = null,

    @field:SerializedName("loginResult") val loginResult: LoginResult? = null
)

data class LoginResult(

    @field:SerializedName("userId") val userId: String? = null,

    @field:SerializedName("name") val name: String? = null,

    @field:SerializedName("token") val token: String? = null
)

data class ErrorResponse(
    @field:SerializedName("error") val error: Boolean? = null,
    @field:SerializedName("message") val message: String? = null
)