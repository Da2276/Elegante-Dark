package com.example.data.repository

import com.example.data.local.FocusSessionDao
import com.example.data.local.TaskDao
import com.example.data.model.Category
import com.example.data.model.FocusSessionEntity
import com.example.data.model.Priority
import com.example.data.model.Subtask
import com.example.data.model.SubtaskConverter
import com.example.data.model.TaskEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.Calendar

class ProductivityRepository(
    private val taskDao: TaskDao,
    private val focusSessionDao: FocusSessionDao
) {
    val allTasks: Flow<List<TaskEntity>> = taskDao.getAllTasks()
    val allFocusSessions: Flow<List<FocusSessionEntity>> = focusSessionDao.getAllSessions()

    fun getTodayFocusMinutes(): Flow<Int?> {
        val startOfDay = getStartOfTodayMillis()
        return focusSessionDao.getTodayFocusMinutes(startOfDay)
    }

    fun getTotalFocusMinutes(): Flow<Int?> = focusSessionDao.getTotalFocusMinutes()

    fun getTotalSessionCount(): Flow<Int> = focusSessionDao.getTotalSessionCount()

    suspend fun insertTask(task: TaskEntity): Long = withContext(Dispatchers.IO) {
        taskDao.insertTask(task)
    }

    suspend fun updateTask(task: TaskEntity) = withContext(Dispatchers.IO) {
        taskDao.updateTask(task)
    }

    suspend fun deleteTask(task: TaskEntity) = withContext(Dispatchers.IO) {
        taskDao.deleteTask(task)
    }

    suspend fun deleteTaskById(id: Long) = withContext(Dispatchers.IO) {
        taskDao.deleteTaskById(id)
    }

    suspend fun toggleTaskCompletion(task: TaskEntity) = withContext(Dispatchers.IO) {
        val newStatus = !task.isCompleted
        val completedAt = if (newStatus) System.currentTimeMillis() else null
        taskDao.setTaskCompleted(task.id, newStatus, completedAt)
    }

    suspend fun toggleSubtask(task: TaskEntity, subtaskId: String) = withContext(Dispatchers.IO) {
        val currentSubtasks = task.subtasks
        val updated = currentSubtasks.map {
            if (it.id == subtaskId) it.copy(isDone = !it.isDone) else it
        }
        val updatedTask = task.copy(subtasksRaw = SubtaskConverter.fromSubtasks(updated))
        taskDao.updateTask(updatedTask)
    }

    suspend fun recordFocusSession(taskId: Long?, taskTitle: String, durationMinutes: Int) = withContext(Dispatchers.IO) {
        val session = FocusSessionEntity(
            taskId = taskId,
            taskTitle = taskTitle,
            durationMinutes = durationMinutes,
            completedAt = System.currentTimeMillis()
        )
        focusSessionDao.insertSession(session)
        if (taskId != null && taskId > 0) {
            taskDao.addCompletedMinutes(taskId, durationMinutes)
        }
    }

    suspend fun checkAndSeedInitialData() = withContext(Dispatchers.IO) {
        val count = taskDao.getTaskCount()
        if (count == 0) {
            val initialTasks = listOf(
                TaskEntity(
                    title = "Definir as 3 prioridades do dia",
                    description = "A chave para a alta produtividade pessoal é focar no essencial antes de checar e-mails ou mensagens.",
                    priority = Priority.URGENT.name,
                    category = Category.WORK.name,
                    estimatedMinutes = 15,
                    completedMinutes = 15,
                    isCompleted = true,
                    completedAt = System.currentTimeMillis() - 3600000,
                    subtasksRaw = SubtaskConverter.fromSubtasks(
                        listOf(
                            Subtask(title = "Revisar compromissos", isDone = true),
                            Subtask(title = "Escolher tarefa de maior impacto", isDone = true),
                            Subtask(title = "Eliminar distrações da bancada", isDone = true)
                        )
                    )
                ),
                TaskEntity(
                    title = "Sessão de Foco Profundo (Pomodoro)",
                    description = "Executar um bloco de foco contínuo na tarefa principal sem trocar de abas ou atender interrupções.",
                    priority = Priority.HIGH.name,
                    category = Category.PROJECTS.name,
                    estimatedMinutes = 50,
                    completedMinutes = 25,
                    isCompleted = false,
                    subtasksRaw = SubtaskConverter.fromSubtasks(
                        listOf(
                            Subtask(title = "Iniciar temporizador no app", isDone = true),
                            Subtask(title = "Concluir rascunho inicial", isDone = false),
                            Subtask(title = "Revisar entregáveis", isDone = false)
                        )
                    )
                ),
                TaskEntity(
                    title = "Leitura e Aprendizado Contínuo",
                    description = "Dedicar ao menos 20 minutos para leitura de artigos técnicos ou livro de aperfeiçoamento.",
                    priority = Priority.MEDIUM.name,
                    category = Category.STUDY.name,
                    estimatedMinutes = 25,
                    completedMinutes = 0,
                    isCompleted = false,
                    subtasksRaw = SubtaskConverter.fromSubtasks(
                        listOf(
                            Subtask(title = "Ler 1 capítulo", isDone = false),
                            Subtask(title = "Anotar 1 aprendizado prático", isDone = false)
                        )
                    )
                ),
                TaskEntity(
                    title = "Pausa Ativa e Hidratação",
                    description = "Alongamento rápido de 5 minutos e beber um copo d'água para manter a energia física e mental.",
                    priority = Priority.LOW.name,
                    category = Category.HEALTH.name,
                    estimatedMinutes = 10,
                    completedMinutes = 0,
                    isCompleted = false,
                    subtasksRaw = SubtaskConverter.fromSubtasks(
                        listOf(
                            Subtask(title = "Alongar pescoço e ombros", isDone = false),
                            Subtask(title = "Caminhar 2 minutos", isDone = false)
                        )
                    )
                )
            )
            taskDao.insertAll(initialTasks)

            // Seed one completed session for statistics realism
            focusSessionDao.insertSession(
                FocusSessionEntity(
                    taskId = null,
                    taskTitle = "Planejamento Matinal",
                    durationMinutes = 25,
                    completedAt = System.currentTimeMillis() - 7200000
                )
            )
        }
    }

    private fun getStartOfTodayMillis(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}
