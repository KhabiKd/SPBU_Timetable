package com.kudbi.spbutimetable.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.kudbi.spbutimetable.TimetableApplication
import com.kudbi.spbutimetable.data.TimetableRepositoryApi
import com.kudbi.spbutimetable.network.model.Educator
import com.kudbi.spbutimetable.network.model.LessonsDayEducator
import com.kudbi.spbutimetable.network.model.SubjectEducator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

class EducatorViewModel(private val timetableRepositoryApi: TimetableRepositoryApi): ViewModel() {
    var educators = mutableStateListOf<Educator>()
        private set

    var days = mutableStateListOf<LessonsDayEducator>()
        private set

    var lessons = mutableStateListOf<SubjectEducator>()
        private set

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun getEducatorsFlow(query: String): Flow<List<Educator>> = flow {
        _isLoading.value = true
        val educators = timetableRepositoryApi.getEducators(query).educators
        this.emit(educators)
        _isLoading.value = false
    }

    fun getEducators(query: String) {
        educators.clear()
        viewModelScope.launch {
            _isLoading.value = true
            educators.addAll(timetableRepositoryApi.getEducators(query).educators)
            _isLoading.value = false
        }
    }

    fun getEducatorLessons(id: String, date: LocalDate) {
        days.clear()
        lessons.clear()
        viewModelScope.launch {
            _isLoading.value = true

            val (formattedMonday, formattedSunday) = findMondayAndSunday(date)

            days.addAll(timetableRepositoryApi.getEducatorLessons(id, formattedMonday, formattedSunday).educatorEventsDays)

            _isLoading.value = false
        }
    }

    fun getEducatorLessonsByDay(dayStudyEvents: List<SubjectEducator>) {
        lessons.clear()
        viewModelScope.launch {
            lessons.addAll(dayStudyEvents)
        }
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

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                        as TimetableApplication)
                val timetableRepositoryApi = application.container.timetableRepositoryApi
                EducatorViewModel(timetableRepositoryApi = timetableRepositoryApi)
            }
        }
    }
}