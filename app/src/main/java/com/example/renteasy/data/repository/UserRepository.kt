package com.example.renteasy.data.repository

import com.example.renteasy.data.model.User
import com.example.renteasy.utils.Constants
import com.example.renteasy.utils.RentEasyLog
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val firestore: FirebaseFirestore? = try { FirebaseFirestore.getInstance() } catch (e: Throwable) { null },
    private val demoDataStore: DemoDataStore = DemoDataStore
) {
    private val tag = "UserRepository"

    fun getUser(uid: String): Flow<User?> {
        val firestoreFlow: Flow<User?> = if (firestore != null) {
            callbackFlow {
                try {
                    val listener = firestore.collection(Constants.COLLECTION_USERS)
                        .document(uid)
                        .addSnapshotListener { snapshot, error ->
                            if (error != null) {
                                RentEasyLog.w(tag, "Firestore getUser error: ${error.localizedMessage}")
                                trySend(null)
                                return@addSnapshotListener
                            }
                            val user = snapshot?.toObject(User::class.java)?.copy(uid = snapshot.id)
                            trySend(user)
                        }
                    awaitClose { listener.remove() }
                } catch (e: Exception) {
                    trySend(null)
                    awaitClose { }
                }
            }
        } else {
            flowOf(null)
        }

        return combine(firestoreFlow, demoDataStore.users) { firestoreUser, demoUsers ->
            firestoreUser ?: demoUsers.find { it.uid == uid }
        }.distinctUntilChanged()
    }

    fun getAllUsers(): Flow<List<User>> {
        val firestoreFlow: Flow<List<User>> = if (firestore != null) {
            callbackFlow {
                try {
                    val listener = firestore.collection(Constants.COLLECTION_USERS)
                        .addSnapshotListener { snapshot, error ->
                            if (error != null) {
                                RentEasyLog.w(tag, "Firestore getAllUsers error: ${error.localizedMessage}")
                                trySend(emptyList())
                                return@addSnapshotListener
                            }
                            val list = snapshot?.documents?.mapNotNull { doc ->
                                doc.toObject(User::class.java)?.copy(uid = doc.id)
                            } ?: emptyList()
                            trySend(list)
                        }
                    awaitClose { listener.remove() }
                } catch (e: Exception) {
                    trySend(emptyList())
                    awaitClose { }
                }
            }
        } else {
            flowOf(emptyList())
        }

        return combine(firestoreFlow, demoDataStore.users) { firestoreList, demoList ->
            (firestoreList + demoList).distinctBy { it.uid }
        }
    }

    suspend fun updateUser(user: User): Result<Unit> {
        return try {
            demoDataStore.saveUser(user)
            if (firestore != null) {
                try {
                    firestore.collection(Constants.COLLECTION_USERS)
                        .document(user.uid)
                        .set(user)
                        .await()
                } catch (e: Exception) {
                    RentEasyLog.w(tag, "Firestore updateUser failed: ${e.localizedMessage}")
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
