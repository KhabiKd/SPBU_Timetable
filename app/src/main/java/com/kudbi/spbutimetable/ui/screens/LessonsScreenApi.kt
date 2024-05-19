package com.kudbi.spbutimetable.ui.screens

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.kudbi.spbutimetable.R
import com.kudbi.spbutimetable.domain.entities.Constants
import com.kudbi.spbutimetable.domain.entities.ThemeManager
import com.kudbi.spbutimetable.domain.model.restList
import com.kudbi.spbutimetable.ui.FavoriteGroupsViewModel
import com.kudbi.spbutimetable.ui.HidedLessonsViewModel
import com.kudbi.spbutimetable.ui.theme.Bronze
import com.kudbi.spbutimetable.ui.theme.Orange
import com.kudbi.spbutimetable.ui.theme.White
import com.kudbi.spbutimetable.ui.EducatorViewModel
import com.kudbi.spbutimetable.ui.TimetableViewModelApi
import com.kudbi.spbutimetable.util.TypeUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun LessonsScreenTopBarApi(
    group: String,
    selectedDate: LocalDate,
    isFavorite: Boolean,
    onDatePickerSelected: (LocalDate) -> Unit,
    onMenuClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    darkTheme: Boolean,
    onThemeUpdated: () -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val month = Constants.MONTHS
    TopAppBar(
        title = {
            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(text = group, color = White)
                Text(
                    text = "${month[selectedDate.month.value - 1]} ${
                        selectedDate.format(
                            DateTimeFormatter.ofPattern("yyyy", Locale.getDefault())
                        )
                    }",
                    color = White,
                    fontSize = 12.sp
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Navigation icon",
                    tint = Color.White
                )
            }
        },
        actions = {
            IconButton(onClick = onFavoriteClick) {
                Icon(
                    imageVector = if (!isFavorite) Icons.Outlined.FavoriteBorder else Icons.Outlined.Favorite,
                    tint = Color.White,
                    contentDescription = "Select date"
                )
            }
            IconButton(onClick = { showDatePicker = true }) {
                Icon(
                    imageVector = Icons.Filled.CalendarMonth,
                    tint = Color.White,
                    contentDescription = "Select date"
                )
            }
            ThemeSwitcher(
                darkTheme = darkTheme,
                onClick = onThemeUpdated
            )
        }
    )
    if (showDatePicker) {
        DatePickerApi(
            initialDate = LocalDate.now(),
            onDateSelected = { date: LocalDate ->
                onDatePickerSelected(date)
            }
        )
        showDatePicker = false
    }
}

@Composable
fun DatePickerApi(initialDate: LocalDate, onDateSelected: (LocalDate) -> Unit) {
    val mContext = LocalContext.current
    var selectedDate by remember { mutableStateOf(initialDate) }
    val year = selectedDate.year
    val month = selectedDate.monthValue - 1
    val day = selectedDate.dayOfMonth
    DatePickerDialog(
        mContext,
        R.style.DatePickerTheme,
        { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
            selectedDate = LocalDate.of(mYear, mMonth + 1, mDayOfMonth)
            onDateSelected(selectedDate)
        },
        year,
        month,
        day
    ).show()
}

