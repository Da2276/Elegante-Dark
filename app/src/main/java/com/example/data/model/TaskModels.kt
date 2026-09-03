package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

enum class Priority(val displayName: String, val weight: Int) {
    URGENT("Urgente", 4),
    HIGH("Alta", 3),
    MEDIUM("Média", 2),
    LOW("Baixa", 1);

    companion object {
        fun fromString(value: String): Priority {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: MEDIUM
        }
    }
}

enum class Category(val displayName: String) {
    WORK("Trabalho"),
    PERSONAL("Pessoal"),
    STUDY("Estudos"),
    HEALTH("Saúde"),
    PROJECTS("Projetos");

    companion object {
        fun fromString(value: String): Category {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: WORK
        }
    }
}

data class Subtask(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val isDone: Boolean = false
)

object SubtaskConverter {
    fun fromSubtasks(subtasks: List<Subtask>): String {
        if (subtasks.isEmpty()) return ""
        return subtasks.joinToString(";;;") { "${it.id}:::${it.title.replace(":::", "")}:::${it.isDone}" }
    }

    fun toSubtasks(encoded: String): List<Subtask> {
        if (encoded.isBlank()) return emptyList()
        return encoded.split(";;;").mapNotNull { part ->
            val tokens = part.split(":::")
            if (tokens.size >= 3) {
                Subtask(
                    id = tokens[0],
                    title = tokens[1],
                    isDone = tokens[2].toBooleanStrictOrNull() ?: false
                )
            } else null
        }
    }
}

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val priority: String = Priority.MEDIUM.name,
    val category: String = Category.WORK.name,
    val dueDate: Long? = null,
    val estimatedMinutes: Int = 25,
    val completedMinutes: Int = 0,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val subtasksRaw: String = ""
) {
    val subtasks: List<Subtask>
        get() = SubtaskConverter.toSubtasks(subtasksRaw)

    val priorityEnum: Priority
        get() = Priority.fromString(priority)

    val categoryEnum: Category
        get() = Category.fromString(category)
}

@Entity(tableName = "focus_sessions")
data class FocusSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val taskId: Long? = null,
    val taskTitle: String = "Foco Geral",
    val durationMinutes: Int = 25,
    val completedAt: Long = System.currentTimeMillis()
)
