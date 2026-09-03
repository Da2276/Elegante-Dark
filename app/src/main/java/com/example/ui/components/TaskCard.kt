package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Tag
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Category
import com.example.data.model.Priority
import com.example.data.model.TaskEntity
import com.example.ui.theme.ElegantDarkOutline
import com.example.ui.theme.ElegantDarkSurface
import com.example.ui.theme.ElegantDarkSurfaceVariant
import com.example.ui.theme.ElegantDarkOnSurface
import com.example.ui.theme.ElegantDarkOnSurfaceVariant
import com.example.ui.theme.ElegantLilacPrimary
import com.example.ui.theme.ElegantPurpleContainer
import com.example.ui.theme.ElegantPurpleOnPrimary
import com.example.ui.theme.PriorityHigh
import com.example.ui.theme.PriorityLow
import com.example.ui.theme.PriorityMedium
import com.example.ui.theme.PriorityUrgent

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TaskCard(
    task: TaskEntity,
    onToggleTask: () -> Unit,
    onToggleSubtask: (String) -> Unit,
    onEditTask: () -> Unit,
    onDeleteTask: () -> Unit,
    onStartFocus: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    val priorityColor = when (task.priorityEnum) {
        Priority.URGENT -> PriorityUrgent
        Priority.HIGH -> PriorityHigh
        Priority.MEDIUM -> PriorityMedium
        Priority.LOW -> PriorityLow
    }

    val categoryColor = when (task.categoryEnum) {
        Category.WORK -> ElegantLilacPrimary
        Category.PERSONAL -> MaterialTheme.colorScheme.secondary
        Category.STUDY -> MaterialTheme.colorScheme.tertiary
        Category.HEALTH -> Color(0xFFA8DAB5)
        Category.PROJECTS -> Color(0xFFD0BCFF)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("task_item_${task.id}")
            .animateContentSize(animationSpec = spring()),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted) {
                ElegantDarkSurfaceVariant.copy(alpha = 0.5f)
            } else {
                ElegantDarkSurface
            }
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (task.isCompleted) Color.Transparent else ElegantDarkOutline
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Top Row: Priority Badge, Category Tag, Options
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Priority Badge (Matches Elegant Dark Lilac/Purple Badge)
                    Surface(
                        color = ElegantPurpleContainer,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(priorityColor)
                            )
                            Text(
                                text = task.priorityEnum.displayName,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = priorityColor
                            )
                        }
                    }

                    // Category Tag
                    Surface(
                        color = ElegantDarkSurfaceVariant,
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, ElegantDarkOutline.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Tag,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = categoryColor
                            )
                            Text(
                                text = task.categoryEnum.displayName,
                                style = MaterialTheme.typography.labelSmall,
                                color = categoryColor,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Time estimate & action buttons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = ElegantDarkSurfaceVariant,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Schedule,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = ElegantDarkOnSurfaceVariant
                            )
                            Text(
                                text = "${task.completedMinutes}/${task.estimatedMinutes}m",
                                style = MaterialTheme.typography.labelSmall,
                                color = ElegantDarkOnSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    IconButton(
                        onClick = onEditTask,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar tarefa",
                            modifier = Modifier.size(18.dp),
                            tint = ElegantDarkOnSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = onDeleteTask,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Excluir tarefa",
                            modifier = Modifier.size(18.dp),
                            tint = PriorityUrgent.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Row: Elegant Checkbox + Title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleTask() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Checkbox matching Elegant Dark design: rounded square with lilac accent
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .then(
                            if (task.isCompleted) {
                                Modifier.background(ElegantLilacPrimary)
                            } else {
                                Modifier
                                    .background(Color.Transparent)
                                    .border(2.dp, ElegantLilacPrimary, RoundedCornerShape(6.dp))
                            }
                        )
                        .clickable { onToggleTask() },
                    contentAlignment = Alignment.Center
                ) {
                    if (task.isCompleted) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Concluída",
                            tint = ElegantPurpleOnPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = if (task.isCompleted) {
                            ElegantDarkOnSurface.copy(alpha = 0.45f)
                        } else {
                            ElegantDarkOnSurface
                        },
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    )

                    if (task.description.isNotBlank()) {
                        Text(
                            text = task.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (task.isCompleted) {
                                ElegantDarkOnSurfaceVariant.copy(alpha = 0.4f)
                            } else {
                                ElegantDarkOnSurfaceVariant
                            },
                            maxLines = if (isExpanded) 10 else 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }

            // Subtasks Summary / Toggle
            val subtasks = task.subtasks
            if (subtasks.isNotEmpty()) {
                val doneCount = subtasks.count { it.isDone }
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { isExpanded = !isExpanded }
                        .padding(vertical = 4.dp, horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Subtarefas ($doneCount/${subtasks.size})",
                        style = MaterialTheme.typography.labelMedium,
                        color = ElegantLilacPrimary,
                        fontWeight = FontWeight.Medium
                    )
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Expandir subtarefas",
                        tint = ElegantLilacPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                AnimatedVisibility(visible = isExpanded) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        subtasks.forEach { subtask ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { onToggleSubtask(subtask.id) }
                                    .padding(vertical = 4.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (subtask.isDone) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircleOutline,
                                    contentDescription = null,
                                    tint = if (subtask.isDone) ElegantLilacPrimary else ElegantDarkOutline,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = subtask.title,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (subtask.isDone) ElegantDarkOnSurface.copy(alpha = 0.5f) else ElegantDarkOnSurface,
                                    textDecoration = if (subtask.isDone) TextDecoration.LineThrough else TextDecoration.None
                                )
                            }
                        }
                    }
                }
            }

            // Bottom Quick Action: Focus button if not completed
            if (!task.isCompleted) {
                Spacer(modifier = Modifier.height(12.dp))
                FilledTonalButton(
                    onClick = onStartFocus,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = ElegantPurpleContainer,
                        contentColor = ElegantLilacPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Focar com Pomodoro",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
