package com.kudbi.spbutimetable.ui

import androidx.lifecycle.ViewModel
import com.kudbi.spbutimetable.domain.HidedLessonsRepository
import com.kudbi.spbutimetable.domain.model.HidedLesson
import com.kudbi.spbutimetable.util.TypeUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HidedLessonsViewModel(private val hidedRepository: HidedLessonsRepository) : ViewModel() {

    private val _hidedLessons = MutableStateFlow<Set<HidedLesson>>(emptySet())
    val hidedLesson: StateFlow<Set<HidedLesson>> = _hidedLessons.asStateFlow()

    init {
        getHidedLessons()
    }

    fun addHidedLesson(name: String, teacher: String, time: String) {
        hidedRepository.addHidedLesson(HidedLesson(name, teacher, time))
        val updatedList = _hidedLessons.value.toMutableSet().apply { add(HidedLesson(name, teacher, time)) }
        _hidedLessons.value = updatedList
    }

    fun removeHidedLesson(hidedLesson: HidedLesson) {
        hidedRepository.removeHidedLesson(hidedLesson)
        val updatedList = _hidedLessons.value.toMutableSet().apply { remove(hidedLesson) }
        _hidedLessons.value = updatedList
    }

    fun getHidedLessons() {
        val lessons = hidedRepository.getHidedLessons()
        _hidedLessons.value = lessons
    }
}