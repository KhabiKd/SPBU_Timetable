package com.kudbi.spbutimetable.data

import android.content.SharedPreferences
import com.kudbi.spbutimetable.domain.FavoriteGroupsRepository
import com.kudbi.spbutimetable.domain.model.FavoriteGroup
import com.google.gson.Gson

class SharedPreferencesFavoriteGroupsRepository(
    private val sharedPreferences: SharedPreferences
) : FavoriteGroupsRepository {

    companion object {
        private const val PREFERENCE_KEY = "favorite_groups"
    }

    override fun addFavoriteGroup(favoriteGroup: FavoriteGroup) {
        val favoriteGroups = getFavoriteGroups().toMutableSet()
        favoriteGroups.add(favoriteGroup)
        sharedPreferences.edit().putStringSet(PREFERENCE_KEY, favoriteGroups.map { it.toJson() }.toSet()).apply()
    }

    override fun removeFavoriteGroup(favoriteGroup: FavoriteGroup) {
        val favoriteGroups = getFavoriteGroups().toMutableSet()
        favoriteGroups.remove(favoriteGroup)
        sharedPreferences.edit().putStringSet(PREFERENCE_KEY, favoriteGroups.map { it.toJson() }.toSet()).apply()
    }

    override fun getFavoriteGroups(): Set<FavoriteGroup> {
        val favoriteGroupsJson = sharedPreferences.getStringSet(PREFERENCE_KEY, emptySet())
        return (favoriteGroupsJson?.mapNotNull { it.fromJson() } ?: emptySet()).toSet()
    }

    private fun FavoriteGroup.toJson(): String {
        return Gson().toJson(this)
    }

    private fun String.fromJson(): FavoriteGroup? {
        return try {
            Gson().fromJson(this, FavoriteGroup::class.java)
        } catch (e: Exception) {
            null
        }
    }
}
