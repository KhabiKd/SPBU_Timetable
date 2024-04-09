package com.example.spbutimetableonapi.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GroupLessons(
    @SerialName("StudentGroupId") val studentGroupId: Int,
    @SerialName("StudentGroupDisplayName") val studentGroupDisplayName: String,
    @SerialName("Days") val days: LessonsDay
)

@Serializable
data class LessonsDay(
    @SerialName("Day") val day: String,
    @SerialName("DayString") val dayString: String,
    @SerialName("DayStudyEvents") val dayStudyEvents: Subject
)

@Serializable
data class Subject(
    @SerialName("Subject") val name: String,
    @SerialName("TimeIntervalString") val time: String,
    @SerialName("LocationsDisplayText") val location: String,
    @SerialName("EducatorsDisplayText") val educator: String,
    @SerialName("IsCancelled") val isCancelled: Boolean
)