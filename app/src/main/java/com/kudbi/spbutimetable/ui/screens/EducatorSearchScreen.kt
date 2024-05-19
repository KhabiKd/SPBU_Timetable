package com.kudbi.spbutimetable.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AppBarDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kudbi.spbutimetable.R
import com.kudbi.spbutimetable.network.model.Educator
import com.kudbi.spbutimetable.ui.theme.White
import com.kudbi.spbutimetable.ui.EducatorViewModel

@Composable
fun EducatorSearchScreenTopAppBar(
    searchState: SearchState,
    searchTextState: String,
    onTextChange: (String) -> Unit,
    onCloseClicked: () -> Unit,
    onSearchClicked: () -> Unit,
    onSearchTriggered: () -> Unit,
) {
    when (searchState) {
        SearchState.CLOSED -> {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.educators), color = White)
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
        SearchState.OPENED -> SearchAppBarEducator(
            text = searchTextState,
            onTextChange = onTextChange,
            onSearchClicked = onSearchClicked,
            onCloseClicked = onCloseClicked,
        )
    }

}

@Composable
fun SearchAppBarEducator(
    text: String,
    onTextChange: (String) -> Unit,
    onSearchClicked: () -> Unit,
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
                onSearch = { onSearchClicked() }
            ),
        )
    }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Composable
fun EducatorSearchScreen(
    isLoading: Boolean,
    educatorViewModel: EducatorViewModel,
    onEducatorSelected: (String, String) -> Unit,
) {
    val educators = remember { educatorViewModel.educators }
    var textState by remember { mutableStateOf("") }
    var searchState by remember { mutableStateOf(SearchState.CLOSED) }

    Scaffold(
        topBar = {
            EducatorSearchScreenTopAppBar(
                searchState,
                textState,
                onTextChange = { textState = it },
                onSearchClicked = { educatorViewModel.getEducators(textState) },
                onCloseClicked = { textState = ""; searchState = SearchState.CLOSED },
                onSearchTriggered = { searchState = SearchState.OPENED }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(color = MaterialTheme.colors.primary)
            }
        } else if (educators.isEmpty()) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {

                    Text(
                        text = stringResource(id = R.string.search_educator),
                        color = MaterialTheme.colors.onSurface,
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.subtitle1,
                        modifier = Modifier.padding(horizontal = 5.dp)
                    )

            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding)) {
                items(educators) { educator ->
                    EducatorCard(educator, onEducatorSelected)
                }
            }
        }
    }
}

@Composable
fun EducatorCard(
    educator: Educator,
    onClick: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(15.dp),
        elevation = 5.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.surface)
                .clickable { onClick(educator.shortName, educator.id.toString()) }
        ) {
            Text(
                text = educator.fullName,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.onSurface,
                modifier = modifier
                    .padding(top = 14.dp, end = 14.dp, start = 14.dp, bottom = 8.dp)
            )
            educator.employments.forEachIndexed { id, employment ->
                Text(
                    text = employment.department,
                    fontSize = 14.sp,
                    color = MaterialTheme.colors.onSurface,
                    modifier = modifier
                        .padding(top = if (id != 0) 8.dp else 0.dp, bottom = if (id == educator.employments.size-1) 8.dp else 0.dp, start = 14.dp, end = 14.dp)
                )
            }
        }
    }
}