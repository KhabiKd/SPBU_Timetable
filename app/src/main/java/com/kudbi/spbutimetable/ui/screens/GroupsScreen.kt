package com.kudbi.spbutimetable.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kudbi.spbutimetable.network.model.Group
import com.kudbi.spbutimetable.R
import com.kudbi.spbutimetable.ui.theme.White

@Composable
fun GroupScreenTopAppBar(programYear: String, navigateUp: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = programYear,
                color = White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(end = 16.dp)
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
fun GroupsScreen(
    isLoading: Boolean,
    groups: List<Group>,
    year: String,
    onGroupSelected: (String, String) -> Unit,
    navigateUp: () -> Unit,
) {
    Scaffold(
        topBar = {
            GroupScreenTopAppBar(programYear = year) {
                navigateUp()
            }
        }
    ) { padding ->
        if (isLoading) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(color = MaterialTheme.colors.primary)
            }
        }
        if ( groups.isNotEmpty() && groups[0].studentGroupName == "0") {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Image(
                        painter = painterResource(id = R.drawable.network),
                        contentDescription = "rest",
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
                items(groups) { group ->
                    GroupCard(group.studentGroupName, group.studentGroupId , onGroupSelected)
                }
            }
        }
    }
}

@Composable
fun GroupCard(
    groupName: String,
    groupId: Int,
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.surface)
                .clickable { onClick(groupName, groupId.toString()) },
        ) {
            Text(
                text = groupName,
                color = MaterialTheme.colors.onSurface,
                modifier = modifier
                    .padding(14.dp)
            )
        }
    }
}