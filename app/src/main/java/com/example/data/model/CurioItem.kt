package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class CurioItem(
    @DocumentId val id: String = "",
    val title: String = "",
    val authorName: String = "Curio Editorial",
    val authorEmail: String = "curioofficial1@gmail.com",
    val category: String = "General",
    val spark: String = "", // Why I researched this
    val overview: String = "", // The story and what I found
    val keyTakeaways: List<String> = emptyList(), // Main facts discovered
    val tangents: List<String> = emptyList(), // Follow-up questions
    val sources: List<String> = emptyList(), // Where I read or verified this
    val rabbitHoleDepth: Int = 2,
    val status: String = "Published",
    val isFavorite: Boolean = false,
    val coverDrawable: String? = null,
    val colorHex: String = "#4F46E5",
    val readTimeMinutes: Int = 4,
    val likesCount: Long = 0L,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)
