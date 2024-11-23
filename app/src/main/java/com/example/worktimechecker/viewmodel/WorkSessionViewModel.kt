package com.example.worktimechecker.viewmodel

import androidx.lifecycle.ViewModel
import com.example.worktimechecker.model.WorkSession
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import java.time.LocalDate

class WorkSessionViewModel: ViewModel() {

    private var _workSession = MutableStateFlow<WorkSession?>(null)
    var workSession: StateFlow<WorkSession?> = _workSession

    private val db = Firebase.firestore

    suspend fun startTime(currentUserEmail: String): Int{
        val status = checkStatus(currentUserEmail)
        if(status != 0)
            return status

        val newSession = hashMapOf(
            "startTime" to System.currentTimeMillis(),
            "pauseTime" to 0,
            "totalTime" to 0
        )

        db.collection("users").document(currentUserEmail)
            .collection("workSessions").document(LocalDate.now().toString())
            .set(newSession)
            .await()

        return 0
    }

    private suspend fun checkStatus(currentUserEmail: String): Int {
        try {
            val document = db.collection("users").document(currentUserEmail)
                .collection("workSessions").document(LocalDate.now().toString())
                .get()
                .await()

            if(document.exists()){
                _workSession.value = document.toObject()

                return if(_workSession.value!!.totalTime != 0.toLong())
                    2
                else
                    1
            }
            return 0
        } catch (e: Exception) {
            return 0
        }
    }

    suspend fun pauseTime(currentUserEmail: String): Int{
        val status = checkStatus(currentUserEmail)
        if(status != 1)
            return status

        val pausedTime = System.currentTimeMillis()

        db.collection("users").document(currentUserEmail)
            .collection("workSessions").document(LocalDate.now().toString())
            .update("pausedTime", pausedTime)
            .await()

        return 1
    }

    suspend fun continueTime(currentUserEmail: String){
        val continuedTime = System.currentTimeMillis()

        val document = db.collection("users").document(currentUserEmail)
            .collection("workSessions").document(LocalDate.now().toString())
            .get()
            .await()

        _workSession.value = document.toObject()

        db.collection("users").document(currentUserEmail)
            .collection("workSessions").document(LocalDate.now().toString())
            .update("pausedTimes", FieldValue.arrayUnion(continuedTime - _workSession.value?.pausedTime!!))
            .await()
    }

    suspend fun endTime(currentUserEmail: String): Int{
        val status = checkStatus(currentUserEmail)
        if(status != 1)
            return status

        val endTime = System.currentTimeMillis()

        val document = db.collection("users").document(currentUserEmail)
            .collection("workSessions").document(LocalDate.now().toString())
            .get()
            .await()

        _workSession.value = document.toObject()

        db.collection("users").document(currentUserEmail)
            .collection("workSessions").document(LocalDate.now().toString())
            .update("totalTime", endTime - _workSession.value?.startTime!!)
            .await()

        return 1
    }
}