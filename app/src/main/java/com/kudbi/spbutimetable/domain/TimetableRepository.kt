package com.kudbi.spbutimetable.domain

import com.kudbi.spbutimetable.domain.model.FacultyInfo
import com.kudbi.spbutimetable.domain.model.GroupInfo
import com.kudbi.spbutimetable.domain.model.LessonInfo
import com.kudbi.spbutimetable.domain.model.ProgramInfo

interface TimetableRepository {
    suspend fun getFaculties(): List<FacultyInfo>
    suspend fun getPrograms(facultyPath: String): List<ProgramInfo>
    suspend fun getGroups(programPath: String): List<GroupInfo>
    suspend fun getLessons(groupPath: String, date: String): List<LessonInfo>
}