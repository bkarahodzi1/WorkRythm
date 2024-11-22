package com.example.worktimechecker.viewmodel

import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.worktimechecker.model.Users
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UsersViewModel : ViewModel() {

    private var _userList = MutableStateFlow<List<Users>>(emptyList())
    var userList: StateFlow<List<Users>> = _userList

    private var _user = MutableStateFlow<Users?>(null)
    var user: StateFlow<Users?> = _user

    private val auth : FirebaseAuth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore

    /*init {
        viewModelScope.launch {
            val email = auth.currentUser?.email
            if (email != null) {
                getUserByEmail(email)
            } else {
                _user.value = null // No authenticated user
            }
        }
    }*/
    
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
        val user = hashMapOf(
            "displayName" to displayName,
            "email" to email
        )

        db.collection("users").document(email)
            .set(user)
    }

    fun signOutUser(){
        _user.value = Users()
    }
}