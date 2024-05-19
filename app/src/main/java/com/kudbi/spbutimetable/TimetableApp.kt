package com.kudbi.spbutimetable

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kudbi.spbutimetable.data.SharedPreferencesFavoriteGroupsRepository
import com.kudbi.spbutimetable.data.SharedPreferencesHidedLessonsRepository
import com.kudbi.spbutimetable.ui.FavoriteGroupsViewModel
import com.kudbi.spbutimetable.ui.HidedLessonsViewModel
import com.kudbi.spbutimetable.ui.screens.*
import com.kudbi.spbutimetable.ui.EducatorViewModel
import com.kudbi.spbutimetable.ui.TimetableViewModelApi
import com.kudbi.spbutimetable.util.TypeUser
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.LocalDate


@Composable
fun TimetableApp() {

    val timetableViewModelApi: TimetableViewModelApi = viewModel(factory = TimetableViewModelApi.Factory)
    val educatorViewModel: EducatorViewModel = viewModel(factory = EducatorViewModel.Factory)
    val isLoading by timetableViewModelApi.isLoading.collectAsState()
    val isLoadingEducator by educatorViewModel.isLoading.collectAsState()

    val navController = rememberNavController()
    val context = LocalContext.current
    val selectedTimetable = timetableViewModelApi.getSelectedTimetable(context)
    val favoriteGroupsViewModel = FavoriteGroupsViewModel(
        SharedPreferencesFavoriteGroupsRepository(
            context.getSharedPreferences(
                "app_prefs",
                Context.MODE_PRIVATE
            )
        )
    )
    val hidedLessonsViewModel = HidedLessonsViewModel(
        SharedPreferencesHidedLessonsRepository(
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

            if (selectedTimetable[0] == null) {
                navController.navigate(
                    "startscreen",
                    navOptions = NavOptions.Builder()
                        .setPopUpTo("start", inclusive = true)
                        .build()
                )
            } else if (selectedTimetable[2] == TypeUser.STUDENT.toString()) {
                val decodedGroupPath =
                    URLDecoder.decode(selectedTimetable[1], StandardCharsets.UTF_8.toString())
                timetableViewModelApi.getLessons(
                    decodedGroupPath,
                    LocalDate.now()
                )
                navController.navigate(
                    "lessons/${selectedTimetable[0]}/${selectedTimetable[1]}",
                    navOptions = NavOptions.Builder()
                        .setPopUpTo("start", inclusive = true)
                        .build()
                )
            } else {
                val decodedGroupPath =
                    URLDecoder.decode(selectedTimetable[1], StandardCharsets.UTF_8.toString())
                educatorViewModel.getEducatorLessons(
                    decodedGroupPath,
                    LocalDate.now()
                )
                navController.navigate(
                    "educatorlessons/${selectedTimetable[0]}/${selectedTimetable[1]}",
                    navOptions = NavOptions.Builder()
                        .setPopUpTo("start", inclusive = true)
                        .build()
                )
            }
        }

        composable("startscreen") {
            StartScreen(
                onStudentSelected = {
                    timetableViewModelApi.getFaculties()
                    navController.navigate("faculties")
                },
                onEducatorSelected = { navController.navigate("educators") }
            )
        }

        composable("faculties") {
            FacultiesScreen(
                isLoading = isLoading,
                faculties = timetableViewModelApi.faculties,
                onFacultySelected = { alias ->
                    timetableViewModelApi.getDegrees(alias)
                    navController.navigate("degree")
                },
            )
        }

        composable(
            "degree"
        ) {
            ProgramDegreesScreen(
                isLoading = isLoading,
                degrees = timetableViewModelApi.degrees,
                onProgramSelected = { degree ->
                    timetableViewModelApi.getPrograms(degree)
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
                    isLoading = isLoading,
                    programs = timetableViewModelApi.programs,
                    studyLevelName = degree,
                    onProgramSelected = { programName ->
                        timetableViewModelApi.getYears(programName)
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
                    isLoading = isLoading,
                    years = timetableViewModelApi.years,
                    programName = programName,
                    onProgramSelected = { programYear, programPath ->
                        timetableViewModelApi.getGroups(programPath)
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
                    isLoading = isLoading,
                    groups = timetableViewModelApi.groups,
                    year = programYear,
                    onGroupSelected = { group, groupPath ->
                        timetableViewModelApi.getLessons(
                            groupPath,
                            LocalDate.now()
                        )
                        val encodedUrl =
                            URLEncoder.encode(groupPath, StandardCharsets.UTF_8.toString())
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
                timetableViewModelApi.saveSelectedTimetable(context, group, URLEncoder.encode(groupPath, StandardCharsets.UTF_8.toString()), TypeUser.STUDENT)
                LessonsScreenApi(
                    isLoading = isLoading,
                    timetableViewModelApi = timetableViewModelApi,
                    educatorViewModel = educatorViewModel,
                    favoriteGroupsViewModel = favoriteGroupsViewModel,
                    hidedLessonsViewModel = hidedLessonsViewModel,
                    group = group,
                    groupPath = groupPath,
                    navController = navController,
                )
            }
        }

        composable("educators") {
            EducatorSearchScreen(
                isLoading = isLoadingEducator,
                educatorViewModel = educatorViewModel,
                onEducatorSelected = { name, id ->
                    educatorViewModel.getEducatorLessons(id, LocalDate.now())
                    val encodedUrl =
                        URLEncoder.encode(id, StandardCharsets.UTF_8.toString())
                    navController.navigate("educatorlessons/${name}/${encodedUrl}")
                }
            )
        }

        composable(
            "educatorlessons/{name}/{id}",
            arguments = listOf(
                navArgument("name") { type = NavType.StringType },
                navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name")
            val id = backStackEntry.arguments?.getString("id")
            if (name != null && id != null) {
                timetableViewModelApi.saveSelectedTimetable(context, name, URLEncoder.encode(id, StandardCharsets.UTF_8.toString()), TypeUser.EDUCATOR)
                EducatorLessonsScreenApi(
                    isLoading = isLoading,
                    timetableViewModelApi = timetableViewModelApi,
                    educatorViewModel = educatorViewModel,
                    favoriteGroupsViewModel = favoriteGroupsViewModel,
                    name = name,
                    id = id,
                    navController = navController,
                )
            }
        }
    }
}