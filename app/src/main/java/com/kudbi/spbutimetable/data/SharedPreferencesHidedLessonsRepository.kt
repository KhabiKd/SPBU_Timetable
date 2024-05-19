package com.kudbi.spbutimetable.data

import android.content.SharedPreferences
import com.google.gson.Gson
import com.kudbi.spbutimetable.domain.HidedLessonsRepository
import com.kudbi.spbutimetable.domain.model.HidedLesson

class SharedPreferencesHidedLessonsRepository(
    private val sharedPreferences: SharedPreferences
) : HidedLessonsRepository {

    companion object {
        private const val PREFERENCE_KEY = "hided_lessons"
    }

    override fun addHidedLesson(hidedLesson: HidedLesson) {
        val hidedLessons = getHidedLessons().toMutableSet()
        hidedLessons.add(hidedLesson)
        sharedPreferences.edit().putStringSet(PREFERENCE_KEY, hidedLessons.map { it.toJson() }.toSet()).apply()
    }

    override fun removeHidedLesson(hidedLesson: HidedLesson) {
        val hidedLessons = getHidedLessons().toMutableSet()
        hidedLessons.remove(hidedLesson)
        sharedPreferences.edit().putStringSet(PREFERENCE_KEY, hidedLessons.map { it.toJson() }.toSet()).apply()
    }

    override fun getHidedLessons(): Set<HidedLesson> {
        val hidedLessonsJson = sharedPreferences.getStringSet(PREFERENCE_KEY, emptySet())
        return (hidedLessonsJson?.mapNotNull { it.fromJson() } ?: emptySet()).toSet()
    }


    private fun HidedLesson.toJson(): String {
        return Gson().toJson(this)
    }

    private fun String.fromJson(): HidedLesson? {
        return try {
            Gson().fromJson(this, HidedLesson::class.java)
        } catch (e: Exception) {
            null
        }
    }
}