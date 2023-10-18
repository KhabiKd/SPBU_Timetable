package com.kudbi.spbutimetable.ui

import androidx.lifecycle.ViewModel
import com.kudbi.spbutimetable.domain.FavoriteGroupsRepository
import com.kudbi.spbutimetable.domain.model.FavoriteGroup
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FavoriteGroupsViewModel(private val favoritesRepository: FavoriteGroupsRepository) : ViewModel() {

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    private val _favoriteGroups = MutableStateFlow<Set<FavoriteGroup>>(emptySet())
    val favoriteGroups: StateFlow<Set<FavoriteGroup>> = _favoriteGroups.asStateFlow()

    init {
        getFavoriteGroups()
    }

    fun addFavoriteGroup(group: String, groupPath: String) {
        favoritesRepository.addFavoriteGroup(FavoriteGroup(group, groupPath))
        val updatedList = _favoriteGroups.value.toMutableSet().apply { add(FavoriteGroup(group, groupPath)) }
        _favoriteGroups.value = updatedList
        _isFavorite.value = true
    }

    fun removeFavoriteGroup(group: String, groupPath: String) {
        favoritesRepository.removeFavoriteGroup(FavoriteGroup(group, groupPath))
        val updatedList = _favoriteGroups.value.toMutableSet().apply { remove(FavoriteGroup(group, groupPath)) }
        _favoriteGroups.value = updatedList
        _isFavorite.value = false
    }

    private fun isFavorite(group: String, groupPath: String): Boolean {
        val favoriteGroups = favoriteGroups.value
        val isFavorite = favoriteGroups.contains(FavoriteGroup(group, groupPath))
        _isFavorite.value = isFavorite
        return isFavorite
    }

    fun updateIsFavorite(group: String, groupPath: String) {
        val isFavorite = isFavorite(group, groupPath)
        _isFavorite.value = isFavorite
    }

    fun getFavoriteGroups() {
        val groups = favoritesRepository.getFavoriteGroups()
        _favoriteGroups.value = groups
    }

}