package com.kudbi.spbutimetable.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kudbi.spbutimetable.network.model.StudyLevel
import com.kudbi.spbutimetable.R
import com.kudbi.spbutimetable.ui.theme.White

@Composable
fun ProgramDegreesScreenTopAppBar(navigateUp: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.programs),
                color = White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        navigationIcon = {
            IconButton(onClick = navigateUp) {
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Go Back", tint = White)
            }
        }
    )
}

@Composable
fun ProgramDegreesScreen(
    isLoading: Boolean,
    degrees: List<StudyLevel>,
    onProgramSelected: (String) -> Unit,
    navigateUp: () -> Unit,
) {
    Scaffold(
        topBar = {
            ProgramDegreesScreenTopAppBar {
                navigateUp()
            }
        }
    ) { padding ->
        if (isLoading) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(color = MaterialTheme.colors.primary)
            }
        }
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(degrees) { degree ->
                ProgramDegreeCard(degree.studyLevelName, onProgramSelected)
            }
        }
    }
}

@Composable
fun ProgramDegreeCard(
    degree: String,
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
                .clickable { onClick(degree) },
        ) {
            Text(
                text = degree,
                color = MaterialTheme.colors.onSurface,
                modifier = modifier
                    .padding(14.dp)
            )
        }
    }
}
