package com.example.data.util

import android.util.Log

enum class OperationType {
    GET,
    LIST,
    CREATE,
    UPDATE,
    DELETE
}

fun handleFirestoreError(e: Exception, op: OperationType, path: String) {
    Log.e("FirestoreError", "Firestore operation failed [op=${op.name}, path=$path]: ${e.message}", e)
}
