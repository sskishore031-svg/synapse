package com.siamts.synapse.data.local

import com.siamts.synapse.data.model.UserDto

interface UserDao {
    suspend fun insertOrUpdateUser(name: String, email: String, phone: String?): Long
    suspend fun getUserByEmail(email: String): UserDto?
    suspend fun getUserById(userId: Int): UserDto?
}

