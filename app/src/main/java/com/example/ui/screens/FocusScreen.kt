package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.ui.theme.ElegantSecondary
import com.example.ui.theme.ElegantTertiary
import com.example.ui.viewmodel.TimerMode
import com.example.ui.viewmodel.TimerState
import com.example.ui.viewmodel.TimerUiState

@Composable
fun FocusScreen(
    timerState: TimerUiState,
    availableTasks: List<TaskEntity>,
    onStartTimer: () -> Unit,
    onPauseTimer: () -> Unit,
    onResetTimer: () -> Unit,
    onSwitchMode: (TimerMode) -> Unit,
    onSelectTask: (TaskEntity?) -> Unit,
    onSetCustomMinutes: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var isTaskDropdownExpanded by remember { mutableStateOf(false) }

    val activeColor = when (timerState.mode) {
        TimerMode.POMODORO -> ElegantLilacPrimary
        TimerMode.SHORT_BREAK -> ElegantSecondary
        TimerMode.LONG_BREAK -> ElegantTertiary
    }

    val animatedProgress by animateFloatAsState(
        targetValue = timerState.progress,
        animationSpec = tween(durationMillis = 500),
        label = "timerProgress"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ElegantDarkBackground),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Mode Selector Chips
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                TimerMode.entries.forEach { mode ->
                    val isSelected = timerState.mode == mode
                    Surface(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .clip(CircleShape)
                            .clickable { onSwitchMode(mode) },
                        shape = CircleShape,
                        color = if (isSelected) ElegantLilacPrimary else ElegantDarkSurface,
                        border = if (isSelected) null else BorderStroke(1.dp, ElegantDarkOutline)
                    ) {
                        Text(
                            text = "${mode.label} (${mode.minutes}m)",
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) ElegantPurpleOnPrimary else ElegantDarkOnSurface
                        )
                    }
                }
            }
        }

        // Linked Task Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("focus_task_selector"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = ElegantDarkSurface
                ),
                border = BorderStroke(1.dp, ElegantDarkOutline)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { isTaskDropdownExpanded = true }
                    ) {
                        Text(
                            text = "Tarefa em Foco",
                            style = MaterialTheme.typography.labelSmall,
                            color = ElegantLilacPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = timerState.selectedTask?.title ?: "Toque para vincular uma tarefa",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = if (timerState.selectedTask != null) {
                                ElegantDarkOnSurface
                            } else {
                                ElegantDarkOnSurfaceVariant
                            }
                        )
                    }

                    if (timerState.selectedTask != null) {
                        IconButton(
                            onClick = { onSelectTask(null) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Desvincular tarefa",
                                modifier = Modifier.size(16.dp),
                                tint = ElegantDarkOnSurfaceVariant
                            )
                        }
                    } else {
                        Button(
                            onClick = { isTaskDropdownExpanded = true },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ElegantPurpleContainer,
                                contentColor = ElegantLilacPrimary
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Escolher", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    // Dropdown for selecting tasks
                    DropdownMenu(
                        expanded = isTaskDropdownExpanded,
                        onDismissRequest = { isTaskDropdownExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Foco Geral (sem tarefa específica)") },
                            onClick = {
                                onSelectTask(null)
                                isTaskDropdownExpanded = false
                            }
                        )
                        availableTasks.filter { !it.isCompleted }.forEach { task ->
                            DropdownMenuItem(
                                text = { Text(task.title) },
                                onClick = {
                                    onSelectTask(task)
                                    isTaskDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // Circular Timer Canvas
        item {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .size(260.dp)
                    .testTag("circular_timer")
            ) {
                val trackColor = ElegantDarkOutline.copy(alpha = 0.5f)
                val sweepAngle = 360f * (1f - animatedProgress)

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 14.dp.toPx()
                    val diameter = size.minDimension - strokeWidth
                    val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
                    val arcSize = Size(diameter, diameter)

                    // Background Track
                    drawArc(
                        color = trackColor,
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )

                    // Foreground Progress
                    drawArc(
                        color = activeColor,
                        startAngle = -90f,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }

                // Inside text
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = timerState.formattedTime,
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElegantDarkOnSurface,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Surface(
                        color = ElegantPurpleContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = when (timerState.state) {
                                TimerState.RUNNING -> "EM ANDAMENTO"
                                TimerState.PAUSED -> "PAUSADO"
                                TimerState.IDLE -> timerState.mode.label.uppercase()
                            },
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = ElegantLilacPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Timer Control Buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Reset Button
                IconButton(
                    onClick = onResetTimer,
                    modifier = Modifier
                        .size(52.dp)
                        .background(ElegantDarkSurface, CircleShape)
                        .clip(CircleShape)
                        .clickable { onResetTimer() }
                        .testTag("reset_timer_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reiniciar Temporizador",
                        tint = ElegantDarkOnSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(24.dp))

                // Play / Pause Main Button (Matches Elegant Lilac Accent)
                Button(
                    onClick = {
                        if (timerState.state == TimerState.RUNNING) {
                            onPauseTimer()
                        } else {
                            onStartTimer()
                        }
                    },
                    modifier = Modifier
                        .size(72.dp)
                        .testTag("play_pause_button"),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElegantLilacPrimary,
                        contentColor = ElegantPurpleOnPrimary
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = if (timerState.state == TimerState.RUNNING) {
                            Icons.Default.Pause
                        } else {
                            Icons.Default.PlayArrow
                        },
                        contentDescription = if (timerState.state == TimerState.RUNNING) "Pausar" else "Iniciar",
                        modifier = Modifier.size(36.dp),
                        tint = ElegantPurpleOnPrimary
                    )
                }

                Spacer(modifier = Modifier.width(24.dp))

                // Preset +5 min
                Surface(
                    onClick = {
                        val currentMinutes = timerState.remainingSeconds / 60
                        onSetCustomMinutes(currentMinutes + 5)
                    },
                    modifier = Modifier.size(52.dp),
                    shape = CircleShape,
                    color = ElegantDarkSurface,
                    border = BorderStroke(1.dp, ElegantDarkOutline)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "+5m",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = ElegantLilacPrimary
                        )
                    }
                }
            }
        }

        // Deep Work Productivity Tip Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = ElegantDarkSurface
                ),
                border = BorderStroke(1.dp, ElegantDarkOutline)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(ElegantPurpleContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = ElegantLilacPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Técnica Pomodoro & Foco",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = ElegantDarkOnSurface
                        )
                        Text(
                            text = "Trabalhe 25 minutos com 100% de atenção em uma única tarefa. Quando o alarme soar, faça 5 minutos de pausa para recarregar as energias.",
                            style = MaterialTheme.typography.bodySmall,
                            color = ElegantDarkOnSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

