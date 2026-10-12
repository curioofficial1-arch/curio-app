package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CurioItem
import com.example.ui.components.CurioImageHelper
import com.example.ui.components.DepthBadge
import com.example.ui.theme.CurioGold
import com.example.ui.theme.CurioPrimary

@Composable
fun CurioAddEditScreen(
    initialEntry: CurioItem?,
    onSave: (
        title: String,
        category: String,
        spark: String,
        overview: String,
        keyTakeaways: List<String>,
        tangents: List<String>,
        sources: List<String>,
        rabbitHoleDepth: Int,
        colorHex: String,
        coverDrawable: String?
    ) -> Unit,
    onCancel: () -> Unit,
    onGenerateAngles: (String, (String, String, List<String>, List<String>) -> Unit) -> Unit,
    isGenerating: Boolean,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onCancel)

    var title by remember { mutableStateOf(initialEntry?.title ?: "") }
    var category by remember { mutableStateOf(initialEntry?.category ?: "Brain & Mind") }
    var spark by remember { mutableStateOf(initialEntry?.spark ?: "") }
    var overview by remember { mutableStateOf(initialEntry?.overview ?: "") }
    var rabbitHoleDepth by remember { mutableFloatStateOf((initialEntry?.rabbitHoleDepth ?: 2).toFloat()) }
    var selectedCover by remember { mutableStateOf<String?>(initialEntry?.coverDrawable) }

    val keyTakeaways = remember {
        mutableStateListOf<String>().apply {
            if (initialEntry != null && initialEntry.keyTakeaways.isNotEmpty()) {
                addAll(initialEntry.keyTakeaways)
            } else {
                add("")
            }
        }
    }

    val tangents = remember {
        mutableStateListOf<String>().apply {
            if (initialEntry != null && initialEntry.tangents.isNotEmpty()) {
                addAll(initialEntry.tangents)
            } else {
                add("")
            }
        }
    }

    val sources = remember {
        mutableStateListOf<String>().apply {
            if (initialEntry != null && initialEntry.sources.isNotEmpty()) {
                addAll(initialEntry.sources)
            } else {
                add("")
            }
        }
    }

    val categoryColorHex = when (category) {
        "Brain & Mind" -> "#6366F1"
        "Sports & Scandals" -> "#0284C7"
        "History" -> "#F59E0B"
        "Mysteries" -> "#EC4899"
        "Science" -> "#10B981"
        "Technology" -> "#06B6D4"
        else -> "#4F46E5"
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(onClick = onCancel, modifier = Modifier.testTag("form_cancel_button")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Cancel"
                        )
                    }
                    Text(
                        text = if (initialEntry == null || initialEntry.id.isBlank()) "Add New Search" else "Edit Exploration",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            onSave(
                                title,
                                category,
                                spark,
                                overview,
                                keyTakeaways.toList(),
                                tangents.toList(),
                                sources.toList(),
                                rabbitHoleDepth.toInt().coerceIn(1, 4),
                                categoryColorHex,
                                selectedCover
                            )
                        }
                    },
                    enabled = title.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CurioPrimary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("form_save_button")
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (initialEntry != null) "Update Research" else "Publish Research")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Title Input
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "What did you search or explore?",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = {
                        Text("e.g. What happens to the brain when a person is dying")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_title"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = CurioPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                // Simple English suggestion helper
                if (title.isNotBlank()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                onGenerateAngles(title) { genSpark, genOverview, genTakeaways, genTangents ->
                                    if (spark.isBlank()) spark = genSpark
                                    if (overview.isBlank()) overview = genOverview
                                    keyTakeaways.clear()
                                    keyTakeaways.addAll(genTakeaways)
                                    tangents.clear()
                                    tangents.addAll(genTangents)

                                    val lower = title.lowercase()
                                    if (lower.contains("brain") || lower.contains("dying")) {
                                        category = "Brain & Mind"
                                        selectedCover = "hero_brain_dying"
                                    } else if (lower.contains("city") || lower.contains("football") || lower.contains("downfall")) {
                                        category = "Sports & Scandals"
                                        selectedCover = "hero_mancity_investigation"
                                    }
                                }
                            },
                            enabled = !isGenerating,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CurioPrimary.copy(alpha = 0.12f),
                                contentColor = CurioPrimary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("ai_assist_button")
                        ) {
                            if (isGenerating) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = CurioPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Thinking...")
                            } else {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Suggest Questions & Facts")
                            }
                        }
                    }
                }
            }

            // Category Picker
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Category",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "Brain & Mind",
                        "Sports & Scandals",
                        "History",
                        "Mysteries",
                        "Science",
                        "Technology"
                    ).forEach { catName ->
                        val isSelected = category == catName
                        ElevatedFilterChip(
                            selected = isSelected,
                            onClick = { category = catName },
                            label = { Text(catName) },
                            colors = FilterChipDefaults.elevatedFilterChipColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                selectedContainerColor = CurioPrimary.copy(alpha = 0.15f),
                                selectedLabelColor = CurioPrimary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("chip_category_$catName")
                        )
                    }
                }
            }

            // Why I searched this
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = CurioGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Why did you search this?",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
                OutlinedTextField(
                    value = spark,
                    onValueChange = { spark = it },
                    placeholder = {
                        Text("What video, conversation, news headline, or question made you curious?")
                    },
                    minLines = 2,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_spark"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = CurioPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
            }

            // What I found
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "What did you find?",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                OutlinedTextField(
                    value = overview,
                    onValueChange = { overview = it },
                    placeholder = {
                        Text("Write what you learned in plain, simple English...")
                    },
                    minLines = 4,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_overview"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = CurioPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
            }

            // Key Facts list
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Key Facts Discovered",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    IconButton(
                        onClick = { keyTakeaways.add("") },
                        modifier = Modifier.testTag("add_takeaway_button")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Fact", tint = CurioPrimary)
                    }
                }

                keyTakeaways.forEachIndexed { index, takeaway ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = takeaway,
                            onValueChange = { keyTakeaways[index] = it },
                            placeholder = { Text("Fact #${index + 1}") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_takeaway_$index"),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedBorderColor = CurioPrimary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )
                        if (keyTakeaways.size > 1) {
                            IconButton(onClick = { keyTakeaways.removeAt(index) }) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "Remove",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Follow-up questions
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Questions to Explore Next",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    IconButton(onClick = { tangents.add("") }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Question", tint = CurioGold)
                    }
                }

                tangents.forEachIndexed { index, tangent ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = tangent,
                            onValueChange = { tangents[index] = it },
                            placeholder = { Text("Follow-up question #${index + 1}...") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedBorderColor = CurioPrimary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )
                        if (tangents.size > 1) {
                            IconButton(onClick = { tangents.removeAt(index) }) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "Remove",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Where I read this
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Where Did You Read or Watch This?",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    IconButton(onClick = { sources.add("") }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Source", tint = CurioPrimary)
                    }
                }

                sources.forEachIndexed { index, source ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = source,
                            onValueChange = { sources[index] = it },
                            placeholder = { Text("Website, YouTube channel, book, or article...") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedBorderColor = CurioPrimary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )
                        if (sources.size > 1) {
                            IconButton(onClick = { sources.removeAt(index) }) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "Remove",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Depth
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "How Deep Was This Rabbit Hole?",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    DepthBadge(depth = rabbitHoleDepth.toInt())
                }

                Slider(
                    value = rabbitHoleDepth,
                    onValueChange = { rabbitHoleDepth = it },
                    valueRange = 1f..4f,
                    steps = 2,
                    colors = SliderDefaults.colors(
                        thumbColor = CurioPrimary,
                        activeTrackColor = CurioPrimary
                    ),
                    modifier = Modifier.testTag("depth_slider")
                )
            }

            // Cover Image Selection
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Cover Picture",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp, 60.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (selectedCover == null) CurioPrimary.copy(alpha = 0.15f)
                                else MaterialTheme.colorScheme.surface
                            )
                            .border(
                                width = 1.dp,
                                color = if (selectedCover == null) CurioPrimary else MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedCover = null },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No Picture", style = MaterialTheme.typography.labelSmall)
                    }

                    CurioImageHelper.availableCovers.forEach { cover ->
                        val isSelected = selectedCover == cover.key
                        Box(
                            modifier = Modifier
                                .size(100.dp, 60.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    width = 2.dp,
                                    color = if (isSelected) CurioPrimary else Color.Transparent,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedCover = cover.key }
                        ) {
                            Image(
                                painter = painterResource(id = cover.resId),
                                contentDescription = cover.title,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }
        }
    }
}
