package com.kudbi.spbutimetable.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Educators(
    @SerialName("EducatorLastNameQuery") val educatorLastNameQuery: String,
    @SerialName("Educators") val educators: List<Educator>
)

@Serializable
data class Educator(
    @SerialName("Id") val id: Int,
    @SerialName("DisplayName") val shortName: String,
    @SerialName("FullName") val fullName: String,
    @SerialName("Employments") val employments: List<Employment>
)

@Serializable
data class Employment(
    @SerialName("Position") val position: String,
    @SerialName("Department") val department: String
)

@Serializable
data class EducatorLessons(
    @SerialName("EducatorMasterId") val educatorMasterId: Int,
    @SerialName("EducatorDisplayText") val educatorDisplayText: String,
    @SerialName("EducatorEventsDays") val educatorEventsDays: List<LessonsDayEducator>
)

@Serializable
data class LessonsDayEducator(
    @SerialName("Day") val day: String,
    @SerialName("DayString") val dayString: String,
    @SerialName("DayStudyEvents") val dayStudyEvents: List<SubjectEducator>
)

@Serializable
data class SubjectEducator(
    @SerialName("Subject") val name: String,
    @SerialName("TimeIntervalString") val time: String,
    @SerialName("LocationsDisplayText") val location: String,
    @SerialName("ContingentUnitName") val groups: String,
    @SerialName("IsCancelled") val isCancelled: Boolean
)