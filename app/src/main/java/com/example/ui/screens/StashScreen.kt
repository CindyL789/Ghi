package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.YarnItem
import com.example.ui.viewmodel.CrochetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StashScreen(
    viewModel: CrochetViewModel,
    modifier: Modifier = Modifier
) {
    val yarnStash by viewModel.allYarn.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var yarnToEdit by remember { mutableStateOf<YarnItem?>(null) }

    val totalSkeins = yarnStash.sumOf { it.skeins.toDouble() }
    val totalYards = yarnStash.sumOf { (it.skeins * it.yardsPerSkein).toDouble() }.toInt()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    yarnToEdit = null
                    showAddDialog = true
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("add_yarn_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Yarn")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Yarn Stash Inventory",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Stash Summary Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = String.format("%.1f", totalSkeins),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = "Skeins in Stash",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(30.dp)
                            .background(MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.3f))
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$totalYards yd",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = "Est. Total Length",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (yarnStash.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Inventory2,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Your stash is currently empty!",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(yarnStash, key = { it.id }) { yarn ->
                        YarnCard(
                            yarn = yarn,
                            onEdit = {
                                yarnToEdit = yarn
                                showAddDialog = true
                            },
                            onDelete = { viewModel.deleteYarn(yarn) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        YarnEditDialog(
            existingYarn = yarnToEdit,
            onDismiss = { showAddDialog = false },
            onSave = { updated ->
                viewModel.saveYarn(updated)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun YarnCard(
    yarn: YarnItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val swatchColor = try {
        Color(android.graphics.Color.parseColor(yarn.colorHex))
    } catch (_: Exception) {
        MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("yarn_card_${yarn.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Color Swatch
            Surface(
                shape = CircleShape,
                color = swatchColor,
                modifier = Modifier
                    .size(48.dp)
                    .border(2.dp, MaterialTheme.colorScheme.surfaceVariant, CircleShape)
            ) {}

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = yarn.brand,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = yarn.colorway,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${yarn.weight} • ${yarn.fiberContent}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Skeins: ${yarn.skeins} (${yarn.skeins * yarn.yardsPerSkein} yd)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Row {
                IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.outline)
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun YarnEditDialog(
    existingYarn: YarnItem?,
    onDismiss: () -> Unit,
    onSave: (YarnItem) -> Unit
) {
    var brand by remember { mutableStateOf(existingYarn?.brand ?: "") }
    var colorway by remember { mutableStateOf(existingYarn?.colorway ?: "") }
    var weight by remember { mutableStateOf(existingYarn?.weight ?: "Worsted (4)") }
    var skeinsText by remember { mutableStateOf(existingYarn?.skeins?.toString() ?: "1.0") }
    var yardsText by remember { mutableStateOf(existingYarn?.yardsPerSkein?.toString() ?: "200") }
    var fiberContent by remember { mutableStateOf(existingYarn?.fiberContent ?: "100% Wool") }
    var colorHex by remember { mutableStateOf(existingYarn?.colorHex ?: "#E07A5F") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existingYarn == null) "Add Yarn to Stash" else "Edit Yarn") },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    OutlinedTextField(
                        value = brand,
                        onValueChange = { brand = it },
                        label = { Text("Brand Name") },
                        modifier = Modifier.fillMaxWidth().testTag("dialog_yarn_brand")
                    )
                }
                item {
                    OutlinedTextField(
                        value = colorway,
                        onValueChange = { colorway = it },
                        label = { Text("Colorway / Shade Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = weight,
                        onValueChange = { weight = it },
                        label = { Text("Yarn Weight (Worsted, DK, Sport, etc.)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = skeinsText,
                        onValueChange = { skeinsText = it },
                        label = { Text("Skeins Quantity") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = yardsText,
                        onValueChange = { yardsText = it },
                        label = { Text("Yards per Skein") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = fiberContent,
                        onValueChange = { fiberContent = it },
                        label = { Text("Fiber Content (e.g., 100% Cotton)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = colorHex,
                        onValueChange = { colorHex = it },
                        label = { Text("Color Hex Swatch (e.g. #E07A5F)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (brand.isNotBlank() && colorway.isNotBlank()) {
                        val skeins = skeinsText.toFloatOrNull() ?: 1.0f
                        val yards = yardsText.toIntOrNull() ?: 200
                        val updated = existingYarn?.copy(
                            brand = brand,
                            colorway = colorway,
                            weight = weight,
                            skeins = skeins,
                            yardsPerSkein = yards,
                            fiberContent = fiberContent,
                            colorHex = colorHex
                        ) ?: YarnItem(
                            brand = brand,
                            colorway = colorway,
                            weight = weight,
                            skeins = skeins,
                            yardsPerSkein = yards,
                            fiberContent = fiberContent,
                            colorHex = colorHex
                        )
                        onSave(updated)
                    }
                },
                modifier = Modifier.testTag("save_yarn_button")
            ) {
                Text("Save to Stash")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
