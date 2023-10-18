package com.kudbi.spbutimetable.domain

import com.kudbi.spbutimetable.domain.model.FavoriteGroup

interface FavoriteGroupsRepository {
    fun addFavoriteGroup(favoriteGroup: FavoriteGroup)
    fun removeFavoriteGroup(favoriteGroup: FavoriteGroup)
    fun getFavoriteGroups(): Set<FavoriteGroup>
}