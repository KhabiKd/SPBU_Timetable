package com.kudbi.spbutimetable.data

import com.kudbi.spbutimetable.domain.TimetableRepository
import com.kudbi.spbutimetable.domain.model.FacultyInfo
import com.kudbi.spbutimetable.domain.model.GroupInfo
import com.kudbi.spbutimetable.domain.model.LessonInfo
import com.kudbi.spbutimetable.domain.model.ProgramInfo
import com.kudbi.spbutimetable.domain.model.Result
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class TimetableRepositoryImpl: TimetableRepository {

    private val baseUrl = "https://timetable.spbu.ru"
    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()

    override suspend fun getFaculties(): List<FacultyInfo> {
        return when(val document = loadPage(baseUrl)) {
            is Result.Success -> {
                val facultiesPage = document.data
                parseFaculties(facultiesPage)
            }
            is Result.Error -> {
                emptyList()
            }
        }
    }

    override suspend fun getPrograms(facultyPath: String): List<ProgramInfo> {
        return when(val document = loadPage("$baseUrl$facultyPath")) {
            is Result.Success -> {
                val programPage = document.data
                parsePrograms(programPage)
            }
            is Result.Error -> {
                emptyList()
            }
        }
    }

    override suspend fun getGroups(programPath: String): List<GroupInfo> {
        return when(val document = loadPage("$baseUrl$programPath")) {
            is Result.Success -> {
                val groupPage = document.data
                parseGroups(groupPage)
            }
            is Result.Error -> {
                listOf(GroupInfo("0", "0"))
            }
        }
    }

    override suspend fun getLessons(groupPath: String, date: String): List<LessonInfo> = withContext(Dispatchers.IO) {
        val lessonsList = mutableListOf<LessonInfo>()
        val regex = "^/([^/]+)/.+/(\\d+)$".toRegex()
        val matchResult = regex.find(groupPath)
        val (faculty, group) = matchResult!!.destructured
        try {
            val snapshot = firebaseDatabase.reference
                .child("faculties")
                .child(faculty)
                .child("groups")
                .child(group)
                .child("lessons")
                .orderByChild("date")
                .equalTo(date)
                .get()
                .await()

            for (lessonSnapshot in snapshot.children) {
                val lesson = lessonSnapshot.getValue(LessonInfo::class.java)
                if (lesson != null) {
                    lessonsList.add(lesson)
                }
            }
        } catch (e: Exception) {
            throw e
        }
        lessonsList
    }


    private suspend fun loadPage(url: String): Result<Document> {
        return withContext(Dispatchers.IO) {
            try {
                val document = Jsoup.connect(url).header("Accept-Language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7").get()
                Result.Success(document)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    private fun parseFaculties(page: Document): List<FacultyInfo> {
        val facultiesList = page.select("div.row div.col-sm-6:has(div.panel-heading:contains(Направления)) a")
        return facultiesList.map { faculty ->
            val facultyName = faculty.text()
            val facultyPath = faculty.attr("href")
            FacultyInfo(facultyName, facultyPath)
        }
    }

    private fun parsePrograms(page: Document): List<ProgramInfo> {
        val programList = mutableListOf<ProgramInfo>()
        val node = page.select("div.panel-group").first()!!.children()
        for (i in 0 until node.size) {
            val degree = node[i].select("h4.panel-title").text()
            val childNode = node[i].select("li.common-list-item.row")
            for (j in 1 until childNode.size) {
                val programName = childNode[j].select("div.col-sm-5").text()
                val childChildNode = childNode[j].children()
                for (k in 1 until childChildNode.size) {
                    val programYear = childChildNode[k].text()
                    val programPath = childChildNode[k].select("a").attr("href")
                    programList.add(ProgramInfo(degree, programName, programYear, programPath))
                }
            }
        }
        return programList
    }

    private fun parseGroups(page: Document): List<GroupInfo> {
        val groupList = mutableListOf<GroupInfo>()
        val node = page.select("ul#studentGroupsForCurrentYear li.common-list-item.row")
        node.forEach {
            val groupName = it.select("div.col-sm-4").text()
            val groupPath = it.select("div.tile").attr("onclick").substringAfter("'").substringBefore("'")
            groupList.add(GroupInfo(groupName, groupPath))
        }
        return groupList
    }
}