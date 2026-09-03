package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.Category
import com.example.data.model.Priority
import com.example.data.model.Subtask
import com.example.data.model.TaskEntity
import com.example.ui.theme.ElegantDarkBackground
import com.example.ui.theme.ElegantDarkOnSurface
import com.example.ui.theme.ElegantDarkOnSurfaceVariant
import com.example.ui.theme.ElegantDarkOutline
import com.example.ui.theme.ElegantDarkSurface
import com.example.ui.theme.ElegantDarkSurfaceVariant
import com.example.ui.theme.ElegantLilacPrimary
import com.example.ui.theme.ElegantPurpleContainer
import com.example.ui.theme.ElegantPurpleOnPrimary
import com.example.ui.theme.PriorityHigh
import com.example.ui.theme.PriorityLow
import com.example.ui.theme.PriorityMedium
import com.example.ui.theme.PriorityUrgent

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TaskDialog(
    taskToEdit: TaskEntity? = null,
    onDismiss: () -> Unit,
    onSave: (
        title: String,
        description: String,
        priority: Priority,
        category: Category,
        estimatedMinutes: Int,
        subtasks: List<Subtask>
    ) -> Unit
) {
    var title by remember { mutableStateOf(taskToEdit?.title ?: "") }
    var description by remember { mutableStateOf(taskToEdit?.description ?: "") }
    var priority by remember { mutableStateOf(taskToEdit?.priorityEnum ?: Priority.HIGH) }
    var category by remember { mutableStateOf(taskToEdit?.categoryEnum ?: Category.WORK) }
    var estimatedMinutes by remember { mutableIntStateOf(taskToEdit?.estimatedMinutes ?: 25) }

    val subtasksList = remember {
        mutableStateListOf<Subtask>().apply {
            if (taskToEdit != null) {
                addAll(taskToEdit.subtasks)
            }
        }
    }
    var newSubtaskText by remember { mutableStateOf("") }
    var titleError by remember { mutableStateOf(false) }

    val durationOptions = listOf(15, 25, 45, 60, 90)

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = ElegantDarkSurface,
        unfocusedContainerColor = ElegantDarkSurface,
        focusedBorderColor = ElegantLilacPrimary,
        unfocusedBorderColor = ElegantDarkOutline,
        focusedTextColor = ElegantDarkOnSurface,
        unfocusedTextColor = ElegantDarkOnSurface,
        cursorColor = ElegantLilacPrimary,
        focusedLabelColor = ElegantLilacPrimary,
        unfocusedLabelColor = ElegantDarkOnSurfaceVariant,
        focusedPlaceholderColor = ElegantDarkOnSurfaceVariant,
        unfocusedPlaceholderColor = ElegantDarkOnSurfaceVariant
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ElegantDarkSurface,
        title = {
            Text(
                text = if (taskToEdit == null) "Nova Tarefa" else "Editar Tarefa",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = ElegantDarkOnSurface
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Title Field
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        if (titleError && it.isNotBlank()) titleError = false
                    },
                    label = { Text("Título da Tarefa *") },
                    placeholder = { Text("Ex: Finalizar relatório financeiro") },
                    isError = titleError,
                    supportingText = if (titleError) {
                        { Text("Por favor, digite o título da tarefa.") }
                    } else null,
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("task_title_input")
                )

                // Description Field
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrição / Notas (opcional)") },
                    placeholder = { Text("Adicione detalhes, links ou contexto...") },
                    maxLines = 3,
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors,
                    modifier = Modifier.fillMaxWidth()
                )

                // Priority Selection
                Text(
                    text = "Prioridade",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = ElegantDarkOnSurface
                )
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Priority.entries.forEach { p ->
                        val isSelected = priority == p
                        val color = when (p) {
                            Priority.URGENT -> PriorityUrgent
                            Priority.HIGH -> PriorityHigh
                            Priority.MEDIUM -> PriorityMedium
                            Priority.LOW -> PriorityLow
                        }
                        Surface(
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable { priority = p },
                            shape = CircleShape,
                            color = if (isSelected) color.copy(alpha = 0.2f) else ElegantDarkSurface,
                            border = BorderStroke(1.dp, if (isSelected) color else ElegantDarkOutline)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                )
                                Text(
                                    text = p.displayName,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) color else ElegantDarkOnSurface
                                )
                            }
                        }
                    }
                }

                // Category Selection
                Text(
                    text = "Categoria",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = ElegantDarkOnSurface
                )
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Category.entries.forEach { c ->
                        val isSelected = category == c
                        Surface(
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable { category = c },
                            shape = CircleShape,
                            color = if (isSelected) ElegantLilacPrimary else ElegantDarkSurface,
                            border = if (isSelected) null else BorderStroke(1.dp, ElegantDarkOutline)
                        ) {
                            Text(
                                text = c.displayName,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) ElegantPurpleOnPrimary else ElegantDarkOnSurface
                            )
                        }
                    }
                }

                // Time Estimate
                Text(
                    text = "Tempo Estimado (Foco)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = ElegantDarkOnSurface
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    durationOptions.forEach { minutes ->
                        val isSelected = estimatedMinutes == minutes
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(CircleShape)
                                .clickable { estimatedMinutes = minutes },
                            shape = CircleShape,
                            color = if (isSelected) ElegantLilacPrimary else ElegantDarkSurface,
                            border = if (isSelected) null else BorderStroke(1.dp, ElegantDarkOutline)
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${minutes}m",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) ElegantPurpleOnPrimary else ElegantDarkOnSurface
                                )
                            }
                        }
                    }
                }

                // Subtasks Checklist Builder
                Text(
                    text = "Subtarefas (Etapas)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = ElegantDarkOnSurface
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newSubtaskText,
                        onValueChange = { newSubtaskText = it },
                        label = { Text("Nova etapa") },
                        placeholder = { Text("Ex: Coletar métricas") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = textFieldColors,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (newSubtaskText.isNotBlank()) {
                                subtasksList.add(Subtask(title = newSubtaskText.trim()))
                                newSubtaskText = ""
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                ElegantPurpleContainer,
                                RoundedCornerShape(10.dp)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Adicionar etapa",
                            tint = ElegantLilacPrimary
                        )
                    }
                }

                // Subtask list display
                if (subtasksList.isNotEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        subtasksList.forEachIndexed { index, subtask ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = ElegantDarkSurfaceVariant,
                                border = BorderStroke(1.dp, ElegantDarkOutline.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${index + 1}. ${subtask.title}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = ElegantDarkOnSurface,
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(
                                        onClick = { subtasksList.removeAt(index) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Remover subtarefa",
                                            modifier = Modifier.size(16.dp),
                                            tint = ElegantDarkOnSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank()) {
                        titleError = true
                    } else {
                        onSave(
                            title.trim(),
                            description.trim(),
                            priority,
                            category,
                            estimatedMinutes,
                            subtasksList.toList()
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = ElegantLilacPrimary,
                    contentColor = ElegantPurpleOnPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("save_task_button")
            ) {
                Text(
                    text = if (taskToEdit == null) "Criar Tarefa" else "Salvar Alterações",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cancelar", color = ElegantDarkOnSurfaceVariant)
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}

