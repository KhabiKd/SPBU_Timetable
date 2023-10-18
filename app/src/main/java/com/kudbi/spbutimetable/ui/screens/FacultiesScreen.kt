package com.kudbi.spbutimetable.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kudbi.spbutimetable.R
import com.kudbi.spbutimetable.domain.model.FacultyInfo
import com.kudbi.spbutimetable.ui.theme.SPBUTimetableTheme
import com.kudbi.spbutimetable.ui.theme.White
import java.util.*

enum class SearchState {
    OPENED,
    CLOSED,
}

@Composable
fun FacultiesScreenTopAppBar(
    searchState: SearchState,
    searchTextState: String,
    onTextChange: (String) -> Unit,
    onCloseClicked: () -> Unit,
    onSearchTriggered: () -> Unit,
) {
    when (searchState) {
        SearchState.CLOSED -> {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.faculties), color = White)
                },
                actions = {
                    IconButton(onClick = {
                        onSearchTriggered()
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            tint = Color.White,
                            contentDescription = stringResource(R.string.search)
                        )
                    }
                }
            )
        }
        SearchState.OPENED -> SearchAppBar(
            text = searchTextState,
            onTextChange = onTextChange,
            onCloseClicked = onCloseClicked,
        )
    }

}

@Composable
fun SearchAppBar(
    text: String,
    onTextChange: (String) -> Unit,
    onCloseClicked: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        elevation = AppBarDefaults.TopAppBarElevation,
        color = MaterialTheme.colors.onSecondary
    ) {
        TextField(
            value = text,
            onValueChange = {
                onTextChange(it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = MaterialTheme.colors.onSecondary,
                textColor = White,
                cursorColor = Color.White
            ),
            singleLine = true,
            maxLines = 1,
            leadingIcon = {
                IconButton(
                    enabled = false,
                    onClick = {}
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        tint = Color.White,
                        contentDescription = stringResource(R.string.search)
                    )
                }
            },
            trailingIcon = {
                IconButton(
                    onClick = { onCloseClicked() }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        tint = Color.White,
                        contentDescription = stringResource(R.string.close)
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = { }
            ),
        )
    }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Composable
fun FacultiesScreen(
    faculties: List<FacultyInfo>,
    onFacultySelected: (String) -> Unit,
) {
    var textState by remember { mutableStateOf("") }
    var searchState by remember { mutableStateOf(SearchState.CLOSED) }
    var filteredFaculties: List<FacultyInfo>

    Scaffold(
        topBar = {
            FacultiesScreenTopAppBar(
                searchState,
                textState,
                onTextChange = { textState = it },
                onCloseClicked = { textState = ""; searchState = SearchState.CLOSED },
                onSearchTriggered = { searchState = SearchState.OPENED }
            )
        }
    ) { padding ->
        if (faculties.isEmpty()) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Image(
                        painter = painterResource(id = R.drawable.network),
                        contentDescription = "network",
                    )
                    Text(
                        text = stringResource(id = R.string.network_problem),
                        color = MaterialTheme.colors.onSurface,
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.subtitle1,
                        modifier = Modifier.padding(horizontal = 5.dp)
                    )
                }
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding)) {
                filteredFaculties = if (textState.isEmpty() || searchState == SearchState.CLOSED) {
                    faculties
                } else {
                    val resultList = mutableListOf<FacultyInfo>()
                    for (faculty in faculties) {
                        if (faculty.facultyName.lowercase(Locale.getDefault())
                                .contains(textState.lowercase(Locale.getDefault()))
                        ) {
                            resultList.add(faculty)
                        }
                    }
                    resultList
                }
                items(filteredFaculties) { faculty ->
                    FacultyCard(faculty, onFacultySelected)
                }
            }
        }
    }
}

@Composable
fun FacultyCard(
    faculty: FacultyInfo,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(15.dp),
        elevation = 5.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.surface)
                .clickable { onClick(faculty.facultyPath) }
        ) {
            Text(
                text = faculty.facultyName,
                color = MaterialTheme.colors.onSurface,
                modifier = modifier
                    .padding(14.dp)
            )
        }
    }
}

@Preview
@Composable
fun FacultyScreenPreview() {
    SPBUTimetableTheme {
        FacultiesScreen(
            faculties = listOf(
                FacultyInfo("Процессы управления", "/AMCP"),
                FacultyInfo("Математика, Механика", "/MATH"),
                FacultyInfo("Физика", "/PHYS")
            ),
            onFacultySelected = {},
        )
    }
}