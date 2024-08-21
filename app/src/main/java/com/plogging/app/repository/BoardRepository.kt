package com.plogging.app.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.plogging.app.data.BoardData
import com.google.firebase.firestore.Query
import com.plogging.app.common.Const.BOARD
import kotlinx.coroutines.tasks.await
class BoardRepository (private val db: FirebaseFirestore) {

    suspend fun getGuestbookEntries(): List<BoardData> {
        return try {
            val snapshots = db.collection(BOARD)
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .await()

            snapshots.map { doc ->
                BoardData(
                    uid = doc.getString("uid") ?: "",
                    message = doc.getString("message") ?: "",
                    timestamp = doc.getLong("timestamp") ?: 0L
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addGuestbookEntry(entry: BoardData) {
        val newEntry = hashMapOf(
            "uid" to entry.uid,
            "message" to entry.message,
            "timestamp" to entry.timestamp
        )
        db.collection(BOARD).add(newEntry).await()
    }
}