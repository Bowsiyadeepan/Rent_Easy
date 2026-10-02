package com.example.renteasy.data.repository

import com.example.renteasy.utils.Constants
import com.example.renteasy.utils.RentEasyLog
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class WishlistRepository(
    private val firestore: FirebaseFirestore? = try { FirebaseFirestore.getInstance() } catch (e: Throwable) { null },
    private val demoDataStore: DemoDataStore = DemoDataStore
) {
    private val tag = "WishlistRepository"

    fun getWishlistPropertyIds(tenantId: String): Flow<Set<String>> {
        val firestoreFlow: Flow<Set<String>> = if (firestore != null) {
            callbackFlow {
                try {
                    val listener = firestore.collection(Constants.COLLECTION_WISHLISTS)
                        .document(tenantId)
                        .addSnapshotListener { snapshot, error ->
                            if (error != null) {
                                RentEasyLog.w(tag, "Firestore getWishlist error: ${error.localizedMessage}")
                                trySend(emptySet())
                                return@addSnapshotListener
                            }
                            @Suppress("UNCHECKED_CAST")
                            val ids = (snapshot?.get("propertyIds") as? List<String>)?.toSet() ?: emptySet()
                            trySend(ids)
                        }
                    awaitClose { listener.remove() }
                } catch (e: Exception) {
                    trySend(emptySet())
                    awaitClose { }
                }
            }
        } else {
            flowOf(emptySet())
        }

        return combine(firestoreFlow, demoDataStore.wishlists) { firestoreSet, demoMap ->
            val demoSet = demoMap[tenantId] ?: emptySet()
            firestoreSet + demoSet
        }
    }

    suspend fun toggleWishlist(tenantId: String, propertyId: String): Result<Boolean> {
        val newState = demoDataStore.toggleWishlist(tenantId, propertyId)
        val allIds = demoDataStore.getWishlistForTenant(tenantId).toList()

        if (firestore != null) {
            try {
                firestore.collection(Constants.COLLECTION_WISHLISTS)
                    .document(tenantId)
                    .set(mapOf("propertyIds" to allIds))
                    .await()
            } catch (e: Exception) {
                RentEasyLog.w(tag, "Firestore toggleWishlist stored locally: ${e.localizedMessage}")
            }
        }

        return Result.success(newState)
    }

    fun isWishlisted(tenantId: String, propertyId: String): Flow<Boolean> {
        return getWishlistPropertyIds(tenantId).map { it.contains(propertyId) }
    }
}
