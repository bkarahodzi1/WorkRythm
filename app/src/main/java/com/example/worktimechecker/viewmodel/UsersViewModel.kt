package com.example.worktimechecker.viewmodel

import androidx.lifecycle.ViewModel
import com.example.worktimechecker.model.Users
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await

class UsersViewModel : ViewModel() {

    private var _userList = MutableStateFlow<List<Users>>(emptyList())
    var userList: StateFlow<List<Users>> = _userList

    private var _user = MutableStateFlow<Users?>(null)
    var user: StateFlow<Users?> = _user

    private val db = Firebase.firestore

    fun getUsers(){
        db.collection("users")
            .addSnapshotListener{ value, error ->
                if(error != null){
                    return@addSnapshotListener
                }
                if(value != null){
                    _userList.value = value.toObjects()
                }
            }
    }

    suspend fun getUserByID(id: String){
        db.collection("users").document(id)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                _user.value = documentSnapshot.toObject()
            }
            .await()
    }

    suspend fun getUserByEmail(email: String){
        db.collection("users").document(email)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                _user.value = documentSnapshot.toObject()
            }
            .addOnFailureListener{
                _user.value = Users()
            }
            .await()
    }

    fun createUser(displayName: String, email: String){
        val newUser = hashMapOf(
            "displayName" to displayName,
            "email" to email
        )

        val newWorkSession = hashMapOf(
            "date" to "1900-01-01",
            "startTime" to 0,
            "endTime" to 0
        )

        db.collection("users").document(email)
            .set(newUser)

        db.collection("users").document(email)
            .collection("workSessions").document("1900-01-01")
            .set(newWorkSession)

        _user.value = Users(newUser["displayName"]!!,newUser["email"]!!)
    }

    fun signOutUser(){
        _user.value = Users()
    }
}