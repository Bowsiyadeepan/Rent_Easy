package com.example.renteasy.data.repository

import com.example.renteasy.data.model.Property
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
import java.util.UUID

class PropertyRepository(
    private val firestore: FirebaseFirestore? = try { FirebaseFirestore.getInstance() } catch (e: Throwable) { null },
    private val demoDataStore: DemoDataStore = DemoDataStore
) {
    private val tag = "PropertyRepository"

    fun getApprovedProperties(): Flow<List<Property>> {
        val firestoreFlow: Flow<List<Property>> = if (firestore != null) {
            callbackFlow {
                try {
                    val listener = firestore.collection(Constants.COLLECTION_PROPERTIES)
                        .whereEqualTo("status", Constants.STATUS_APPROVED)
                        .addSnapshotListener { snapshot, error ->
                            if (error != null) {
                                RentEasyLog.w(tag, "Firestore getApprovedProperties error: ${error.localizedMessage}")
                                trySend(emptyList())
                                return@addSnapshotListener
                            }
                            val list = snapshot?.documents?.mapNotNull { doc ->
                                doc.toObject(Property::class.java)?.copy(propertyId = doc.id)
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

        return combine(firestoreFlow, demoDataStore.properties) { firestoreList, demoList ->
            val approvedDemo = demoList.filter { it.status == Constants.STATUS_APPROVED }
            (firestoreList + approvedDemo).distinctBy { it.propertyId }
        }
    }

    fun getAllProperties(): Flow<List<Property>> {
        val firestoreFlow: Flow<List<Property>> = if (firestore != null) {
            callbackFlow {
                try {
                    val listener = firestore.collection(Constants.COLLECTION_PROPERTIES)
                        .addSnapshotListener { snapshot, error ->
                            if (error != null) {
                                RentEasyLog.w(tag, "Firestore getAllProperties error: ${error.localizedMessage}")
                                trySend(emptyList())
                                return@addSnapshotListener
                            }
                            val list = snapshot?.documents?.mapNotNull { doc ->
                                doc.toObject(Property::class.java)?.copy(propertyId = doc.id)
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

        return combine(firestoreFlow, demoDataStore.properties) { firestoreList, demoList ->
            (firestoreList + demoList).distinctBy { it.propertyId }
        }
    }

    fun getOwnerProperties(ownerId: String): Flow<List<Property>> {
        val firestoreFlow: Flow<List<Property>> = if (firestore != null) {
            callbackFlow {
                try {
                    val listener = firestore.collection(Constants.COLLECTION_PROPERTIES)
                        .whereEqualTo("ownerId", ownerId)
                        .addSnapshotListener { snapshot, error ->
                            if (error != null) {
                                RentEasyLog.w(tag, "Firestore getOwnerProperties error: ${error.localizedMessage}")
                                trySend(emptyList())
                                return@addSnapshotListener
                            }
                            val list = snapshot?.documents?.mapNotNull { doc ->
                                doc.toObject(Property::class.java)?.copy(propertyId = doc.id)
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

        return combine(firestoreFlow, demoDataStore.properties) { firestoreList, demoList ->
            val ownerDemo = demoList.filter { it.ownerId == ownerId }
            (firestoreList + ownerDemo).distinctBy { it.propertyId }
        }
    }

    fun getPropertyById(propertyId: String): Flow<Property?> {
        val firestoreFlow: Flow<Property?> = if (firestore != null) {
            callbackFlow {
                try {
                    val listener = firestore.collection(Constants.COLLECTION_PROPERTIES)
                        .document(propertyId)
                        .addSnapshotListener { snapshot, error ->
                            if (error != null) {
                                RentEasyLog.w(tag, "Firestore getPropertyById error: ${error.localizedMessage}")
                                trySend(null)
                                return@addSnapshotListener
                            }
                            val prop = snapshot?.toObject(Property::class.java)?.copy(propertyId = snapshot.id)
                            trySend(prop)
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

        return combine(firestoreFlow, demoDataStore.properties) { firestoreProp, demoList ->
            firestoreProp ?: demoList.find { it.propertyId == propertyId }
        }.distinctUntilChanged()
    }

    suspend fun addProperty(property: Property): Result<String> {
        val propertyId = if (property.propertyId.isBlank()) "prop_${UUID.randomUUID().toString().take(8)}" else property.propertyId
        val finalProperty = property.copy(propertyId = propertyId, status = Constants.STATUS_PENDING)

        demoDataStore.addProperty(finalProperty)

        if (firestore != null) {
            try {
                firestore.collection(Constants.COLLECTION_PROPERTIES)
                    .document(propertyId)
                    .set(finalProperty)
                    .await()
            } catch (e: Exception) {
                RentEasyLog.w(tag, "Firestore addProperty failed, saved locally: ${e.localizedMessage}")
            }
        }
        return Result.success(propertyId)
    }

    suspend fun updateProperty(property: Property): Result<Unit> {
        demoDataStore.updateProperty(property)

        if (firestore != null) {
            try {
                firestore.collection(Constants.COLLECTION_PROPERTIES)
                    .document(property.propertyId)
                    .set(property)
                    .await()
            } catch (e: Exception) {
                RentEasyLog.w(tag, "Firestore updateProperty failed, saved locally: ${e.localizedMessage}")
            }
        }
        return Result.success(Unit)
    }

    suspend fun deleteProperty(propertyId: String): Result<Unit> {
        demoDataStore.deleteProperty(propertyId)

        if (firestore != null) {
            try {
                firestore.collection(Constants.COLLECTION_PROPERTIES)
                    .document(propertyId)
                    .delete()
                    .await()
            } catch (e: Exception) {
                RentEasyLog.w(tag, "Firestore deleteProperty failed, deleted locally: ${e.localizedMessage}")
            }
        }
        return Result.success(Unit)
    }

    suspend fun updatePropertyStatus(propertyId: String, status: String): Result<Unit> {
        demoDataStore.updatePropertyStatus(propertyId, status)

        if (firestore != null) {
            try {
                firestore.collection(Constants.COLLECTION_PROPERTIES)
                    .document(propertyId)
                    .update("status", status)
                    .await()
            } catch (e: Exception) {
                RentEasyLog.w(tag, "Firestore updatePropertyStatus failed, saved locally: ${e.localizedMessage}")
            }
        }
        return Result.success(Unit)
    }
}
