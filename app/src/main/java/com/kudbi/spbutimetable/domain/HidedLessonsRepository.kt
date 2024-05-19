package com.kudbi.spbutimetable.domain

import com.kudbi.spbutimetable.domain.model.HidedLesson

interface HidedLessonsRepository {

    fun addHidedLesson(hidedLesson: HidedLesson)

    fun removeHidedLesson(hidedLesson: HidedLesson)

    fun getHidedLessons(): Set<HidedLesson>
}