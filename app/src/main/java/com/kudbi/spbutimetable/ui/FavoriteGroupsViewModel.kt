package com.kudbi.spbutimetable.ui

import androidx.lifecycle.ViewModel
import com.kudbi.spbutimetable.domain.FavoriteGroupsRepository
import com.kudbi.spbutimetable.domain.model.FavoriteGroup
import com.kudbi.spbutimetable.util.TypeUser
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

    fun addFavoriteGroup(group: String, groupPath: String, typeUser: TypeUser) {
        favoritesRepository.addFavoriteGroup(FavoriteGroup(group, groupPath, typeUser))
        val updatedList = _favoriteGroups.value.toMutableSet().apply { add(FavoriteGroup(group, groupPath, typeUser)) }
        _favoriteGroups.value = updatedList
        _isFavorite.value = true
    }

    fun removeFavoriteGroup(group: String, groupPath: String, typeUser: TypeUser) {
        favoritesRepository.removeFavoriteGroup(FavoriteGroup(group, groupPath, typeUser))
        val updatedList = _favoriteGroups.value.toMutableSet().apply { remove(FavoriteGroup(group, groupPath, typeUser)) }
        _favoriteGroups.value = updatedList
        _isFavorite.value = false
    }

    private fun isFavorite(group: String, groupPath: String, typeUser: TypeUser): Boolean {
        val favoriteGroups = favoriteGroups.value
        val isFavorite = favoriteGroups.contains(FavoriteGroup(group, groupPath, typeUser))
        _isFavorite.value = isFavorite
        return isFavorite
    }

    fun updateIsFavorite(group: String, groupPath: String, typeUser: TypeUser) {
        val isFavorite = isFavorite(group, groupPath, typeUser)
        _isFavorite.value = isFavorite
    }

    fun getFavoriteGroups() {
        val groups = favoritesRepository.getFavoriteGroups()
        _favoriteGroups.value = groups
    }

}