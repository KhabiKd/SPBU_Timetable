package com.kudbi.spbutimetable.domain.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.kudbi.spbutimetable.R

data class RestInfo(
    @DrawableRes val imageResourceId: Int,
    @StringRes val description: Int
)

val restList = listOf(
    RestInfo(R.drawable.bbq, R.string.restInfo_bbq),
    RestInfo(R.drawable.family, R.string.restInfo_family),
    RestInfo(R.drawable.love, R.string.restInfo_love),
    RestInfo(R.drawable.puppy, R.string.restInfo_puppy),
    RestInfo(R.drawable.rest, R.string.restInfo_rest),
    RestInfo(R.drawable.train, R.string.restInfo_train)
)