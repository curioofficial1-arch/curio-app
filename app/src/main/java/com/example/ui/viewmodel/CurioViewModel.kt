package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.CurioItem
import com.example.data.repository.FirestoreCurioRepository
import com.example.data.sample.InitialCuriosities
import com.example.data.sample.InspirationPrompt
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class CurioNavTab {
    STORIES,  // Public News / Research publication feed
    TOPICS,   // Topic categories & radar prompts
    SAVED     // Reader's saved / bookmarked research stories
}

class CurioViewModel(application: Application) : AndroidViewModel(application) {
    private val firestoreRepo = FirestoreCurioRepository(application)

    // Current Author / Publisher User (optional for reading, needed to publish)
    val currentUser = MutableStateFlow<FirebaseUser?>(Firebase.auth.currentUser)

    // User preference for theme (Default is FALSE = white/light theme)
    val isDarkTheme = MutableStateFlow(false)

    val currentTab = MutableStateFlow(CurioNavTab.STORIES)
    val searchQuery = MutableStateFlow("")
    val selectedCategory = MutableStateFlow("All")
    val selectedDepthFilter = MutableStateFlow<Int?>(null)

    // Active detail article
    val activeDetailEntry = MutableStateFlow<CurioItem?>(null)

    // Author Publisher Form state
    val isFormOpen = MutableStateFlow(false)
    val editingEntry = MutableStateFlow<CurioItem?>(null)

    // Publisher Sign-In Modal state
    val isAuthModalOpen = MutableStateFlow(false)

    // Reader Bookmarks
    val bookmarkedIds = MutableStateFlow<Set<String>>(emptySet())

    // AI angle assistant state
    val isGeneratingAngles = MutableStateFlow(false)

    // Raw published stories from Firestore, defaulting to initial editorial articles
    private val publicStories = MutableStateFlow<List<CurioItem>>(InitialCuriosities.sampleList)

    init {
        // Automatically listen to public publication feed
        viewModelScope.launch {
            firestoreRepo.seedPublicIfEmpty(currentUser.value)
            firestoreRepo.observePublishedResearches()
                .catch { emit(emptyList()) }
                .collect { items ->
                    if (items.isNotEmpty()) {
                        publicStories.value = items
                    } else {
                        publicStories.value = InitialCuriosities.sampleList
                    }
                    // Keep open story synced
                    if (activeDetailEntry.value != null) {
                        val current = (if (items.isNotEmpty()) items else InitialCuriosities.sampleList).find { it.id == activeDetailEntry.value?.id }
                        if (current != null) {
                            activeDetailEntry.value = current
                        }
                    }
                }
        }
    }

    fun onUserSignedIn(user: FirebaseUser) {
        currentUser.value = user
        isAuthModalOpen.value = false
        // Seed remote collection if empty now that author is authenticated
        viewModelScope.launch {
            firestoreRepo.seedPublicIfEmpty(user)
        }
    }

    fun onUserSignedOut() {
        currentUser.value = null
    }

