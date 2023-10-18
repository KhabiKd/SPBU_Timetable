package com.kudbi.spbutimetable.ui.screens

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.runtime.*
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.kudbi.spbutimetable.R
import com.kudbi.spbutimetable.domain.entities.ThemeManager
import com.kudbi.spbutimetable.domain.entities.Constants
import com.kudbi.spbutimetable.domain.model.restList
import com.kudbi.spbutimetable.ui.FavoriteGroupsViewModel
import com.kudbi.spbutimetable.ui.TimetableViewModel
import com.kudbi.spbutimetable.ui.theme.*
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

@Composable
fun LessonsScreenTopBar(
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
                Icon(imageVector = Icons.Filled.Menu, contentDescription = "Navigation icon", tint = Color.White)
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
        DatePicker(
            initialDate = LocalDate.now(),
            onDateSelected = { date: LocalDate ->
                onDatePickerSelected(date)
            }
        )
        showDatePicker = false
    }
}

@Composable
fun DatePicker(initialDate: LocalDate, onDateSelected: (LocalDate) -> Unit) {
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
fun LessonsScreen(
    timetableViewModel: TimetableViewModel,
    favoriteGroupsViewModel: FavoriteGroupsViewModel,
    group: String,
    groupPath: String,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val darkTheme by ThemeManager.darkTheme.collectAsState()
    val formatter = DateTimeFormatter.ofPattern("dd.MM")
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val lessons = remember { timetableViewModel.lessons }

    val isLoading by timetableViewModel.isLoading.collectAsState()

    val favoriteGroups by favoriteGroupsViewModel.favoriteGroups.collectAsState()
    val isFavorite by favoriteGroupsViewModel.isFavorite.collectAsState()
    favoriteGroupsViewModel.updateIsFavorite(group, groupPath)

    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    var datesOfWeek by remember { mutableStateOf(timetableViewModel.getCurrentWeek(selectedDate)) }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            LessonsScreenTopBar(
                group = group,
                selectedDate = selectedDate,
                isFavorite = isFavorite,
                onDatePickerSelected = { date ->
                    selectedDate = date
                    datesOfWeek = timetableViewModel.getCurrentWeek(date)
                    timetableViewModel.loadLessons(
                        groupPath = groupPath,
                        date = selectedDate.format(formatter)
                    )
                },
                onMenuClick = { coroutineScope.launch { scaffoldState.drawerState.open() } },
                onFavoriteClick = {
                    favoriteGroupsViewModel.updateIsFavorite(group, groupPath)
                    if (isFavorite) {
                        favoriteGroupsViewModel.removeFavoriteGroup(group, groupPath)
                    } else {
                        favoriteGroupsViewModel.addFavoriteGroup(group, groupPath)
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
                            navController.navigate("faculties")
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
                                    timetableViewModel.loadLessons(
                                        it.groupPath,
                                        LocalDate
                                            .now()
                                            .format(DateTimeFormatter.ofPattern("dd.MM"))
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
                    datesOfWeek = timetableViewModel.getPreviousWeek(datesOfWeek)
                    selectedDate =
                        if (datesOfWeek.contains(LocalDate.now())) LocalDate.now() else datesOfWeek[0]
                    timetableViewModel.loadLessons(
                        groupPath = groupPath,
                        date = selectedDate.format(formatter)
                    )
                }, modifier = Modifier.weight(1f)) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft,
                        tint = Orange,
                        contentDescription = "Previous week"
                    )
                }

                datesOfWeek.forEachIndexed { _, date ->
                    Box(modifier = Modifier.weight(1f)) {
                        DateItem(
                            date,
                            isSelected = date == selectedDate,
                            onClick = {
                                selectedDate = date
                                timetableViewModel.loadLessons(
                                    groupPath = groupPath,
                                    date = selectedDate.format(formatter)
                                )
                            }
                        )
                    }
                }

                IconButton(onClick = {
                    datesOfWeek = timetableViewModel.getNextWeek(datesOfWeek)
                    selectedDate =
                        if (datesOfWeek.contains(LocalDate.now())) LocalDate.now() else datesOfWeek[0]
                    timetableViewModel.loadLessons(
                        groupPath = groupPath,
                        date = selectedDate.format(formatter)
                    )
                }, modifier = Modifier.weight(1f)) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
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
                itemsIndexed(lessons) { index, lesson ->

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
                                        text = lesson.place,
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
                                        text = lesson.teacher,
                                        color = MaterialTheme.colors.onSurface,
                                        style = MaterialTheme.typography.subtitle2,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }
                            }
                        }
                    }

                }
            }
//            }

        }
    }
}

@Composable
fun DateItem(
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
                    text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                    color = MaterialTheme.colors.onSurface,
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(3.dp)
                )
            }
        }

    }
}

@Preview
@Composable
fun DateItemPreview() {
    SPBUTimetableTheme {
        DateItem(date = LocalDate.now(), isSelected = true) {}
    }
}
