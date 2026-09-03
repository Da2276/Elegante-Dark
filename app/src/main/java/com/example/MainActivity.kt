package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.TaskEntity
import com.example.ui.components.TaskDialog
import com.example.ui.screens.FocusScreen
import com.example.ui.screens.StatsScreen
import com.example.ui.screens.TasksScreen
import com.example.ui.theme.ElegantDarkBackground
import com.example.ui.theme.ElegantDarkOnSurfaceVariant
import com.example.ui.theme.ElegantDarkOutline
import com.example.ui.theme.ElegantDarkSurfaceVariant
import com.example.ui.theme.ElegantLilacPrimary
import com.example.ui.theme.ElegantPurpleContainer
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.ProductivityViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: ProductivityViewModel by viewModels {
        ProductivityViewModel.provideFactory(application)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                var selectedTab by rememberSaveable { mutableIntStateOf(0) }
                var showTaskDialog by remember { mutableStateOf(false) }
                var taskToEdit by remember { mutableStateOf<TaskEntity?>(null) }

                val tasks by viewModel.filteredTasks.collectAsStateWithLifecycle()
                val allTasks by viewModel.allTasks.collectAsStateWithLifecycle()
                val stats by viewModel.stats.collectAsStateWithLifecycle()
                val timerState by viewModel.timerState.collectAsStateWithLifecycle()
                val allFocusSessions by viewModel.allFocusSessions.collectAsStateWithLifecycle()
                val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
                val statusFilter by viewModel.statusFilter.collectAsStateWithLifecycle()
                val categoryFilter by viewModel.categoryFilter.collectAsStateWithLifecycle()
                val priorityFilter by viewModel.priorityFilter.collectAsStateWithLifecycle()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = {
                                Text(
                                    text = when (selectedTab) {
                                        0 -> "Minhas Tarefas"
                                        1 -> "Temporizador Pomodoro"
                                        else -> "Métricas de Produtividade"
                                    },
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = MaterialTheme.colorScheme.background
                            )
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            modifier = Modifier
                                .testTag("main_navigation_bar")
                                .drawBehind {
                                    drawLine(
                                        color = ElegantDarkOutline,
                                        start = Offset(0f, 0f),
                                        end = Offset(size.width, 0f),
                                        strokeWidth = 1.dp.toPx()
                                    )
                                },
                            containerColor = ElegantDarkSurfaceVariant
                        ) {
                            val navColors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ElegantLilacPrimary,
                                selectedTextColor = ElegantLilacPrimary,
                                indicatorColor = ElegantPurpleContainer,
                                unselectedIconColor = ElegantDarkOnSurfaceVariant,
                                unselectedTextColor = ElegantDarkOnSurfaceVariant
                            )

                            NavigationBarItem(
                                selected = selectedTab == 0,
                                onClick = { selectedTab = 0 },
                                icon = {
                                    Icon(
                                        imageVector = if (selectedTab == 0) Icons.Filled.Checklist else Icons.Outlined.Checklist,
                                        contentDescription = "Tarefas"
                                    )
                                },
                                label = { Text("Tarefas") },
                                colors = navColors,
                                modifier = Modifier.testTag("nav_tab_tasks")
                            )

                            NavigationBarItem(
                                selected = selectedTab == 1,
                                onClick = { selectedTab = 1 },
                                icon = {
                                    Icon(
                                        imageVector = if (selectedTab == 1) Icons.Filled.Timer else Icons.Outlined.Timer,
                                        contentDescription = "Foco"
                                    )
                                },
                                label = { Text("Foco") },
                                colors = navColors,
                                modifier = Modifier.testTag("nav_tab_focus")
                            )

                            NavigationBarItem(
                                selected = selectedTab == 2,
                                onClick = { selectedTab = 2 },
                                icon = {
                                    Icon(
                                        imageVector = if (selectedTab == 2) Icons.Filled.Insights else Icons.Outlined.Insights,
                                        contentDescription = "Produtividade"
                                    )
                                },
                                label = { Text("Métricas") },
                                colors = navColors,
                                modifier = Modifier.testTag("nav_tab_stats")
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (selectedTab) {
                            0 -> TasksScreen(
                                tasks = tasks,
                                stats = stats,
                                searchQuery = searchQuery,
                                onSearchChange = { viewModel.setSearchQuery(it) },
                                selectedStatus = statusFilter,
                                onStatusChange = { viewModel.setStatusFilter(it) },
                                selectedCategory = categoryFilter,
                                onCategoryChange = { viewModel.setCategoryFilter(it) },
                                selectedPriority = priorityFilter,
                                onPriorityChange = { viewModel.setPriorityFilter(it) },
                                onToggleTask = { viewModel.toggleTask(it) },
                                onToggleSubtask = { task, subtaskId ->
                                    viewModel.toggleSubtask(task, subtaskId)
                                },
                                onEditTask = { task ->
                                    taskToEdit = task
                                    showTaskDialog = true
                                },
                                onDeleteTask = { viewModel.deleteTask(it) },
                                onStartFocus = { task ->
                                    viewModel.selectTaskForFocus(task)
                                    selectedTab = 1
                                },
                                onAddTask = {
                                    taskToEdit = null
                                    showTaskDialog = true
                                }
                            )

                            1 -> FocusScreen(
                                timerState = timerState,
                                availableTasks = allTasks,
                                onStartTimer = { viewModel.startTimer() },
                                onPauseTimer = { viewModel.pauseTimer() },
                                onResetTimer = { viewModel.resetTimer() },
                                onSwitchMode = { viewModel.switchTimerMode(it) },
                                onSelectTask = { viewModel.selectTaskForFocus(it) },
                                onSetCustomMinutes = { viewModel.setCustomDurationMinutes(it) }
                            )

                            2 -> StatsScreen(
                                stats = stats,
                                allTasks = allTasks,
                                sessions = allFocusSessions
                            )
                        }
                    }

                    if (showTaskDialog) {
                        TaskDialog(
                            taskToEdit = taskToEdit,
                            onDismiss = {
                                showTaskDialog = false
                                taskToEdit = null
                            },
                            onSave = { title, description, priority, category, estimatedMinutes, subtasks ->
                                viewModel.saveTask(
                                    id = taskToEdit?.id ?: 0L,
                                    title = title,
                                    description = description,
                                    priority = priority,
                                    category = category,
                                    estimatedMinutes = estimatedMinutes,
                                    subtasks = subtasks
                                )
                                showTaskDialog = false
                                taskToEdit = null
                            }
                        )
                    }
                }
            }
        }
    }
}

// Kept for screenshot test compatibility
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}