    // Filtered public feed of research stories
    val curioEntries: StateFlow<List<CurioItem>> = combine(
        publicStories,
        searchQuery,
        selectedCategory,
        selectedDepthFilter,
        bookmarkedIds
    ) { all, query, category, depth, saved ->
        all.map { item ->
            item.copy(isFavorite = saved.contains(item.id))
        }.filter { entry ->
            val matchesQuery = if (query.isBlank()) true else {
                entry.title.contains(query, ignoreCase = true) ||
                entry.overview.contains(query, ignoreCase = true) ||
                entry.spark.contains(query, ignoreCase = true) ||
                entry.category.contains(query, ignoreCase = true) ||
                entry.keyTakeaways.any { it.contains(query, ignoreCase = true) } ||
                entry.tangents.any { it.contains(query, ignoreCase = true) }
            }

            val matchesCategory = when (category) {
                "All" -> true
                "Favorites" -> saved.contains(entry.id)
                else -> entry.category.equals(category, ignoreCase = true)
            }

            val matchesDepth = depth == null || entry.rabbitHoleDepth == depth

            matchesQuery && matchesCategory && matchesDepth
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Saved/Bookmarked stories
    val savedEntries: StateFlow<List<CurioItem>> = combine(
        publicStories,
        bookmarkedIds
    ) { all, saved ->
        all.filter { saved.contains(it.id) }.map { it.copy(isFavorite = true) }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun toggleTheme(dark: Boolean) {
        isDarkTheme.value = dark
    }

    fun setTab(tab: CurioNavTab) {
        currentTab.value = tab
    }

    fun onSearchQueryChanged(query: String) {
        searchQuery.value = query
    }

    fun selectCategory(cat: String) {
        selectedCategory.value = cat
    }

    fun toggleDepthFilter(depth: Int) {
        selectedDepthFilter.value = if (selectedDepthFilter.value == depth) null else depth
    }

    fun openDetail(entry: CurioItem) {
        activeDetailEntry.value = entry
    }

    fun closeDetail() {
        activeDetailEntry.value = null
    }

    fun toggleBookmark(storyId: String) {
        val current = bookmarkedIds.value.toMutableSet()
        if (current.contains(storyId)) {
            current.remove(storyId)
        } else {
            current.add(storyId)
        }
        bookmarkedIds.value = current
    }

    fun likeStory(storyId: String) {
        publicStories.value = publicStories.value.map { item ->
            if (item.id == storyId) item.copy(likesCount = item.likesCount + 1) else item
        }
        if (activeDetailEntry.value?.id == storyId) {
            activeDetailEntry.value = activeDetailEntry.value?.let { it.copy(likesCount = it.likesCount + 1) }
        }
        viewModelScope.launch {
            firestoreRepo.likeResearch(storyId)
        }
    }

    fun requestPublish(initialPrompt: InspirationPrompt? = null) {
        if (currentUser.value == null) {
            // Prompt author to sign in
            isAuthModalOpen.value = true
        } else {
            openPublishForm(initialPrompt)
        }
    }

    fun openPublishForm(initialPrompt: InspirationPrompt? = null) {
        if (initialPrompt != null) {
            editingEntry.value = CurioItem(
                title = initialPrompt.topic,
                category = initialPrompt.category,
                spark = initialPrompt.spark,
                colorHex = initialPrompt.suggestedColor
            )
        } else {
            editingEntry.value = null
        }
        isFormOpen.value = true
    }

    fun openEditForm(entry: CurioItem) {
        editingEntry.value = entry
        isFormOpen.value = true
    }

    fun closeForm() {
        isFormOpen.value = false
        editingEntry.value = null
    }

    fun closeAuthModal() {
        isAuthModalOpen.value = false
    }

    fun deleteStory(entry: CurioItem) {
        viewModelScope.launch {
            firestoreRepo.deleteResearch(entry.id)
            if (activeDetailEntry.value?.id == entry.id) {
                activeDetailEntry.value = null
            }
        }
    }

    fun publishResearch(
        id: String,
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
    ) {
        val user = currentUser.value
        val authorEmail = user?.email ?: "curioofficial1@gmail.com"
        val authorName = user?.displayName ?: "Curio"

        viewModelScope.launch {
            val wordsCount = (overview.split("\\s+".toRegex()).size + keyTakeaways.sumOf { it.split("\\s+".toRegex()).size })
            val estimatedReadTime = maxOf(1, wordsCount / 120)

            val item = CurioItem(
                id = id,
                authorName = authorName,
                authorEmail = authorEmail,
                title = title.trim(),
                category = category.trim(),
                spark = spark.trim(),
                overview = overview.trim(),
                keyTakeaways = keyTakeaways.map { it.trim() }.filter { it.isNotBlank() },
                tangents = tangents.map { it.trim() }.filter { it.isNotBlank() },
                sources = sources.map { it.trim() }.filter { it.isNotBlank() },
                rabbitHoleDepth = rabbitHoleDepth,
                colorHex = colorHex,
                coverDrawable = coverDrawable,
                readTimeMinutes = estimatedReadTime,
                likesCount = editingEntry.value?.likesCount ?: 0L
            )

            firestoreRepo.publishResearch(item, authorEmail, authorName)
            com.example.util.CurioNotificationHelper.notifyNewPost(
                context = getApplication(),
                postId = item.id.ifBlank { System.currentTimeMillis().toString() },
                title = item.title,
                overview = item.overview,
                category = item.category
            )
            isFormOpen.value = false
            editingEntry.value = null
        }
    }

    fun generateIntelligentAngles(topicQuery: String, onGenerated: (spark: String, overview: String, takeaways: List<String>, tangents: List<String>) -> Unit) {
        viewModelScope.launch {
            isGeneratingAngles.value = true
            val cleanQuery = topicQuery.trim()
            val lower = cleanQuery.lowercase()

            val generated = when {
                lower.contains("brain") || lower.contains("dying") || lower.contains("death") -> {
                    Pair(
                        "Scientists discovered that when the heart stops, the brain experiences a sudden surge of organized brainwaves similar to deep dreaming and remembering old memories. Hearing is the last sense to go.",
                        listOf(
                            "A burst of memory brainwaves surges for up to a few minutes after clinical death.",
                            "Hearing remains active longer than any other sense, still registering voices.",
                            "Natural calming chemicals flood the body, removing pain and inducing peace."
                        )
                    )
                }
                lower.contains("city") || lower.contains("football") || lower.contains("downfall") || lower.contains("scandal") -> {
                    Pair(
                        "The investigation into Manchester City was triggered by Portuguese hacker Rui Pinto, who leaked private emails proving secret owner payments and hidden offshore manager contracts.",
                        listOf(
                            "Over 70 million leaked Football Leaks documents sparked police and league investigations.",
                            "Disguised sponsor money: Club owners covertly paid invoices declared under official sponsors.",
                            "The Premier League filed 115 historic charges with no expiration time limit."
                        )
                    )
                }
                lower.contains("universe") || lower.contains("alien") || lower.contains("space") || lower.contains("fermi") -> {
                    Pair(
                        "With billions of planets in our galaxy, where are all the extraterrestrial civilizations? Scientists believe either intelligent life is extremely rare, or civilizations deliberately stay quiet.",
                        listOf(
                            "There are billions of Earth-like planets in the Milky Way alone.",
                            "The Great Filter idea suggests species hit an evolutionary bottleneck that wipes them out.",
                            "Some believe quiet civilizations survive while loud civilizations risk getting noticed."
                        )
                    )
                }
                else -> {
                    Pair(
                        "Looking past surface explanations to find the most surprising and interesting answers about: '$cleanQuery'.",
                        listOf(
                            "Common belief vs reality: What most people get wrong about this topic.",
                            "The key turning point or discovery that changed how experts understand it.",
                            "The practical takeaway and why this matters for the future."
                        )
                    )
                }
            }

            val tangents = listOf(
                "What is the biggest unanswered question about '$cleanQuery'?",
                "Who was the first person to discover or prove this?",
                "What is the most surprising fact about this topic?"
            )

            onGenerated(
                "I was curious about '$cleanQuery' and wanted to understand what really happens.",
                generated.first,
                generated.second,
                tangents
            )
            isGeneratingAngles.value = false
        }
    }
}
