package com.example.renteasy.data.repository

import com.example.renteasy.data.model.RentalRequest
import com.example.renteasy.utils.Constants
import com.example.renteasy.utils.RentEasyLog
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.UUID

class RentalRequestRepository(
    private val firestore: FirebaseFirestore = try { FirebaseFirestore.getInstance() } catch (e: Exception) { FirebaseFirestore.getInstance() },
    private val demoDataStore: DemoDataStore = DemoDataStore
) {
    private val tag = "RentalRequestRepository"

    fun getTenantRequests(tenantId: String): Flow<List<RentalRequest>> {
        val firestoreFlow: Flow<List<RentalRequest>> = callbackFlow {
            try {
                val listener = firestore.collection(Constants.COLLECTION_RENTAL_REQUESTS)
                    .whereEqualTo("tenantId", tenantId)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            RentEasyLog.w(tag, "Firestore getTenantRequests error: ${error.localizedMessage}")
                            trySend(emptyList())
                            return@addSnapshotListener
                        }
                        val list = snapshot?.documents?.mapNotNull { doc ->
                            doc.toObject(RentalRequest::class.java)?.copy(requestId = doc.id)
                        } ?: emptyList()
                        trySend(list)
                    }
                awaitClose { listener.remove() }
            } catch (e: Exception) {
                trySend(emptyList())
                awaitClose { }
            }
        }

        return combine(firestoreFlow, demoDataStore.rentalRequests) { firestoreList, demoList ->
            val tenantDemo = demoList.filter { it.tenantId == tenantId }
            (firestoreList + tenantDemo).distinctBy { it.requestId }
        }
    }

    fun getOwnerRequests(ownerId: String): Flow<List<RentalRequest>> {
        val firestoreFlow: Flow<List<RentalRequest>> = callbackFlow {
            try {
                val listener = firestore.collection(Constants.COLLECTION_RENTAL_REQUESTS)
                    .whereEqualTo("ownerId", ownerId)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            RentEasyLog.w(tag, "Firestore getOwnerRequests error: ${error.localizedMessage}")
                            trySend(emptyList())
                            return@addSnapshotListener
                        }
                        val list = snapshot?.documents?.mapNotNull { doc ->
                            doc.toObject(RentalRequest::class.java)?.copy(requestId = doc.id)
                        } ?: emptyList()
                        trySend(list)
                    }
                awaitClose { listener.remove() }
            } catch (e: Exception) {
                trySend(emptyList())
                awaitClose { }
            }
        }

        return combine(firestoreFlow, demoDataStore.rentalRequests) { firestoreList, demoList ->
            val ownerDemo = demoList.filter { it.ownerId == ownerId }
            (firestoreList + ownerDemo).distinctBy { it.requestId }
        }
    }

    fun getAllRequests(): Flow<List<RentalRequest>> {
        val firestoreFlow: Flow<List<RentalRequest>> = callbackFlow {
            try {
                val listener = firestore.collection(Constants.COLLECTION_RENTAL_REQUESTS)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            RentEasyLog.w(tag, "Firestore getAllRequests error: ${error.localizedMessage}")
                            trySend(emptyList())
                            return@addSnapshotListener
                        }
                        val list = snapshot?.documents?.mapNotNull { doc ->
                            doc.toObject(RentalRequest::class.java)?.copy(requestId = doc.id)
                        } ?: emptyList()
                        trySend(list)
                    }
                awaitClose { listener.remove() }
            } catch (e: Exception) {
                trySend(emptyList())
                awaitClose { }
            }
        }

        return combine(firestoreFlow, demoDataStore.rentalRequests) { firestoreList, demoList ->
            (firestoreList + demoList).distinctBy { it.requestId }
        }
    }

    suspend fun createRentalRequest(request: RentalRequest): Result<String> {
        val requestId = if (request.requestId.isBlank()) "req_${UUID.randomUUID().toString().take(8)}" else request.requestId
        val finalRequest = request.copy(requestId = requestId, timestamp = request.timestamp ?: Date())

        // 1. Check duplicate pending in DemoDataStore
        val demoResult = demoDataStore.createRentalRequest(finalRequest)
        if (demoResult.isFailure) {
            return demoResult
        }

        // 2. Sync to Firestore
        try {
            // Check if already pending on Firestore
            val existing = firestore.collection(Constants.COLLECTION_RENTAL_REQUESTS)
                .whereEqualTo("tenantId", finalRequest.tenantId)
                .whereEqualTo("propertyId", finalRequest.propertyId)
                .whereEqualTo("status", Constants.REQUEST_PENDING)
                .get()
                .await()

            if (!existing.isEmpty) {
                return Result.failure(IllegalStateException("A pending rental request already exists for this property."))
            }

            firestore.collection(Constants.COLLECTION_RENTAL_REQUESTS)
                .document(requestId)
                .set(finalRequest)
                .await()
        } catch (e: Exception) {
            RentEasyLog.w(tag, "Firestore createRentalRequest fallback: ${e.localizedMessage}")
        }
        return Result.success(requestId)
    }

    suspend fun acceptRentalRequest(requestId: String, propertyId: String): Result<Unit> {
        RentEasyLog.i(tag, "Accepting request $requestId for property $propertyId")

        // 1. Atomic update in DemoDataStore
        val demoResult = demoDataStore.acceptRentalRequest(requestId, propertyId)
        if (demoResult.isFailure) {
            return demoResult
        }

        // 2. Atomic Firestore Batch Write: request Accepted + property RENTED
        try {
            val batch = firestore.batch()
            val requestRef = firestore.collection(Constants.COLLECTION_RENTAL_REQUESTS).document(requestId)
            val propertyRef = firestore.collection(Constants.COLLECTION_PROPERTIES).document(propertyId)

            batch.update(requestRef, "status", Constants.REQUEST_ACCEPTED)
            batch.update(propertyRef, "status", Constants.STATUS_RENTED)
            batch.commit().await()
            RentEasyLog.i(tag, "Batch write succeeded in Firestore for acceptance")
        } catch (e: Exception) {
            RentEasyLog.w(tag, "Firestore accept batch failed or offline, mirrored locally: ${e.localizedMessage}")
        }

        return Result.success(Unit)
    }

    suspend fun rejectRentalRequest(requestId: String): Result<Unit> {
        demoDataStore.rejectRentalRequest(requestId)

        try {
            firestore.collection(Constants.COLLECTION_RENTAL_REQUESTS)
                .document(requestId)
                .update("status", Constants.REQUEST_REJECTED)
                .await()
        } catch (e: Exception) {
            RentEasyLog.w(tag, "Firestore reject failed, mirrored locally: ${e.localizedMessage}")
        }
        return Result.success(Unit)
    }
}
