package com.example.agenthq.ui.screens.pullrequest

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

enum class ReviewEvent(val label: String, val apiValue: String) {
    APPROVE("Approve", "APPROVE"),
    REQUEST_CHANGES("Request Changes", "REQUEST_CHANGES"),
    COMMENT("Comment", "COMMENT")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewSubmitSheet(
    onDismiss: () -> Unit,
    onSubmit: (event: String, body: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedEvent by remember { mutableStateOf(ReviewEvent.COMMENT) }
    var body by remember { mutableStateOf("") }

    val isSubmitEnabled = when (selectedEvent) {
        ReviewEvent.REQUEST_CHANGES -> body.isNotBlank()
        ReviewEvent.APPROVE, ReviewEvent.COMMENT -> true
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Submit Review", style = MaterialTheme.typography.titleLarge)

            Column(modifier = Modifier.selectableGroup()) {
                ReviewEvent.entries.forEach { event ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = selectedEvent == event,
                                onClick = { selectedEvent = event },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RadioButton(
                            selected = selectedEvent == event,
                            onClick = null
                        )
                        Text(event.label, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            OutlinedTextField(
                value = body,
                onValueChange = { body = it },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    val hint = if (selectedEvent == ReviewEvent.REQUEST_CHANGES) "Review comment (required)" else "Review comment (optional)"
                    Text(hint)
                },
                minLines = 3,
                maxLines = 6
            )

            Spacer(Modifier.height(4.dp))

            Button(
                onClick = { onSubmit(selectedEvent.apiValue, body) },
                enabled = isSubmitEnabled,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit ${selectedEvent.label}")
            }
        }
    }
}
