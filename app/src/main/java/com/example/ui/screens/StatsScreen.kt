package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Category
import com.example.data.model.FocusSessionEntity
import com.example.data.model.Priority
import com.example.data.model.TaskEntity
import com.example.ui.theme.AmberTertiary
import com.example.ui.theme.ElegantDarkBackground
import com.example.ui.theme.ElegantDarkOnSurface
import com.example.ui.theme.ElegantDarkOnSurfaceVariant
import com.example.ui.theme.ElegantDarkOutline
import com.example.ui.theme.ElegantDarkSurface
import com.example.ui.theme.ElegantDarkSurfaceVariant
import com.example.ui.theme.ElegantLilacPrimary
import com.example.ui.theme.ElegantPurpleContainer
import com.example.ui.theme.PriorityHigh
import com.example.ui.theme.PriorityLow
import com.example.ui.theme.PriorityMedium
import com.example.ui.theme.PriorityUrgent
import com.example.ui.viewmodel.ProductivityStats
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StatsScreen(
    stats: ProductivityStats,
    allTasks: List<TaskEntity>,
    sessions: List<FocusSessionEntity>,
    modifier: Modifier = Modifier
) {
    val timeFormat = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ElegantDarkBackground)
            .testTag("stats_screen"),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Streak Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = ElegantDarkSurface
                ),
                border = BorderStroke(1.dp, ElegantDarkOutline)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = ElegantPurpleContainer,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.LocalFireDepartment,
                                        contentDescription = null,
                                        tint = AmberTertiary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Sequência de Produtividade",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = ElegantDarkOnSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "${stats.streakDays} ${if (stats.streakDays == 1) "Dia Consecutivo" else "Dias Consecutivos"}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = ElegantLilacPrimary
                        )

                        Text(
                            text = "A consistência diária vence a motivação esporádica.",
                            style = MaterialTheme.typography.bodySmall,
                            color = ElegantDarkOnSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }

        // 2x2 Grid of Key Metrics
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "Taxa de Conclusão",
                        value = "${(stats.completionRate * 100).toInt()}%",
                        subtitle = "${stats.completedTasks} de ${stats.totalTasks} tarefas",
                        icon = Icons.Default.CheckCircle,
                        iconColor = ElegantLilacPrimary,
                        modifier = Modifier.weight(1f)
                    )

                    MetricCard(
                        title = "Foco Hoje",
                        value = "${stats.todayFocusMinutes} min",
                        subtitle = "Tempo focado",
                        icon = Icons.Default.Timer,
                        iconColor = Color(0xFFA8DAB5),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "Total em Foco",
                        value = "${stats.totalFocusMinutes / 60}h ${stats.totalFocusMinutes % 60}m",
                        subtitle = "Acumulado",
                        icon = Icons.Default.TrendingUp,
                        iconColor = AmberTertiary,
                        modifier = Modifier.weight(1f)
                    )

                    MetricCard(
                        title = "Sessões Pomodoro",
                        value = "${stats.totalSessionsCount}",
                        subtitle = "Blocos de foco",
                        icon = Icons.Default.SelfImprovement,
                        iconColor = ElegantLilacPrimary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Category Breakdown Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ElegantDarkSurface),
                border = BorderStroke(1.dp, ElegantDarkOutline)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Distribuição por Categoria",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ElegantDarkOnSurface
                    )

                    Category.entries.forEach { category ->
                        val tasksInCat = allTasks.count { it.categoryEnum == category }
                        val fraction = if (allTasks.isNotEmpty()) tasksInCat.toFloat() / allTasks.size.toFloat() else 0f

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = category.displayName,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium,
                                    color = ElegantDarkOnSurface
                                )
                                Text(
                                    text = "$tasksInCat tarefas (${(fraction * 100).toInt()}%)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ElegantDarkOnSurfaceVariant
                                )
                            }
                            LinearProgressIndicator(
                                progress = { fraction },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = ElegantLilacPrimary,
                                trackColor = ElegantDarkOutline,
                                strokeCap = StrokeCap.Round
                            )
                        }
                    }
                }
            }
        }

        // Priority Distribution
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ElegantDarkSurface),
                border = BorderStroke(1.dp, ElegantDarkOutline)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Matriz de Prioridades",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ElegantDarkOnSurface
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Priority.entries.forEach { p ->
                            val count = allTasks.count { it.priorityEnum == p && !it.isCompleted }
                            val color = when (p) {
                                Priority.URGENT -> PriorityUrgent
                                Priority.HIGH -> PriorityHigh
                                Priority.MEDIUM -> PriorityMedium
                                Priority.LOW -> PriorityLow
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            ) {
                                Surface(
                                    color = color.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, color.copy(alpha = 0.3f)),
                                    modifier = Modifier.size(52.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "$count",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 20.sp,
                                            color = color
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = p.displayName,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ElegantDarkOnSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Recent Sessions Timeline
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ElegantDarkSurface),
                border = BorderStroke(1.dp, ElegantDarkOutline)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.History,
                            contentDescription = null,
                            tint = ElegantLilacPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Histórico Recente de Foco",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = ElegantDarkOnSurface
                        )
                    }

                    if (sessions.isEmpty()) {
                        Text(
                            text = "Nenhuma sessão de foco registrada ainda. Inicie o temporizador Pomodoro para registrar seus blocos de foco.",
                            style = MaterialTheme.typography.bodySmall,
                            color = ElegantDarkOnSurfaceVariant,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        sessions.take(5).forEach { session ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = session.taskTitle,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = ElegantDarkOnSurface
                                    )
                                    Text(
                                        text = timeFormat.format(Date(session.completedAt)),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = ElegantDarkOnSurfaceVariant
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = ElegantPurpleContainer
                                ) {
                                    Text(
                                        text = "+${session.durationMinutes} min",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = ElegantLilacPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ElegantDarkSurface),
        border = BorderStroke(1.dp, ElegantDarkOutline)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(iconColor.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = ElegantDarkOnSurface
            )

            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = ElegantDarkOnSurface
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = ElegantDarkOnSurfaceVariant
            )
        }
    }
}

