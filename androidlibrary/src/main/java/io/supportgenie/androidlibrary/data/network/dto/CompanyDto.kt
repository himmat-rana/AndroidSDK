package io.supportgenie.androidlibrary.data.network.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class CompanyDto {
    lateinit var companyId: String
    lateinit var name: String
    lateinit var photoUrl: String
    lateinit var website: String
    lateinit var updatedAt: String
}