@Composable
fun LessonsScreenApi(
    isLoading: Boolean,
    timetableViewModelApi: TimetableViewModelApi,
    educatorViewModel: EducatorViewModel,
    favoriteGroupsViewModel: FavoriteGroupsViewModel,
    hidedLessonsViewModel: HidedLessonsViewModel,
    group: String,
    groupPath: String,
    navController: NavController,
    modifier: Modifier = Modifier
) {

    val days = remember { timetableViewModelApi.days }
    val lessons = remember { timetableViewModelApi.lessons }

    val context = LocalContext.current
    val darkTheme by ThemeManager.darkTheme.collectAsState()
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    val favoriteGroups by favoriteGroupsViewModel.favoriteGroups.collectAsState()
    val isFavorite by favoriteGroupsViewModel.isFavorite.collectAsState()
    favoriteGroupsViewModel.updateIsFavorite(group, groupPath, TypeUser.STUDENT)

    val hidedLessons by hidedLessonsViewModel.hidedLesson.collectAsState()

    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    var datesOfWeek by remember { mutableStateOf(timetableViewModelApi.getCurrentWeek(selectedDate)) }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            LessonsScreenTopBarApi(
                group = group,
                selectedDate = selectedDate,
                isFavorite = isFavorite,
                onDatePickerSelected = { date ->
                    selectedDate = date
                    datesOfWeek = timetableViewModelApi.getCurrentWeek(date)
                    coroutineScope.launch {
                        timetableViewModelApi.getLessons(
                            groupId = groupPath,
                            date = selectedDate
                        )
                        while (timetableViewModelApi.days.isEmpty()) {
                            delay(10)
                        }
                        if (days.any {
                                it.day.substring(0, 10) == selectedDate.format(
                                    DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                )
                            }) {
                            timetableViewModelApi.getLessonsByDay(
                                days.find {
                                    it.day.substring(0, 10) ==
                                            selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                }!!.dayStudyEvents,
                                hidedLessons
                            )
                        } else {
                            lessons.clear()
                        }
                    }
                },
                onMenuClick = { coroutineScope.launch { scaffoldState.drawerState.open() } },
                onFavoriteClick = {
                    favoriteGroupsViewModel.updateIsFavorite(group, groupPath, TypeUser.STUDENT)
                    if (isFavorite) {
                        favoriteGroupsViewModel.removeFavoriteGroup(
                            group,
                            groupPath,
                            TypeUser.STUDENT
                        )
                    } else {
                        favoriteGroupsViewModel.addFavoriteGroup(group, groupPath, TypeUser.STUDENT)
                    }
                },
                darkTheme = darkTheme,
                onThemeUpdated = {
                    val newTheme = !darkTheme
                    ThemeManager.setDarkTheme(newTheme, context)
                },
            )
        },
        drawerContent = {
            Column {
                Text(
                    text = stringResource(id = R.string.to_faculties),
                    color = MaterialTheme.colors.onSurface,
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            coroutineScope.launch {
                                scaffoldState.drawerState.close()
                            }
                            navController.navigate("startscreen")
                        }
                        .padding(16.dp)
                )
                Divider(
                    color = Color.LightGray,
                    thickness = 1.dp,
                    modifier = Modifier.padding(start = 16.dp)
                )
                Text(
                    text = "Избранные:",
                    color = MaterialTheme.colors.onSurface,
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
                if (favoriteGroups.isNotEmpty()) {
                    favoriteGroups.forEachIndexed { index, it ->
                        Text(
                            text = it.group,
                            fontSize = 16.sp,
                            color = MaterialTheme.colors.primary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    coroutineScope.launch {
                                        scaffoldState.drawerState.close()
                                    }
                                    val encodedUrl =
                                        URLEncoder.encode(
                                            it.groupPath,
                                            StandardCharsets.UTF_8.toString()
                                        )
                                    if (it.typeUser == TypeUser.STUDENT) {
                                        timetableViewModelApi.getLessons(
                                            it.groupPath,
                                            LocalDate.now()
                                        )
                                        navController.navigate(
                                            "lessons/${it.group}/${encodedUrl}",
                                            navOptions = NavOptions
                                                .Builder()
                                                .setPopUpTo(
                                                    "lessons/{group}/{groupPath}",
                                                    inclusive = true
                                                )
                                                .build()
                                        )
                                    } else if (it.typeUser == TypeUser.EDUCATOR) {
                                        educatorViewModel.getEducatorLessons(
                                            it.groupPath,
                                            LocalDate.now()
                                        )
                                        navController.navigate(
                                            "educatorlessons/${it.group}/${encodedUrl}",
                                            navOptions = NavOptions
                                                .Builder()
                                                .setPopUpTo(
                                                    "educatorlessons/{name}/{id}",
                                                    inclusive = true
                                                )
                                                .build()
                                        )
                                    }
                                }
                                .padding(
                                    start = 32.dp,
                                    top = if (index == 0) 0.dp else 8.dp,
                                    bottom = if (index == favoriteGroups.size - 1) 16.dp else 8.dp
                                )
                        )
                    }
                } else {
                    Text(
                        text = stringResource(id = R.string.favorites),
                        fontSize = 16.sp,
                        color = MaterialTheme.colors.primary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = 16.dp,
                                bottom = 16.dp
                            )
                    )
                }

                Divider(
                    color = Color.LightGray,
                    thickness = 1.dp,
                    modifier = Modifier.padding(start = 16.dp)
                )

                Text(
                    text = "Скрытые:",
                    color = MaterialTheme.colors.onSurface,
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
                if (hidedLessons.isNotEmpty()) {
                    hidedLessons.forEachIndexed { index, it ->
                        Text(
                            text = it.name,
                            fontSize = 16.sp,
                            color = MaterialTheme.colors.primary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    hidedLessonsViewModel.removeHidedLesson(it)
                                    coroutineScope.launch {
                                        timetableViewModelApi.getLessons(
                                            groupId = groupPath,
                                            date = selectedDate
                                        )
                                        while (timetableViewModelApi.days.isEmpty()) {
                                            delay(10)
                                        }
                                        if (days.any {
                                                it.day.substring(0, 10) == selectedDate.format(
                                                    DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                                )
                                            }) {
                                            timetableViewModelApi.getLessonsByDay(
                                                days.find {
                                                    it.day.substring(0, 10) ==
                                                            selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                                }!!.dayStudyEvents,
                                                hidedLessons
                                            )
                                        } else {
                                            lessons.clear()
                                        }
                                    }
                                }
                                .padding(
                                    start = 32.dp,
                                    top = if (index == 0) 0.dp else 8.dp,
                                    bottom = if (index == favoriteGroups.size - 1) 16.dp else 8.dp
                                )
                        )
                    }
                }
            }
        }

    ) { padding ->
        Column(
            modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(onClick = {
                    datesOfWeek = timetableViewModelApi.getPreviousWeek(datesOfWeek)
                    selectedDate =
                        if (datesOfWeek.contains(LocalDate.now())) LocalDate.now() else datesOfWeek[0]
                    coroutineScope.launch {
                        timetableViewModelApi.getLessons(
                            groupId = groupPath,
                            date = selectedDate
                        )
                        while (timetableViewModelApi.days.isEmpty()) {
                            delay(10)
                        }
                        if (days.any {
                                it.day.substring(0, 10) == selectedDate.format(
                                    DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                )
                            }) {
                            timetableViewModelApi.getLessonsByDay(
                                days.find {
                                    it.day.substring(0, 10) ==
                                            selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                }!!.dayStudyEvents,
                                hidedLessons
                            )
                        } else {
                            lessons.clear()
                        }
                    }
                }, modifier = Modifier.weight(1f)) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        tint = Orange,
                        contentDescription = "Previous week"
                    )
                }

                datesOfWeek.forEachIndexed { _, date ->
                    Box(modifier = Modifier.weight(1f)) {
                        DateItemApi(
                            date,
                            isSelected = date == selectedDate,
                            onClick = {
                                selectedDate = date
                                if (days.any {
                                        it.day.substring(0, 10) == selectedDate.format(
                                            DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        )
                                    }) {
                                    timetableViewModelApi.getLessonsByDay(
                                        days.find {
                                            it.day.substring(0, 10) ==
                                                    selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                        }!!.dayStudyEvents,
                                        hidedLessons
                                    )
                                } else {
                                    lessons.clear()
                                }
                            }
                        )
                    }
                }

                IconButton(onClick = {
                    datesOfWeek = timetableViewModelApi.getNextWeek(datesOfWeek)
                    selectedDate =
                        if (datesOfWeek.contains(LocalDate.now())) LocalDate.now() else datesOfWeek[0]
                    coroutineScope.launch {
                        timetableViewModelApi.getLessons(
                            groupId = groupPath,
                            date = selectedDate
                        )
                        while (timetableViewModelApi.days.isEmpty()) {
                            delay(10)
                        }
                        if (days.any {
                                it.day.substring(0, 10) == selectedDate.format(
                                    DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                )
                            }) {
                            timetableViewModelApi.getLessonsByDay(
                                days.find {
                                    it.day.substring(0, 10) ==
                                            selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                }!!.dayStudyEvents,
                                hidedLessons
                            )
                        } else {
                            lessons.clear()
                        }
                    }
                }, modifier = Modifier.weight(1f)) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        tint = Orange,
                        contentDescription = "Next week"
                    )
                }

            }

            if (isLoading) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(color = MaterialTheme.colors.primary)
                }
            }

            if (lessons.isEmpty()) {
                val restInfo = restList.random()
                Box(contentAlignment = Alignment.TopCenter, modifier = Modifier.fillMaxSize()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(top = 150.dp)
                    ) {
                        Image(
                            painter = painterResource(id = restInfo.imageResourceId),
                            contentDescription = "rest",
                        )
                        Text(
                            text = stringResource(id = restInfo.description),
                            color = MaterialTheme.colors.onSurface,
                            textAlign = TextAlign.Center,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.subtitle1,
                            modifier = Modifier.padding(horizontal = 5.dp)
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(lessons.filter { !it.isCancelled }) { index, lesson ->

                    if (index > 0 && index < lessons.size) Divider(
                        color = Color.LightGray,
                        thickness = 1.dp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Card(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                            .heightIn(min = 64.dp)
                    ) {
                        Box(modifier = Modifier.background(MaterialTheme.colors.secondary)) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Image(
                                        painter =
                                        if (darkTheme) painterResource(id = R.drawable.clock_o)
                                        else painterResource(id = R.drawable.clock),
                                        contentDescription = "time",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .height(16.dp)
                                            .width(16.dp)
                                            .aspectRatio(1f)
                                    )
                                    Text(
                                        text = lesson.time,
                                        color = MaterialTheme.colors.onSurface,
                                        style = MaterialTheme.typography.subtitle2,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                    Spacer(modifier = modifier.weight(1f))
                                    Image(
                                        painter =
                                            if (darkTheme) painterResource(id = R.drawable.dots_o)
                                            else painterResource(id = R.drawable.dots),
                                        contentDescription = "hide",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .height(16.dp)
                                            .width(16.dp)
                                            .aspectRatio(1f)
                                            .clickable {
                                                hidedLessonsViewModel.addHidedLesson(lesson.name, lesson.educator, lesson.time)
                                                coroutineScope.launch {
                                                    timetableViewModelApi.getLessons(
                                                        groupId = groupPath,
                                                        date = selectedDate
                                                    )
                                                    while (timetableViewModelApi.days.isEmpty()) {
                                                        delay(10)
                                                    }
                                                    if (days.any {
                                                            it.day.substring(0, 10) == selectedDate.format(
                                                                DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                                            )
                                                        }) {
                                                        timetableViewModelApi.getLessonsByDay(
                                                            days.find {
                                                                it.day.substring(0, 10) ==
                                                                        selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                                            }!!.dayStudyEvents,
                                                            hidedLessons
                                                        )
                                                    } else {
                                                        lessons.clear()
                                                    }
                                                }
                                            }
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Image(
                                        painter =
                                        if (darkTheme) painterResource(id = R.drawable.graduation_hat_o)
                                        else painterResource(id = R.drawable.graduation_hat),
                                        contentDescription = "lesson_name",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .height(16.dp)
                                            .width(16.dp)
                                            .aspectRatio(1f)
                                    )
                                    Text(
                                        text = lesson.name,
                                        color = MaterialTheme.colors.onSurface,
                                        style = MaterialTheme.typography.subtitle2,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Image(
                                        painter =
                                        if (darkTheme) painterResource(id = R.drawable.maps_and_flags_o)
                                        else painterResource(id = R.drawable.maps_and_flags),
                                        contentDescription = "lesson_place",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .height(16.dp)
                                            .width(16.dp)
                                            .aspectRatio(1f)
                                    )
                                    Text(
                                        text = lesson.location,
                                        color = MaterialTheme.colors.onSurface,
                                        style = MaterialTheme.typography.subtitle2,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Image(
                                        painter =
                                        if (darkTheme) painterResource(id = R.drawable.man_user_o)
                                        else painterResource(id = R.drawable.man_user),
                                        contentDescription = "lesson_teacher",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .height(16.dp)
                                            .width(16.dp)
                                            .aspectRatio(1f)
                                    )

                                    Text(
                                        text = lesson.educator,
                                        color = MaterialTheme.colors.onSurface,
                                        textDecoration = TextDecoration.Underline,
                                        style = MaterialTheme.typography.subtitle2,
                                        modifier = Modifier
                                            .padding(start = 8.dp)
                                            .clickable {
                                                coroutineScope.launch {
                                                    val query = lesson.educator
                                                        .substringBefore(',')
                                                        .substringBeforeLast('.')
                                                    educatorViewModel
                                                        .getEducatorsFlow(query)
                                                        .collect { educators ->
                                                            if (educators.isNotEmpty()) {
                                                                val educator = educators.first()
                                                                educatorViewModel.getEducatorLessons(
                                                                    educator.id.toString(),
                                                                    LocalDate.now()
                                                                )
                                                                val encodedUrl =
                                                                    withContext(
                                                                        Dispatchers.IO
                                                                    ) {
                                                                        URLEncoder.encode(
                                                                            educator.id.toString(),
                                                                            StandardCharsets.UTF_8.toString()
                                                                        )
                                                                    }
                                                                navController.navigate(
                                                                    "educatorlessons/${
                                                                        lesson.educator.substringBefore(
                                                                            ','
                                                                        )
                                                                    }/${encodedUrl}",
                                                                    navOptions = NavOptions
                                                                        .Builder()
                                                                        .setPopUpTo(
                                                                            "educatorlessons/{name}/{id}",
                                                                            inclusive = true
                                                                        )
                                                                        .build()
                                                                )
                                                            }
                                                        }
                                                }
                                            }
                                    )
                                }
                            }
                        }
                    }

                }
            }
        }
    }
}

@Composable
fun DateItemApi(
    date: LocalDate,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val background =
        if (isSelected) Bronze else MaterialTheme.colors.onBackground
    val background2 =
        if (isSelected) Orange else MaterialTheme.colors.secondary

    Card(
        shape = RoundedCornerShape(10.dp),
        backgroundColor = background,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(35.dp)
                        .clip(shape = RoundedCornerShape(10.dp))
                        .background(background2)
                ) {
                    Text(
                        text = date.dayOfMonth.toString(),
                        color = MaterialTheme.colors.onSurface,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                    )
                }
                Text(
                    text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("ru")),
                    color = MaterialTheme.colors.onSurface,
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(3.dp)
                )
            }
        }
    }
}