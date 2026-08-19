package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.navigation.Screen
import com.example.ui.screens.AccountScreen
import com.example.ui.screens.AssistantScreen
import com.example.ui.screens.CounterScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PrivacyPolicyScreen
import com.example.ui.screens.ProGuildScreen
import com.example.ui.screens.ProjectsScreen
import com.example.ui.screens.StashScreen
import com.example.ui.screens.ToolsScreen
import com.example.ui.theme.LoopCrochetTheme
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.CrochetViewModel
import com.example.ui.viewmodel.GeminiAssistantViewModel

class MainActivity : ComponentActivity() {

    private val crochetViewModel: CrochetViewModel by viewModels()
    private val assistantViewModel: GeminiAssistantViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        crochetViewModel.initializeBilling(this)

        setContent {
            LoopCrochetTheme {
                LoopCrochetApp(
                    crochetViewModel = crochetViewModel,
                    assistantViewModel = assistantViewModel,
                    authViewModel = authViewModel
                )
            }
        }
    }
}

@Composable
fun LoopCrochetApp(
    crochetViewModel: CrochetViewModel,
    assistantViewModel: GeminiAssistantViewModel,
    authViewModel: AuthViewModel
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route

    val navigationItems = listOf(
        Screen.Home,
        Screen.Projects,
        Screen.Counter,
        Screen.Stash,
        Screen.Tools,
        Screen.Assistant,
        Screen.ProGuild,
        Screen.Account
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("bottom_navigation_bar"),
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                navigationItems.forEach { screen ->
                    val isSelected = currentRoute == screen.route
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            if (currentRoute != screen.route) {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                contentDescription = screen.title
                            )
                        },
                        label = { Text(screen.title) },
                        modifier = Modifier.testTag("nav_item_${screen.route}")
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = crochetViewModel,
                    onNavigateToProjects = { navController.navigate(Screen.Projects.route) },
                    onNavigateToCounter = { navController.navigate(Screen.Counter.route) },
                    onNavigateToStash = { navController.navigate(Screen.Stash.route) },
                    onSelectProject = { project ->
                        crochetViewModel.selectProject(project)
                    },
                    onNavigateToProGuild = { navController.navigate(Screen.ProGuild.route) },
                    onNavigateToAccount = { navController.navigate(Screen.Account.route) },
                    authViewModel = authViewModel
                )
            }

            composable(Screen.Projects.route) {
                ProjectsScreen(
                    viewModel = crochetViewModel,
                    onOpenCounter = { project ->
                        crochetViewModel.selectProject(project)
                        navController.navigate(Screen.Counter.route)
                    }
                )
            }

            composable(Screen.Counter.route) {
                CounterScreen(
                    viewModel = crochetViewModel
                )
            }

            composable(Screen.Stash.route) {
                StashScreen(
                    viewModel = crochetViewModel
                )
            }

            composable(Screen.Tools.route) {
                ToolsScreen()
            }

            composable(Screen.Assistant.route) {
                AssistantScreen(
                    viewModel = assistantViewModel
                )
            }

            composable(Screen.ProGuild.route) {
                ProGuildScreen(
                    viewModel = crochetViewModel
                )
            }

            composable(Screen.Account.route) {
                AccountScreen(
                    authViewModel = authViewModel,
                    crochetViewModel = crochetViewModel,
                    onNavigateToProGuild = { navController.navigate(Screen.ProGuild.route) },
                    onNavigateToPrivacyPolicy = { navController.navigate(Screen.PrivacyPolicy.route) }
                )
            }

            composable(Screen.PrivacyPolicy.route) {
                PrivacyPolicyScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
