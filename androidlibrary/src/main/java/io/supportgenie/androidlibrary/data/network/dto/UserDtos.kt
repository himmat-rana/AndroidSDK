package io.supportgenie.androidlibrary.data.network.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class AppUsersWrapper {
    lateinit var companies: List<CompanyDto> // Navigates down the ‘list’ object
    lateinit var users: List<UserDto> // Navigates down the ‘item’ array
    var userData: UserDataDto? = null  // Navigates down the ‘item’ array
}

@JsonClass(generateAdapter = true)
class UserDto {
    lateinit var companyId: String
    lateinit var userId: String
    lateinit var updatedAt: String
    var isActive: Boolean = true
}

@JsonClass(generateAdapter = true)
class UserDataDto {
    lateinit var userId: String
    var isActive: Boolean = true
    lateinit var phone: String
    lateinit var photoUrl: String
}   lateinit var updatedAt: String



