package com.getaride.android.vo

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class Authorization(
        @field:SerializedName("id")
        val id: String,
        @field:SerializedName("url")
        val url: String?,
        @field:SerializedName("token")
        val token: String?,
        @field:SerializedName("token_last_eight")
        val tokenLastEight: String?,
        @field:SerializedName("hashed_token")
        val hashedToken: String?,
        @field:SerializedName("note")
        val note: String?,
        @field:SerializedName("note_url")
        val noteUrl: String?,
        @field:SerializedName("updated_at")
        val updatedAt: LocalDateTime?,
        @field:SerializedName("created_at")
        val createdAt: LocalDateTime?
)
