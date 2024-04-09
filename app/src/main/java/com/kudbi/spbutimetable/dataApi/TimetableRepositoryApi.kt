package com.kudbi.spbutimetable.dataApi

import com.example.spbutimetableonapi.network.model.Faculty
import com.example.spbutimetableonapi.network.model.Groups
import com.example.spbutimetableonapi.network.model.StudyLevel
import com.kudbi.spbutimetable.network.TimetableAPI

class TimetableRepositoryApi(
    private val timetableAPI: TimetableAPI
) {
    suspend fun getFaculties(): List<Faculty> = timetableAPI.getFaculties()

    suspend fun getPrograms(alias: String): List<StudyLevel> = timetableAPI.getPrograms(alias)

    suspend fun getGroups(studyProgramId: String): Groups = timetableAPI.getGroups(studyProgramId)
}