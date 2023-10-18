package com.kudbi.spbutimetable

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kudbi.spbutimetable.data.SharedPreferencesFavoriteGroupsRepository
import com.kudbi.spbutimetable.data.TimetableRepositoryImpl
import com.kudbi.spbutimetable.ui.FavoriteGroupsViewModel
import com.kudbi.spbutimetable.ui.TimetableViewModel
import com.kudbi.spbutimetable.ui.screens.*
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Composable
fun TimetableApp(
    viewModel: TimetableViewModel = TimetableViewModel(TimetableRepositoryImpl()),
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val selectedGroup = viewModel.getSelectedGroup(context)
    val favoriteGroupsViewModel = FavoriteGroupsViewModel(
        SharedPreferencesFavoriteGroupsRepository(
            context.getSharedPreferences(
                "app_prefs",
                Context.MODE_PRIVATE
            )
        )
    )

    NavHost(
        navController = navController,
        startDestination = "start",
    ) {
        composable("start") {

            if (selectedGroup[0] != null) {
                val decodedGroupPath =
                    URLDecoder.decode(selectedGroup[1], StandardCharsets.UTF_8.toString())
                viewModel.loadLessons(
                    decodedGroupPath,
                    LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM"))
                )
                navController.navigate(
                    "lessons/${selectedGroup[0]}/${selectedGroup[1]}",
                    navOptions = NavOptions.Builder()
                        .setPopUpTo("start", inclusive = true)
                        .build()
                )
            } else {
                navController.navigate(
                    "faculties",
                    navOptions = NavOptions.Builder()
                        .setPopUpTo("start", inclusive = true)
                        .build()
                )
            }
        }

        composable("faculties") {
            FacultiesScreen(
                faculties = viewModel.faculties,
                onFacultySelected = { facultyPath ->
                    viewModel.loadPrograms(facultyPath)
                    navController.navigate("degree")
                },
            )
        }

        composable(
            "degree"
        ) {
            ProgramDegreesScreen(
                degrees = viewModel.degrees,
                onProgramSelected = { degree ->
                    viewModel.loadProgramsName(degree)
                    navController.navigate("programs/${degree}")
                },
                navigateUp = { navController.navigateUp() }
            )
        }

        composable(
            "programs/{degree}",
            arguments = listOf(navArgument("degree") { type = NavType.StringType })
        ) { backStackEntry ->
            val degree = backStackEntry.arguments?.getString("degree")
            if (degree != null) {
                ProgramsNameScreen(
                    programsName = viewModel.programsName,
                    degree = degree,
                    onProgramSelected = { programName ->
                        viewModel.loadProgramsYear(programName)
                        navController.navigate("years/${programName}")
                    },
                    navigateUp = { navController.navigateUp() }
                )
            }
        }

        composable(
            "years/{programName}",
            arguments = listOf(navArgument("programName") { type = NavType.StringType })
        ) { backStackEntry ->
            val programName = backStackEntry.arguments?.getString("programName")
            if (programName != null) {
                ProgramsYearScreen(
                    programsYear = viewModel.programsYear,
                    programName = programName,
                    onProgramSelected = { programYear, programPath ->
                        viewModel.loadGroups(programPath)
                        navController.navigate("groups/${programYear}")
                    },
                    navigateUp = { navController.navigateUp() }
                )
            }
        }

        composable(
            "groups/{programYear}",
            arguments = listOf(navArgument("programYear") { type = NavType.StringType })
        ) { backStackEntry ->
            val programYear = backStackEntry.arguments?.getString("programYear")
            if (programYear != null) {
                GroupsScreen(
                    groups = viewModel.groups,
                    programYear = programYear,
                    onGroupSelected = { group, groupPath ->
                        viewModel.loadLessons(
                            groupPath,
                            LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM"))
                        )
                        val encodedUrl =
                            URLEncoder.encode(groupPath, StandardCharsets.UTF_8.toString())
//                        viewModel.saveSelectedGroup(context, group, encodedUrl)
                        navController.navigate("lessons/${group}/${encodedUrl}")
                    },
                    navigateUp = { navController.navigateUp() }
                )
            }
        }
        composable(
            "lessons/{group}/{groupPath}",
            arguments = listOf(
                navArgument("group") { type = NavType.StringType },
                navArgument("groupPath") { type = NavType.StringType })
        ) { backStackEntry ->
            val group = backStackEntry.arguments?.getString("group")
            val groupPath = backStackEntry.arguments?.getString("groupPath")
            if (group != null && groupPath != null) {
                viewModel.saveSelectedGroup(context, group, URLEncoder.encode(groupPath, StandardCharsets.UTF_8.toString()))
                LessonsScreen(
                    timetableViewModel = viewModel,
                    favoriteGroupsViewModel = favoriteGroupsViewModel,
                    group = group,
                    groupPath = groupPath,
                    navController = navController,
                )
            }
        }
    }
}