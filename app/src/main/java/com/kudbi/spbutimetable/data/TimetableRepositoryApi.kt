package com.kudbi.spbutimetable.data

import com.kudbi.spbutimetable.network.model.Faculty
import com.kudbi.spbutimetable.network.model.Groups
import com.kudbi.spbutimetable.network.model.StudyLevel
import com.kudbi.spbutimetable.network.TimetableAPI
import com.kudbi.spbutimetable.network.model.EducatorLessons
import com.kudbi.spbutimetable.network.model.Educators
import com.kudbi.spbutimetable.network.model.GroupLessons
import com.kudbi.spbutimetable.network.model.LessonsDay
import com.kudbi.spbutimetable.util.TypeTimetable

class TimetableRepositoryApi(
    private val timetableAPI: TimetableAPI
) {
    suspend fun getFaculties(): List<Faculty> = timetableAPI.getFaculties()

    suspend fun getPrograms(alias: String): List<StudyLevel> = timetableAPI.getPrograms(alias)

    suspend fun getGroups(studyProgramId: String): Groups = timetableAPI.getGroups(studyProgramId)

    suspend fun getLessons(groupId: String, startDate: String, endDate: String): GroupLessons = timetableAPI.getLessons(groupId, startDate, endDate)

    suspend fun getEducators(query: String): Educators = timetableAPI.searchEducator(query)

    suspend fun getEducatorLessons(id: String, startDate: String, endDate: String): EducatorLessons = timetableAPI.getEducatorLessons(id, startDate, endDate)
}