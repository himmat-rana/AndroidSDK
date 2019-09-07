package io.supportgenie.androidlibrary.data.network.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class CompanyAgentsWrapper {
    lateinit var agents: List<AgentDto> // Navigates down the ‘list’ object
}

@JsonClass(generateAdapter = true)
class AgentDto {
    lateinit var companyId: String
    lateinit var userId: String
    lateinit var updatedAt: String
    lateinit var createdAt: String
    var isActive: Boolean = true
    var isAvailable: Boolean = true
    lateinit var loginId: String
    lateinit var name: String
    lateinit var email: String
    lateinit var status: String
    lateinit var photoUrl: String
}




