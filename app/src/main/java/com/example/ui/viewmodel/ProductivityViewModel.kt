package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.Category
import com.example.data.model.FocusSessionEntity
import com.example.data.model.Priority
import com.example.data.model.Subtask
import com.example.data.model.SubtaskConverter
import com.example.data.model.TaskEntity
import com.example.data.repository.ProductivityRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

enum class TaskFilterStatus(val label: String) {
    ALL("Todas"),
    PENDING("Pendentes"),
    COMPLETED("Concluídas")
}

enum class TimerMode(val label: String, val minutes: Int) {
    POMODORO("Foco", 25),
    SHORT_BREAK("Pausa Curta", 5),
    LONG_BREAK("Pausa Longa", 15)
}

enum class TimerState {
    IDLE, RUNNING, PAUSED
}

data class TimerUiState(
    val mode: TimerMode = TimerMode.POMODORO,
    val state: TimerState = TimerState.IDLE,
    val remainingSeconds: Int = TimerMode.POMODORO.minutes * 60,
    val totalSeconds: Int = TimerMode.POMODORO.minutes * 60,
    val selectedTask: TaskEntity? = null,
    val sessionsCompletedToday: Int = 0
) {
    val progress: Float
        get() = if (totalSeconds > 0) 1f - (remainingSeconds.toFloat() / totalSeconds.toFloat()) else 0f

    val formattedTime: String
        get() {
            val m = remainingSeconds / 60
            val s = remainingSeconds % 60
            return String.format("%02d:%02d", m, s)
        }
}

data class ProductivityStats(
    val totalTasks: Int = 0,
    val completedTasks: Int = 0,
    val pendingTasks: Int = 0,
    val completionRate: Float = 0f,
    val todayFocusMinutes: Int = 0,
    val totalFocusMinutes: Int = 0,
    val totalSessionsCount: Int = 0,
    val streakDays: Int = 1
)

