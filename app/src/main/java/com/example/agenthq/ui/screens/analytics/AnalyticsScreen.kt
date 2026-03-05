package com.example.agenthq.ui.screens.analytics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.agenthq.domain.model.AnalyticsData
import com.example.agenthq.domain.model.PrSummary
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(viewModel: AnalyticsViewModel = hiltViewModel()) {
    val analytics by viewModel.analytics.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Analytics") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        if (analytics == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Loading analytics…",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            analytics?.let { data ->
                AnalyticsContent(
                    data = data,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@Composable
private fun AnalyticsContent(data: AnalyticsData, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(Modifier.height(4.dp)) }

        // 1. Agent vs Human PR comparison
        item {
            Text("PR Comparison", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PrStatCard(
                    label = "Agent PRs",
                    count = data.totalAgentPrs,
                    mergeRate = data.agentMergeRate,
                    modifier = Modifier.weight(1f)
                )
                PrStatCard(
                    label = "Human PRs",
                    count = data.totalHumanPrs,
                    mergeRate = data.humanMergeRate,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 2. Success score
        item {
            Text("Agent Success Score", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = String.format(Locale.getDefault(), "%.0f / 100", data.agentSuccessScore),
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { data.agentSuccessScore / 100f },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // 3. Status breakdown
        item {
            Text("Status Breakdown", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf("active", "paused", "completed", "failed").forEach { status ->
                    val count = data.agentPrsByStatus[status] ?: 0
                    SuggestionChip(
                        onClick = {},
                        label = { Text("${status.replaceFirstChar { it.uppercase() }} ($count)") }
                    )
                }
            }
        }

        // 4. Cycle time comparison
        item {
            Text("Cycle Time", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Agent avg: ${String.format(Locale.getDefault(), "%.1f", data.avgAgentCycleHours)} hours",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Human avg: ${String.format(Locale.getDefault(), "%.1f", data.avgHumanCycleHours)} hours",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        // 5. Steering activity
        item {
            Text("Steering Activity", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Text(
                    text = "Total steering instructions sent: ${data.totalSteeringComments}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        // 6. Recent agent PRs
        if (data.recentAgentPrs.isNotEmpty()) {
            item {
                Text("Recent Agent PRs", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
            }
            items(data.recentAgentPrs) { pr ->
                RecentPrCard(pr)
            }
        }

        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
private fun PrStatCard(label: String, count: Int, mergeRate: Float, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = label, style = MaterialTheme.typography.labelMedium)
            Text(text = "$count", style = MaterialTheme.typography.headlineSmall)
            Text(
                text = String.format(Locale.getDefault(), "%.0f%% merged", mergeRate * 100f),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun RecentPrCard(pr: PrSummary) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "#${pr.number} ${pr.title}",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "${pr.repoFullName} · ${pr.status}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (pr.mergedAt != null) {
                Text(
                    text = "Merged: ${pr.mergedAt}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
