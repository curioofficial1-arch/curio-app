package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Article
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CurioItem
import com.example.ui.components.CurioCard
import com.example.ui.components.CurioTopBar
import com.example.ui.theme.CatBrain
import com.example.ui.theme.CatHistory
import com.example.ui.theme.CatMysteries
import com.example.ui.theme.CatScience
import com.example.ui.theme.CatSports
import com.example.ui.theme.CatTech
import com.example.ui.theme.CurioGold
import com.example.ui.theme.CurioPrimary

val NormalCurioCategories = listOf(
    "All" to CurioPrimary,
    "Favorites" to CurioGold,
    "Brain & Mind" to CatBrain,
    "Sports & Scandals" to CatSports,
    "History" to CatHistory,
    "Mysteries" to CatMysteries,
    "Science" to CatScience,
    "Technology" to CatTech
)

@Composable
fun CurioFeedScreen(
    entries: List<CurioItem>,
    searchQuery: String,
    selectedCategory: String,
    selectedDepth: Int?,
    onSearchChange: (String) -> Unit,
    onCategorySelect: (String) -> Unit,
    onDepthSelect: (Int) -> Unit,
    onEntryClick: (CurioItem) -> Unit,
    onToggleBookmark: (CurioItem) -> Unit,
    onLike: (CurioItem) -> Unit,
    onPublishClick: () -> Unit,
    onSurpriseMeClick: () -> Unit,
    onOpenDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CurioTopBar(
                searchQuery = searchQuery,
                onSearchQueryChange = onSearchChange,
                onSurpriseMeClick = onSurpriseMeClick,
                onOpenDrawer = onOpenDrawer
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onPublishClick,
                containerColor = CurioPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .testTag("add_curio_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Publish Research",
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Publish",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Category Filter Scrollable Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NormalCurioCategories.forEach { (catName, catColor) ->
                    val isSelected = selectedCategory == catName
                    ElevatedFilterChip(
                        selected = isSelected,
                        onClick = { onCategorySelect(catName) },
                        label = {
                            Text(
                                text = catName,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            )
                        },
                        colors = FilterChipDefaults.elevatedFilterChipColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            selectedContainerColor = catColor.copy(alpha = 0.15f),
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            selectedLabelColor = catColor
                        ),
                        border = if (isSelected) FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = true,
                            borderColor = catColor
                        ) else null,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("filter_chip_$catName")
                    )
                }
            }

            // Depth Filter Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Depth:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                )

                listOf(
                    1 to "Quick Fact",
                    2 to "Deep Dive",
                    3 to "Rabbit Hole",
                    4 to "Obsession"
                ).forEach { (depthLevel, depthTitle) ->
                    val isDepthSelected = selectedDepth == depthLevel
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isDepthSelected) CurioPrimary.copy(alpha = 0.15f)
                                else MaterialTheme.colorScheme.surface
                            )
                            .border(
                                width = 1.dp,
                                color = if (isDepthSelected) CurioPrimary else MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { onDepthSelect(depthLevel) }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = depthTitle,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isDepthSelected) CurioPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = if (isDepthSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    }
                }
            }

            // Stories List or Empty State
            if (entries.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(24.dp)),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(16f / 9f)
                                    .clip(RoundedCornerShape(16.dp))
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.hero_curio_cabinet),
                                    contentDescription = "Curio Research Publication",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            Text(
                                text = if (searchQuery.isNotBlank()) "No Stories Found" else "No Published Research Yet",
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = if (searchQuery.isNotBlank())
                                    "No research posts match '$searchQuery'. Try checking spelling or clear your search."
                                else
                                    "Curio is a live publication where people read daily deep-dive investigations. Publish your first research post!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )

                            Button(
                                onClick = onPublishClick,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CurioPrimary,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.testTag("empty_state_add_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Publish First Story")
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("curio_feed_list"),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(entries, key = { it.id }) { curio ->
                        CurioCard(
                            entry = curio,
                            onClick = { onEntryClick(curio) },
                            onToggleBookmark = { onToggleBookmark(curio) },
                            onLike = { onLike(curio) }
                        )
                    }
                }
            }
        }
    }
}
