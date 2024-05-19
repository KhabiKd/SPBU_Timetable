package com.kudbi.spbutimetable.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Groups(
    @SerialName("Id") val id: Int,
    @SerialName("Groups") val groups: List<Group>
)

@Serializable
data class Group(
    @SerialName("StudentGroupId") val studentGroupId: Int,
    @SerialName("StudentGroupName") val studentGroupName: String
)
