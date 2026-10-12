package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Newspaper
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.auth.CurioAuthManager
import com.example.data.sample.InspirationPrompt
import com.example.ui.components.CurioDrawerContent
import com.example.ui.screens.CurioAddEditScreen
import com.example.ui.screens.CurioAuthScreen
import com.example.ui.screens.CurioDetailScreen
import com.example.ui.screens.CurioFeedScreen
import com.example.ui.screens.CurioInspirationsScreen
import com.example.ui.screens.CurioSavedScreen
import com.example.ui.theme.CurioPrimary
import com.example.ui.theme.CurioTheme
import com.example.ui.viewmodel.CurioNavTab
import com.example.ui.viewmodel.CurioViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: CurioViewModel = viewModel()
            val isDarkTheme by viewModel.isDarkTheme.collectAsState()

            CurioTheme(darkTheme = isDarkTheme) {
                CurioApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurioApp(viewModel: CurioViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }

    val currentUser by viewModel.currentUser.collectAsState()
    val entries by viewModel.curioEntries.collectAsState()
    val savedEntries by viewModel.savedEntries.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedDepth by viewModel.selectedDepthFilter.collectAsState()
    val activeDetail by viewModel.activeDetailEntry.collectAsState()
    val isFormOpen by viewModel.isFormOpen.collectAsState()
    val editingEntry by viewModel.editingEntry.collectAsState()
    val isGeneratingAngles by viewModel.isGeneratingAngles.collectAsState()
    val isAuthModalOpen by viewModel.isAuthModalOpen.collectAsState()
    val currentTab by viewModel.currentTab.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    // Listen to Firebase Auth state
    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            val user = auth.currentUser
            if (user != null) {
                viewModel.onUserSignedIn(user)
            } else {
                viewModel.onUserSignedOut()
            }
        }
        Firebase.auth.addAuthStateListener(listener)
        onDispose {
            Firebase.auth.removeAuthStateListener(listener)
        }
    }

    // Notification permission & welcome trigger for Android 13+
    val notificationPermissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            com.example.util.CurioNotificationHelper.checkAndSendWelcomeNotification(context)
        }
    }

    LaunchedEffect(Unit) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = androidx.core.content.ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            if (!hasPermission) {
                notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            } else {
                com.example.util.CurioNotificationHelper.checkAndSendWelcomeNotification(context)
            }
        } else {
            com.example.util.CurioNotificationHelper.checkAndSendWelcomeNotification(context)
        }
    }

    // Auto check if already signed in
    LaunchedEffect(Unit) {
        if (currentUser == null) {
            CurioAuthManager.attemptAutoSignIn(
                context = context,
                credentialManager = credentialManager,
                onAuthSuccess = {
                    Firebase.auth.currentUser?.let { viewModel.onUserSignedIn(it) }
                },
                onUnauthenticated = { },
                scope = scope
            )
        }
    }

    BackHandler(enabled = drawerState.isOpen) {
        scope.launch { drawerState.close() }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            CurioDrawerContent(
                userEmail = currentUser?.email,
                isDarkTheme = isDarkTheme,
                onToggleTheme = { viewModel.toggleTheme(it) },
                onPublishClick = { viewModel.requestPublish() },
                onSignInClick = { viewModel.isAuthModalOpen.value = true },
                onSignOut = {
                    CurioAuthManager.signOut(
                        credentialManager = credentialManager,
                        onSignOutComplete = { viewModel.onUserSignedOut() },
                        scope = scope
                    )
                },
                onCloseDrawer = { scope.launch { drawerState.close() } }
            )
        }
    ) {
        // Active Sub-screen routing
        if (activeDetail != null) {
            val isAuthor = currentUser != null && (
                currentUser?.email == activeDetail!!.authorEmail ||
                currentUser?.email?.contains("curio") == true ||
                currentUser?.email == "curioofficial1@gmail.com"
            )

            CurioDetailScreen(
                entry = activeDetail!!,
                isAuthor = isAuthor,
                onBack = { viewModel.closeDetail() },
                onEdit = { viewModel.openEditForm(activeDetail!!) },
                onDelete = { viewModel.deleteStory(activeDetail!!) },
                onToggleBookmark = { viewModel.toggleBookmark(activeDetail!!.id) },
                onLike = { viewModel.likeStory(activeDetail!!.id) },
                onStartTangent = { tangentQuestion ->
                    viewModel.closeDetail()
                    viewModel.requestPublish(
                        InspirationPrompt(
                            topic = tangentQuestion,
                            category = activeDetail!!.category,
                            spark = "Follow-up inquiry sparked by '${activeDetail!!.title}'",
                            suggestedColor = activeDetail!!.colorHex
                        )
                    )
                }
            )
        } else if (isFormOpen) {
            CurioAddEditScreen(
                initialEntry = editingEntry,
                onSave = { title, category, spark, overview, keyTakeaways, tangents, sources, depth, colorHex, cover ->
                    viewModel.publishResearch(
                        id = editingEntry?.id ?: "",
                        title = title,
                        category = category,
                        spark = spark,
                        overview = overview,
                        keyTakeaways = keyTakeaways,
                        tangents = tangents,
                        sources = sources,
                        rabbitHoleDepth = depth,
                        colorHex = colorHex,
                        coverDrawable = cover
                    )
                },
                onCancel = { viewModel.closeForm() },
                onGenerateAngles = { topic, callback ->
                    viewModel.generateIntelligentAngles(topic, callback)
                },
                isGenerating = isGeneratingAngles
            )
        } else {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                bottomBar = {
                    NavigationBar(
                        windowInsets = WindowInsets.navigationBars,
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 6.dp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                            .testTag("curio_bottom_nav")
                    ) {
                        // Professional M3 Bottom Navigation Item 1: Stories / News Feed
                        NavigationBarItem(
                            selected = currentTab == CurioNavTab.STORIES,
                            onClick = { viewModel.setTab(CurioNavTab.STORIES) },
                            icon = {
                                Icon(
                                    imageVector = if (currentTab == CurioNavTab.STORIES) Icons.Filled.Newspaper else Icons.Outlined.Newspaper,
                                    contentDescription = "Stories"
                                )
                            },
                            label = { Text("Stories") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = CurioPrimary,
                                indicatorColor = CurioPrimary.copy(alpha = 0.12f),
                                selectedTextColor = CurioPrimary
                            ),
                            modifier = Modifier.testTag("nav_item_stories")
                        )

                        // Professional M3 Bottom Navigation Item 2: Topics Radar
                        NavigationBarItem(
                            selected = currentTab == CurioNavTab.TOPICS,
                            onClick = { viewModel.setTab(CurioNavTab.TOPICS) },
                            icon = {
                                Icon(
                                    imageVector = if (currentTab == CurioNavTab.TOPICS) Icons.Filled.Explore else Icons.Outlined.Explore,
                                    contentDescription = "Topics"
                                )
                            },
                            label = { Text("Topics") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = CurioPrimary,
                                indicatorColor = CurioPrimary.copy(alpha = 0.12f),
                                selectedTextColor = CurioPrimary
                            ),
                            modifier = Modifier.testTag("nav_item_topics")
                        )

                        // Professional M3 Bottom Navigation Item 3: Saved Bookmarks
                        NavigationBarItem(
                            selected = currentTab == CurioNavTab.SAVED,
                            onClick = { viewModel.setTab(CurioNavTab.SAVED) },
                            icon = {
                                Icon(
                                    imageVector = if (currentTab == CurioNavTab.SAVED) Icons.Filled.Bookmark else Icons.Outlined.Bookmark,
                                    contentDescription = "Saved"
                                )
                            },
                            label = { Text("Saved") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = CurioPrimary,
                                indicatorColor = CurioPrimary.copy(alpha = 0.12f),
                                selectedTextColor = CurioPrimary
                            ),
                            modifier = Modifier.testTag("nav_item_saved")
                        )
                    }
                }
            ) { innerPadding ->
                AnimatedContent(
                    targetState = currentTab,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    label = "TabTransition"
                ) { tab ->
                    when (tab) {
                        CurioNavTab.STORIES -> {
                            CurioFeedScreen(
                                entries = entries,
                                searchQuery = searchQuery,
                                selectedCategory = selectedCategory,
                                selectedDepth = selectedDepth,
                                onSearchChange = { viewModel.onSearchQueryChanged(it) },
                                onCategorySelect = { viewModel.selectCategory(it) },
                                onDepthSelect = { viewModel.toggleDepthFilter(it) },
                                onEntryClick = { viewModel.openDetail(it) },
                                onToggleBookmark = { viewModel.toggleBookmark(it.id) },
                                onLike = { viewModel.likeStory(it.id) },
                                onPublishClick = { viewModel.requestPublish() },
                                onSurpriseMeClick = {
                                    if (entries.isNotEmpty()) {
                                        val randomEntry = entries[Random.nextInt(entries.size)]
                                        viewModel.openDetail(randomEntry)
                                    }
                                },
                                onOpenDrawer = { scope.launch { drawerState.open() } }
                            )
                        }
                        CurioNavTab.TOPICS -> {
                            CurioInspirationsScreen(
                                onStartPrompt = { prompt ->
                                    viewModel.requestPublish(prompt)
                                },
                                onOpenDrawer = { scope.launch { drawerState.open() } }
                            )
                        }
                        CurioNavTab.SAVED -> {
                            CurioSavedScreen(
                                savedEntries = savedEntries,
                                onEntryClick = { viewModel.openDetail(it) },
                                onToggleBookmark = { viewModel.toggleBookmark(it.id) },
                                onLike = { viewModel.likeStory(it.id) },
                                onOpenDrawer = { scope.launch { drawerState.open() } }
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal Sheet for Publisher Sign-In
    if (isAuthModalOpen) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.closeAuthModal() },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            CurioAuthScreen(
                onAuthSuccess = {
                    Firebase.auth.currentUser?.let {
                        viewModel.onUserSignedIn(it)
                        viewModel.openPublishForm()
                    }
                },
                onContinueAsGuest = {
                    viewModel.closeAuthModal()
                },
                onDismiss = {
                    viewModel.closeAuthModal()
                }
            )
        }
    }
}
