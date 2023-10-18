package com.kudbi.spbutimetable.ui

import android.content.Context
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kudbi.spbutimetable.domain.TimetableRepository
import com.kudbi.spbutimetable.domain.model.FacultyInfo
import com.kudbi.spbutimetable.domain.model.GroupInfo
import com.kudbi.spbutimetable.domain.model.LessonInfo
import com.kudbi.spbutimetable.domain.model.ProgramInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

class TimetableViewModel(private val repository: TimetableRepository) : ViewModel() {

    var faculties = mutableStateListOf<FacultyInfo>()
        private set
    var programs = mutableStateListOf<ProgramInfo>()
        private set
    var groups = mutableStateListOf<GroupInfo>()
        private set
    var lessons = mutableStateListOf<LessonInfo>()
        private set
    var degrees = mutableStateMapOf<String, List<ProgramInfo>>()
        private set
    var programsName = mutableStateMapOf<String, List<ProgramInfo>>()
        private set
    var programsYear = mutableStateMapOf<String, List<ProgramInfo>>()
        private set

    private var selectedFacultyPath by mutableStateOf("")
    private var selectedProgramPath by mutableStateOf("")
    private var selectedGroupPath by mutableStateOf("")

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadFaculties()
    }

    private fun loadFaculties() {
        faculties.clear()
        viewModelScope.launch {
            faculties.addAll(repository.getFaculties())
        }
    }

    fun loadProgramsName(degree: String) {
        programsName.clear()
        viewModelScope.launch {
            programsName.putAll(degrees[degree]?.groupBy { it.programName } ?: mapOf())
        }
    }

    fun loadProgramsYear(programName: String) {
        programsYear.clear()
        viewModelScope.launch {
            programsYear.putAll(programsName[programName]?.groupBy { it.programYear } ?: mapOf())
        }
    }

    fun loadPrograms(facultyPath: String) {
        programs.clear()
        degrees.clear()
        selectedFacultyPath = facultyPath
        viewModelScope.launch {
            programs.addAll(repository.getPrograms(facultyPath))
            degrees.putAll(programs.groupBy { it.degree })
        }
    }

    fun loadGroups(programPath: String) {
        groups.clear()
        selectedProgramPath = programPath
        viewModelScope.launch {
            groups.addAll(repository.getGroups(programPath))
        }
    }

    fun loadLessons(groupPath: String, date: String) {
        lessons.clear()
        selectedGroupPath = groupPath
        viewModelScope.launch {
            _isLoading.value = true
            lessons.addAll(repository.getLessons(groupPath, date))
            _isLoading.value = false
        }
    }

    fun saveSelectedGroup(context: Context, group: String, groupPath: String) {
        val sharedPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        sharedPrefs.edit().putString("selected_group", group).apply()
        sharedPrefs.edit().putString("selected_groupPath", groupPath).apply()
    }

    fun getSelectedGroup(context: Context): List<String?> {
        val sharedPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return listOf(
            sharedPrefs.getString("selected_group", null),
            sharedPrefs.getString("selected_groupPath", null)
        )
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
}