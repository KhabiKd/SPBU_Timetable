package com.kudbi.spbutimetable.ui

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.kudbi.spbutimetable.network.model.Faculty
import com.kudbi.spbutimetable.network.model.Group
import com.kudbi.spbutimetable.network.model.StudyLevel
import com.kudbi.spbutimetable.network.model.StudyProgram
import com.kudbi.spbutimetable.network.model.StudyProgramCombination
import com.kudbi.spbutimetable.TimetableApplication
import com.kudbi.spbutimetable.data.TimetableRepositoryApi
import com.kudbi.spbutimetable.domain.model.HidedLesson
import com.kudbi.spbutimetable.network.model.LessonsDay
import com.kudbi.spbutimetable.network.model.Subject
import com.kudbi.spbutimetable.util.TypeUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

class TimetableViewModelApi(private val timetableRepositoryApi: TimetableRepositoryApi) : ViewModel() {

    var faculties = mutableStateListOf<Faculty>()
        private set
    var degrees = mutableStateListOf<StudyLevel>()
        private set
    var programs = mutableStateListOf<StudyProgramCombination>()
        private set
    var years = mutableStateListOf<StudyProgram>()
        private set
    var groups = mutableStateListOf<Group>()
        private set

    var days = mutableStateListOf<LessonsDay>()
        private set

    var lessons = mutableStateListOf<Subject>()
        private set

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun getFaculties() {
        faculties.clear()
        viewModelScope.launch {
            _isLoading.value = true
            faculties.addAll(timetableRepositoryApi.getFaculties())
            _isLoading.value = false
        }
    }

    fun getDegrees(alias: String) {
        degrees.clear()
        viewModelScope.launch {
            _isLoading.value = true
            degrees.addAll(timetableRepositoryApi.getPrograms(alias))
            _isLoading.value = false
        }
    }

    fun getPrograms(studyLevelName: String) {
        programs.clear()
        viewModelScope.launch {
            _isLoading.value = true
            programs.addAll(degrees.find { it.studyLevelName == studyLevelName }?.studyProgramCombinations ?: emptyList())
            _isLoading.value = false
        }
    }

    fun getYears(programName: String) {
        years.clear()
        viewModelScope.launch {
            _isLoading.value = true
            years.addAll(programs.find { it.name == programName }?.admissionYears ?: emptyList())
            _isLoading.value = false
        }
    }

    fun getGroups(studyProgramId: String) {
        groups.clear()
        viewModelScope.launch {
            _isLoading.value = true
            groups.addAll(timetableRepositoryApi.getGroups(studyProgramId).groups)
            _isLoading.value = false
        }
    }

    fun getLessons(groupId: String, date: LocalDate) {
        days.clear()
        lessons.clear()
        viewModelScope.launch {
            _isLoading.value = true

            val (formattedMonday, formattedSunday) = findMondayAndSunday(date)

            days.addAll(timetableRepositoryApi.getLessons(groupId, formattedMonday, formattedSunday).days)

            _isLoading.value = false
        }
    }

    fun getLessonsByDay(dayStudyEvents: List<Subject>, hidedLessons: Set<HidedLesson>) {
        val filteredLessons = dayStudyEvents.filterNot { subject ->
            hidedLessons.any { hidedLesson ->
                subject.name == hidedLesson.name &&
                        subject.educator == hidedLesson.teacher &&
                        subject.time == hidedLesson.time
            }
        }
        lessons.clear()
        viewModelScope.launch {
            lessons.addAll(filteredLessons)
        }
//        lessons.clear()
//        viewModelScope.launch {
//            lessons.addAll(dayStudyEvents.filter { !it.isCancelled })
//        }
    }

    private fun findMondayAndSunday(date: LocalDate): Pair<String, String> {
        val monday = date.with(DayOfWeek.MONDAY)
        val sunday = date.with(DayOfWeek.SUNDAY)

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formattedMonday = monday.format(formatter)
        val formattedSunday = sunday.format(formatter)

        return Pair(formattedMonday, formattedSunday)
    }

    fun getCurrentWeek(selectedDate: LocalDate): List<LocalDate> {
        val monday = selectedDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        return (0..6).map { monday.plusDays(it.toLong()) }
    }

    fun getPreviousWeek(currentWeek: List<LocalDate>): List<LocalDate> {
        return currentWeek.first().minusDays(7).let { mondayOfPreviousWeek ->
            (0..6).map { mondayOfPreviousWeek.plusDays(it.toLong()) }
        }
    }

    fun getNextWeek(currentWeek: List<LocalDate>): List<LocalDate> {
        return currentWeek.last().plusDays(1).let { mondayOfNextWeek ->
            (0..6).map { mondayOfNextWeek.plusDays(it.toLong()) }
        }
    }

    fun saveSelectedTimetable(context: Context, group: String, groupPath: String, typeUser: TypeUser) {
        val sharedPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        sharedPrefs.edit().putString("selected_group", group).apply()
        sharedPrefs.edit().putString("selected_groupPath", groupPath).apply()
        sharedPrefs.edit().putString("type_user", typeUser.toString()).apply()
    }

    fun getSelectedTimetable(context: Context): List<String?> {
        val sharedPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return listOf(
            sharedPrefs.getString("selected_group", null),
            sharedPrefs.getString("selected_groupPath", null),
            sharedPrefs.getString("type_user", null),
        )
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                        as TimetableApplication)
                val timetableRepositoryApi = application.container.timetableRepositoryApi
                TimetableViewModelApi(timetableRepositoryApi = timetableRepositoryApi)
            }
        }
    }
}