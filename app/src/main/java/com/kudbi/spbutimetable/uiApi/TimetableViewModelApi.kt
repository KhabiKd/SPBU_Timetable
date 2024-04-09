package com.kudbi.spbutimetable.uiApi

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.spbutimetableonapi.network.model.Faculty
import com.example.spbutimetableonapi.network.model.Group
import com.example.spbutimetableonapi.network.model.StudyLevel
import com.example.spbutimetableonapi.network.model.StudyProgram
import com.example.spbutimetableonapi.network.model.StudyProgramCombination
import com.kudbi.spbutimetable.TimetableApplication
import com.kudbi.spbutimetable.dataApi.TimetableRepositoryApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        getFaculties()
    }

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