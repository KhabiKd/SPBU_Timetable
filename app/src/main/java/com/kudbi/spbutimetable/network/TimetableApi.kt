package com.kudbi.spbutimetable.network

import com.example.spbutimetableonapi.network.model.Faculty
import com.example.spbutimetableonapi.network.model.GroupLessons
import com.example.spbutimetableonapi.network.model.Groups
import com.example.spbutimetableonapi.network.model.StudyLevel
import com.kudbi.spbutimetable.util.TypeTimetable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TimetableAPI {
    @GET("api/v1/study/divisions")
    suspend fun getFaculties(): List<Faculty>

    @GET("api/v1/study/divisions/{alias}/programs/levels")
    suspend fun getPrograms(@Path("alias") alias: String): List<StudyLevel>

    @GET("api/v1/programs/{studyProgramId}/groups")
    suspend fun getGroups(@Path("studyProgramId") studyProgramId: String): Groups

    @GET("api/v1/groups/{studentGroupId}/events/{startDate}/{endDate}")
    suspend fun getLessons(
        @Path("studentGroupId") studentGroupId: String,
        @Path("startDate") startDate: String,
        @Path("endDate") endDate: String,
        @Query("timetable") type: TypeTimetable
    ): GroupLessons
}