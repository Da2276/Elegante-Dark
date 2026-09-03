package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.Category
import com.example.data.model.Priority
import com.example.data.model.TaskEntity
import com.example.ui.components.TaskCard
import com.example.ui.theme.ElegantDarkBackground
import com.example.ui.theme.ElegantDarkOnSurface
import com.example.ui.theme.ElegantDarkOnSurfaceVariant
import com.example.ui.theme.ElegantDarkOutline
import com.example.ui.theme.ElegantDarkSurface
import com.example.ui.theme.ElegantDarkSurfaceVariant
import com.example.ui.theme.ElegantLilacPrimary
import com.example.ui.theme.ElegantPurpleContainer
import com.example.ui.theme.ElegantPurpleOnPrimary
import com.example.ui.viewmodel.ProductivityStats
import com.example.ui.viewmodel.TaskFilterStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    tasks: List<TaskEntity>,
    stats: ProductivityStats,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedStatus: TaskFilterStatus,
    onStatusChange: (TaskFilterStatus) -> Unit,
    selectedCategory: Category?,
    onCategoryChange: (Category?) -> Unit,
    selectedPriority: Priority?,
    onPriorityChange: (Priority?) -> Unit,
    onToggleTask: (TaskEntity) -> Unit,
    onToggleSubtask: (TaskEntity, String) -> Unit,
    onEditTask: (TaskEntity) -> Unit,
    onDeleteTask: (TaskEntity) -> Unit,
    onStartFocus: (TaskEntity) -> Unit,
    onAddTask: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize().background(ElegantDarkBackground)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Productivity Summary Header Card (Elegant Dark Styled)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = ElegantDarkSurface
                    ),
                    border = BorderStroke(1.dp, ElegantDarkOutline)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Foco do Dia",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = ElegantDarkOnSurface
                                )
                                Text(
                                    text = "Produtividade Pessoal Ativa",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ElegantDarkOnSurfaceVariant
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = ElegantPurpleContainer
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = ElegantLilacPrimary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "${stats.completedTasks}/${stats.totalTasks}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = ElegantLilacPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Progress bar with Elegant Lilac and Dark Outline Track
                        LinearProgressIndicator(
                            progress = { stats.completionRate },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = ElegantLilacPrimary,
                            trackColor = ElegantDarkOutline,
                            strokeCap = StrokeCap.Round
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${(stats.completionRate * 100).toInt()}% concluído",
                                style = MaterialTheme.typography.labelSmall,
                                color = ElegantLilacPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${stats.todayFocusMinutes} min de foco hoje",
                                style = MaterialTheme.typography.labelSmall,
                                color = ElegantDarkOnSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Search Bar (Elegant Dark styled)
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("task_search_field"),
                    placeholder = {
                        Text(
                            text = "Pesquisar tarefas...",
                            color = ElegantDarkOnSurfaceVariant
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar",
                            tint = ElegantDarkOnSurfaceVariant
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Limpar busca",
                                    tint = ElegantDarkOnSurfaceVariant
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = ElegantDarkSurface,
                        unfocusedContainerColor = ElegantDarkSurface,
                        focusedBorderColor = ElegantLilacPrimary,
                        unfocusedBorderColor = ElegantDarkOutline,
                        focusedTextColor = ElegantDarkOnSurface,
                        unfocusedTextColor = ElegantDarkOnSurface,
                        cursorColor = ElegantLilacPrimary
                    )
                )
            }

            // Status Filter Pills (Matching Elegant Dark HTML Snippet)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TaskFilterStatus.entries.forEach { status ->
                        val isSelected = selectedStatus == status
                        Surface(
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable { onStatusChange(status) },
                            shape = CircleShape,
                            color = if (isSelected) ElegantLilacPrimary else ElegantDarkSurface,
                            border = if (isSelected) null else BorderStroke(1.dp, ElegantDarkOutline)
                        ) {
                            Text(
                                text = status.label,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Medium,
                                color = if (isSelected) ElegantPurpleOnPrimary else ElegantDarkOnSurface
                            )
                        }
                    }
                }
            }

            // Category Horizontal Filter Pills
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val allSelected = selectedCategory == null
                    Surface(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { onCategoryChange(null) },
                        shape = CircleShape,
                        color = if (allSelected) ElegantLilacPrimary else ElegantDarkSurface,
                        border = if (allSelected) null else BorderStroke(1.dp, ElegantDarkOutline)
                    ) {
                        Text(
                            text = "Todas Categorias",
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium,
                            color = if (allSelected) ElegantPurpleOnPrimary else ElegantDarkOnSurface
                        )
                    }

                    Category.entries.forEach { cat ->
                        val isCatSelected = selectedCategory == cat
                        Surface(
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable {
                                    onCategoryChange(if (isCatSelected) null else cat)
                                },
                            shape = CircleShape,
                            color = if (isCatSelected) ElegantLilacPrimary else ElegantDarkSurface,
                            border = if (isCatSelected) null else BorderStroke(1.dp, ElegantDarkOutline)
                        ) {
                            Text(
                                text = cat.displayName,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Medium,
                                color = if (isCatSelected) ElegantPurpleOnPrimary else ElegantDarkOnSurface
                            )
                        }
                    }
                }
            }

            // Task Items or Empty State
            if (tasks.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                modifier = Modifier.size(72.dp),
                                shape = CircleShape,
                                color = ElegantDarkSurface,
                                border = BorderStroke(1.dp, ElegantDarkOutline)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Outlined.TaskAlt,
                                        contentDescription = null,
                                        modifier = Modifier.size(36.dp),
                                        tint = ElegantLilacPrimary
                                    )
                                }
                            }
                            Text(
                                text = if (searchQuery.isNotBlank() || selectedCategory != null) {
                                    "Nenhuma tarefa corresponde ao filtro"
                                } else {
                                    "Tudo limpo por aqui!"
                                },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = ElegantDarkOnSurface
                            )
                            Text(
                                text = "Clique no botão '+' abaixo para criar uma nova tarefa e focar no que importa.",
                                style = MaterialTheme.typography.bodySmall,
                                color = ElegantDarkOnSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 32.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(tasks, key = { it.id }) { task ->
                    TaskCard(
                        task = task,
                        onToggleTask = { onToggleTask(task) },
                        onToggleSubtask = { subtaskId -> onToggleSubtask(task, subtaskId) },
                        onEditTask = { onEditTask(task) },
                        onDeleteTask = { onDeleteTask(task) },
                        onStartFocus = { onStartFocus(task) }
                    )
                }
            }
        }

        // Floating Action Button (Matches Elegant Dark rounded-2xl FAB)
        FloatingActionButton(
            onClick = onAddTask,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .size(56.dp)
                .testTag("add_task_fab"),
            containerColor = ElegantLilacPrimary,
            contentColor = ElegantPurpleOnPrimary,
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Criar Tarefa",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

