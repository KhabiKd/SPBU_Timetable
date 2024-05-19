package com.kudbi.spbutimetable.domain.model

import com.kudbi.spbutimetable.util.TypeUser

data class FavoriteGroup(
    val group: String,
    val groupPath: String,
    val typeUser: TypeUser
)