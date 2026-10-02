package com.example.renteasy.data.repository

import com.example.renteasy.data.model.User
import com.example.renteasy.utils.Constants
import com.example.renteasy.utils.RentEasyLog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class AuthRepository(
    private val auth: FirebaseAuth = try { FirebaseAuth.getInstance() } catch (e: Exception) { FirebaseAuth.getInstance() },
    private val firestore: FirebaseFirestore = try { FirebaseFirestore.getInstance() } catch (e: Exception) { FirebaseFirestore.getInstance() },
    private val demoDataStore: DemoDataStore = DemoDataStore
) {
    private val tag = "AuthRepository"

    val currentUser: StateFlow<User?> = demoDataStore.currentUser

    suspend fun login(email: String, password: String): Result<User> {
        val trimmedEmail = email.trim()
        val trimmedPassword = password.trim()

        RentEasyLog.i(tag, "Attempting login for: $trimmedEmail")

        // 1. Check demo accounts first for instant offline/demo switch
        val demoUser = demoDataStore.getUserByEmail(trimmedEmail)
        if (demoUser != null) {
            demoDataStore.setCurrentUser(demoUser)
            RentEasyLog.i(tag, "Logged in via DemoDataStore as ${demoUser.role}: ${demoUser.name}")
            return Result.success(demoUser)
        }

        // 2. Try Firebase Auth
        return try {
            val authResult = auth.signInWithEmailAndPassword(trimmedEmail, trimmedPassword).await()
            val firebaseUser = authResult.user
            if (firebaseUser != null) {
                // Fetch user doc from Firestore
                val doc = firestore.collection(Constants.COLLECTION_USERS)
                    .document(firebaseUser.uid)
                    .get()
                    .await()

                val user = doc.toObject(User::class.java)?.copy(uid = firebaseUser.uid) ?: User(
                    uid = firebaseUser.uid,
                    name = firebaseUser.displayName ?: trimmedEmail.substringBefore("@"),
                    email = trimmedEmail,
                    role = Constants.ROLE_TENANT
                )

                demoDataStore.saveUser(user)
                demoDataStore.setCurrentUser(user)
                Result.success(user)
            } else {
                Result.failure(Exception("Authentication returned empty user"))
            }
        } catch (e: Exception) {
            RentEasyLog.w(tag, "Firebase login failed or offline: ${e.localizedMessage}")
            
            // Fallback: Check if user exists in DemoDataStore by email
            val fallbackUser = demoDataStore.getUserByEmail(trimmedEmail)
            if (fallbackUser != null) {
                demoDataStore.setCurrentUser(fallbackUser)
                Result.success(fallbackUser)
            } else {
                Result.failure(e)
            }
        }
    }

    suspend fun register(
        name: String,
        email: String,
        phone: String,
        password: String,
        role: String
    ): Result<User> {
        val trimmedEmail = email.trim()
        val trimmedName = name.trim()
        val trimmedPhone = phone.trim()

        RentEasyLog.i(tag, "Registering new user: $trimmedEmail with role $role")

        return try {
            val authResult = auth.createUserWithEmailAndPassword(trimmedEmail, password).await()
            val uid = authResult.user?.uid ?: UUID.randomUUID().toString()
            val newUser = User(
                uid = uid,
                name = trimmedName,
                email = trimmedEmail,
                phone = trimmedPhone,
                role = role
            )

            // Save to Firestore
            firestore.collection(Constants.COLLECTION_USERS)
                .document(uid)
                .set(newUser)
                .await()

            // Save to DemoDataStore
            demoDataStore.saveUser(newUser)
            demoDataStore.setCurrentUser(newUser)
            Result.success(newUser)
        } catch (e: Exception) {
            RentEasyLog.w(tag, "Firebase registration failed, saving locally: ${e.localizedMessage}")
            val uid = "user_${UUID.randomUUID().toString().take(8)}"
            val newUser = User(
                uid = uid,
                name = trimmedName,
                email = trimmedEmail,
                phone = trimmedPhone,
                role = role
            )
            demoDataStore.saveUser(newUser)
            demoDataStore.setCurrentUser(newUser)
            Result.success(newUser)
        }
    }

    fun logout() {
        try {
            auth.signOut()
        } catch (e: Exception) {
            RentEasyLog.w(tag, "Firebase signOut exception ignored: ${e.localizedMessage}")
        }
        demoDataStore.setCurrentUser(null)
    }

    fun getCurrentUser(): User? {
        return demoDataStore.currentUser.value
    }

    fun switchDemoUser(role: String) {
        val targetUser = when (role) {
            Constants.ROLE_ADMIN -> demoDataStore.getUser(Constants.DEMO_ADMIN_UID)
            Constants.ROLE_OWNER -> demoDataStore.getUser(Constants.DEMO_OWNER_UID)
            else -> demoDataStore.getUser(Constants.DEMO_TENANT_UID)
        }
        demoDataStore.setCurrentUser(targetUser)
    }
}
