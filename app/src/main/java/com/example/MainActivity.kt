package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.screens.*
import com.example.viewmodel.MathViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppContent()
            }
        }
    }
}

sealed class NavigationCategory(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Frontier : NavigationCategory("dashboard", "Frontier", Icons.Default.Home)
    object Lectures : NavigationCategory("courses", "Lectures", Icons.Default.Favorite)
    object Exams : NavigationCategory("test_series", "Exams", Icons.Default.Check)
    object Solver : NavigationCategory("doubt_solver", "Solver", Icons.Default.Search)
    object Insights : NavigationCategory("math_lab", "Insights", Icons.Default.ThumbUp)
}

@Composable
fun MainAppContent() {
    val navController = rememberNavController()
    val viewModel: MathViewModel = viewModel()

    val navItems = listOf(
        NavigationCategory.Frontier,
        NavigationCategory.Lectures,
        NavigationCategory.Exams,
        NavigationCategory.Solver,
        NavigationCategory.Insights
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            // Standard M3 dynamic navigation bar, styled elegantly with cosmic palette
            NavigationBar(
                modifier = Modifier.testTag("app_navigation_bar"),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = NavigationBarDefaults.Elevation
            ) {
                navItems.forEach { item ->
                    val isSelected = currentRoute == item.route
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            if (currentRoute != item.route) {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title
                            )
                        },
                        label = {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier.testTag("nav_item_${item.route}")
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavigationCategory.Frontier.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavigationCategory.Frontier.route) {
                DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToCourses = { navController.navigate(NavigationCategory.Lectures.route) },
                    onNavigateToTests = { navController.navigate(NavigationCategory.Exams.route) }
                )
            }
            composable(NavigationCategory.Lectures.route) {
                CoursesScreen(viewModel = viewModel)
            }
            composable(NavigationCategory.Exams.route) {
                TestSeriesScreen(viewModel = viewModel)
            }
            composable(NavigationCategory.Solver.route) {
                DoubtSolverScreen(viewModel = viewModel)
            }
            composable(NavigationCategory.Insights.route) {
                MathLabScreen(viewModel = viewModel)
            }
        }
    }
}
