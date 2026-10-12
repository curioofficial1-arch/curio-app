package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.CurioItem
import com.example.data.sample.InitialCuriosities
import com.example.data.util.OperationType
import com.example.data.util.handleFirestoreError
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirestoreCurioRepository(
    private val db: FirebaseFirestore
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val publicResearchesRef = db.collection("researches")

    /**
     * Observe all public research stories published on Curio.
     * Accessible to anyone reading the publication like news.
     */
    fun observePublishedResearches(): Flow<List<CurioItem>> {
        val path = "researches"
        return publicResearchesRef
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(CurioItem::class.java)
                    .sortedByDescending { it.createdAt?.seconds ?: Long.MAX_VALUE }
            }
            .catch { e ->
                if (e is Exception) handleFirestoreError(e, OperationType.LIST, path)
                throw e
            }
    }

    /**
     * Publish a new research story to Curio for the world to read.
     */
    suspend fun publishResearch(item: CurioItem, authorEmail: String?, authorName: String?) {
        val docRef = if (item.id.isBlank()) publicResearchesRef.document() else publicResearchesRef.document(item.id)
        val path = docRef.path
        val isNew = item.id.isBlank()

        try {
            val payload = hashMapOf<String, Any?>(
                "title" to item.title,
                "authorName" to (authorName ?: "Curio Editorial"),
                "authorEmail" to (authorEmail ?: "curioofficial1@gmail.com"),
                "category" to item.category,
                "spark" to item.spark,
                "overview" to item.overview,
                "keyTakeaways" to item.keyTakeaways,
                "tangents" to item.tangents,
                "sources" to item.sources,
                "rabbitHoleDepth" to item.rabbitHoleDepth,
                "status" to "Published",
                "coverDrawable" to item.coverDrawable,
                "colorHex" to item.colorHex,
                "readTimeMinutes" to item.readTimeMinutes,
                "likesCount" to item.likesCount,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            if (isNew) {
                payload["createdAt"] = FieldValue.serverTimestamp()
            }

            docRef.set(payload, SetOptions.merge()).await()
        } catch (e: Exception) {
            handleFirestoreError(e, if (isNew) OperationType.CREATE else OperationType.UPDATE, path)
            throw e
        }
    }

    suspend fun deleteResearch(id: String) {
        if (id.isBlank()) return
        val docRef = publicResearchesRef.document(id)
        try {
            docRef.delete().await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            throw e
        }
    }

    suspend fun likeResearch(id: String) {
        if (id.isBlank()) return
        val docRef = publicResearchesRef.document(id)
        try {
            docRef.update("likesCount", FieldValue.increment(1)).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
        }
    }

    /**
     * Seeds initial public research stories into the live publication if empty.
     * Only attempted when an authenticated author is present, preventing unauthenticated write errors.
     */
    suspend fun seedPublicIfEmpty(currentUser: FirebaseUser?) {
        if (currentUser == null) return
        try {
            val snapshot = publicResearchesRef.limit(1).get().await()
            if (snapshot.isEmpty) {
                for (starter in InitialCuriosities.sampleList) {
                    val docRef = publicResearchesRef.document()
                    val payload = hashMapOf<String, Any?>(
                        "title" to starter.title,
                        "authorName" to (currentUser.displayName ?: "Curio Editorial"),
                        "authorEmail" to (currentUser.email ?: "curioofficial1@gmail.com"),
                        "category" to starter.category,
                        "spark" to starter.spark,
                        "overview" to starter.overview,
                        "keyTakeaways" to starter.keyTakeaways,
                        "tangents" to starter.tangents,
                        "sources" to starter.sources,
                        "rabbitHoleDepth" to starter.rabbitHoleDepth,
                        "status" to "Published",
                        "coverDrawable" to starter.coverDrawable,
                        "colorHex" to starter.colorHex,
                        "readTimeMinutes" to starter.readTimeMinutes,
                        "likesCount" to (12L + starter.rabbitHoleDepth * 5L),
                        "createdAt" to FieldValue.serverTimestamp(),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                    docRef.set(payload).await()
                }
            }
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, publicResearchesRef.path)
        }
    }
}
