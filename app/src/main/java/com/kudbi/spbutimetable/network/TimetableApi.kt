package com.kudbi.spbutimetable.network

import com.kudbi.spbutimetable.network.model.EducatorLessons
import com.kudbi.spbutimetable.network.model.Educators
import com.kudbi.spbutimetable.network.model.Faculty
import com.kudbi.spbutimetable.network.model.GroupLessons
import com.kudbi.spbutimetable.network.model.Groups
import com.kudbi.spbutimetable.network.model.StudyLevel
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
//        @Query("timetable") type: TypeTimetable
    ): GroupLessons

    @GET("api/v1/educators/search/{query}")
    suspend fun searchEducator(
        @Path("query") query: String
    ): Educators

    @GET("api/v1/educators/{id}/events/{startDate}/{endDate}")
    suspend fun getEducatorLessons(
       @Path("id") id: String,
       @Path("startDate") startDate: String,
       @Path("endDate") endDate: String,
    ): EducatorLessons
}