class ProductivityViewModel(
    application: Application,
    private val repository: ProductivityRepository
) : AndroidViewModel(application) {

    val allTasks = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allFocusSessions = repository.allFocusSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filters
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _statusFilter = MutableStateFlow(TaskFilterStatus.ALL)
    val statusFilter = _statusFilter.asStateFlow()

    private val _categoryFilter = MutableStateFlow<Category?>(null)
    val categoryFilter = _categoryFilter.asStateFlow()

    private val _priorityFilter = MutableStateFlow<Priority?>(null)
    val priorityFilter = _priorityFilter.asStateFlow()

    // Filtered tasks
    val filteredTasks: StateFlow<List<TaskEntity>> = combine(
        allTasks,
        _searchQuery,
        _statusFilter,
        _categoryFilter,
        _priorityFilter
    ) { tasks, query, status, category, priority ->
        tasks.filter { task ->
            val matchesQuery = query.isBlank() ||
                    task.title.contains(query, ignoreCase = true) ||
                    task.description.contains(query, ignoreCase = true)

            val matchesStatus = when (status) {
                TaskFilterStatus.ALL -> true
                TaskFilterStatus.PENDING -> !task.isCompleted
                TaskFilterStatus.COMPLETED -> task.isCompleted
            }

            val matchesCategory = category == null || task.categoryEnum == category
            val matchesPriority = priority == null || task.priorityEnum == priority

            matchesQuery && matchesStatus && matchesCategory && matchesPriority
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Productivity Stats
    val stats: StateFlow<ProductivityStats> = combine(
        allTasks,
        repository.getTodayFocusMinutes(),
        repository.getTotalFocusMinutes(),
        repository.getTotalSessionCount()
    ) { tasks, todayMinutes, totalMinutes, sessionCount ->
        val total = tasks.size
        val completed = tasks.count { it.isCompleted }
        val pending = total - completed
        val rate = if (total > 0) completed.toFloat() / total.toFloat() else 0f

        ProductivityStats(
            totalTasks = total,
            completedTasks = completed,
            pendingTasks = pending,
            completionRate = rate,
            todayFocusMinutes = todayMinutes ?: 0,
            totalFocusMinutes = totalMinutes ?: 0,
            totalSessionsCount = sessionCount,
            streakDays = calculateStreak(tasks)
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ProductivityStats())

    // Focus Timer
    private val _timerState = MutableStateFlow(TimerUiState())
    val timerState = _timerState.asStateFlow()

    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            repository.checkAndSeedInitialData()
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setStatusFilter(status: TaskFilterStatus) {
        _statusFilter.value = status
    }

    fun setCategoryFilter(category: Category?) {
        _categoryFilter.value = category
    }

    fun setPriorityFilter(priority: Priority?) {
        _priorityFilter.value = priority
    }

    fun toggleTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.toggleTaskCompletion(task)
        }
    }

    fun toggleSubtask(task: TaskEntity, subtaskId: String) {
        viewModelScope.launch {
            repository.toggleSubtask(task, subtaskId)
        }
    }

    fun saveTask(
        id: Long = 0,
        title: String,
        description: String,
        priority: Priority,
        category: Category,
        estimatedMinutes: Int,
        subtasks: List<Subtask>
    ) {
        viewModelScope.launch {
            val task = TaskEntity(
                id = id,
                title = title.trim(),
                description = description.trim(),
                priority = priority.name,
                category = category.name,
                estimatedMinutes = estimatedMinutes,
                subtasksRaw = SubtaskConverter.fromSubtasks(subtasks)
            )
            if (id == 0L) {
                repository.insertTask(task)
            } else {
                repository.updateTask(task)
            }
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.deleteTask(task)
            if (_timerState.value.selectedTask?.id == task.id) {
                _timerState.value = _timerState.value.copy(selectedTask = null)
            }
        }
    }

    // Timer Controls
    fun selectTaskForFocus(task: TaskEntity?) {
        _timerState.value = _timerState.value.copy(selectedTask = task)
    }

    fun switchTimerMode(mode: TimerMode) {
        timerJob?.cancel()
        val seconds = mode.minutes * 60
        _timerState.value = _timerState.value.copy(
            mode = mode,
            state = TimerState.IDLE,
            remainingSeconds = seconds,
            totalSeconds = seconds
        )
    }

    fun setCustomDurationMinutes(minutes: Int) {
        timerJob?.cancel()
        val seconds = minutes * 60
        _timerState.value = _timerState.value.copy(
            state = TimerState.IDLE,
            remainingSeconds = seconds,
            totalSeconds = seconds
        )
    }

    fun startTimer() {
        if (_timerState.value.state == TimerState.RUNNING) return

        _timerState.value = _timerState.value.copy(state = TimerState.RUNNING)
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_timerState.value.remainingSeconds > 0 && _timerState.value.state == TimerState.RUNNING) {
                delay(1000L)
                val newRemaining = _timerState.value.remainingSeconds - 1
                _timerState.value = _timerState.value.copy(remainingSeconds = newRemaining)
            }

            if (_timerState.value.remainingSeconds <= 0) {
                onTimerCompleted()
            }
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
        _timerState.value = _timerState.value.copy(state = TimerState.PAUSED)
    }

    fun resetTimer() {
        timerJob?.cancel()
        val currentTotal = _timerState.value.totalSeconds
        _timerState.value = _timerState.value.copy(
            state = TimerState.IDLE,
            remainingSeconds = currentTotal
        )
    }

    private fun onTimerCompleted() {
        val current = _timerState.value
        val completedDuration = current.totalSeconds / 60
        val task = current.selectedTask

        viewModelScope.launch {
            if (current.mode == TimerMode.POMODORO) {
                repository.recordFocusSession(
                    taskId = task?.id,
                    taskTitle = task?.title ?: "Sessão de Foco",
                    durationMinutes = completedDuration
                )
            }

            // Transition to break if pomodoro finished
            val nextMode = if (current.mode == TimerMode.POMODORO) TimerMode.SHORT_BREAK else TimerMode.POMODORO
            val nextSeconds = nextMode.minutes * 60

            _timerState.value = current.copy(
                mode = nextMode,
                state = TimerState.IDLE,
                remainingSeconds = nextSeconds,
                totalSeconds = nextSeconds,
                sessionsCompletedToday = current.sessionsCompletedToday + if (current.mode == TimerMode.POMODORO) 1 else 0
            )
        }
    }

    private fun calculateStreak(tasks: List<TaskEntity>): Int {
        val completedDates = tasks.mapNotNull { it.completedAt }
        if (completedDates.isEmpty()) return 1

        val uniqueDays = completedDates.map {
            val cal = Calendar.getInstance()
            cal.timeInMillis = it
            "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.DAY_OF_YEAR)}"
        }.distinct()

        return uniqueDays.size.coerceAtLeast(1)
    }

    companion object {
        fun provideFactory(application: Application): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val db = AppDatabase.getDatabase(application)
                    val repo = ProductivityRepository(db.taskDao(), db.focusSessionDao())
                    return ProductivityViewModel(application, repo) as T
                }
            }
        }
    }
}
