package com.kudbi.spbutimetable.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StudyLevel(
    @SerialName("StudyLevelName") val studyLevelName: String,
    @SerialName("StudyProgramCombinations") val studyProgramCombinations: List<StudyProgramCombination>
)

@Serializable
data class StudyProgramCombination(
    @SerialName("Name") val name: String,
    @SerialName("AdmissionYears") val admissionYears: List<StudyProgram>
)

@Serializable
data class StudyProgram(
    @SerialName("StudyProgramId") val id: Int,
    @SerialName("YearName") val year: String
)

