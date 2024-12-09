package com.example.worktimechecker.viewmodel

import androidx.lifecycle.ViewModel
import com.example.worktimechecker.model.WorkSession
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import java.time.LocalDate

class WorkSessionViewModel: ViewModel() {

    private val _workSession = MutableStateFlow<WorkSession?>(null)
    val workSession: StateFlow<WorkSession?> = _workSession

    private val _workSession2 = MutableStateFlow<WorkSession?>(null)
    val workSession2: StateFlow<WorkSession?> = _workSession2

    private val _workSessions = MutableStateFlow<List<WorkSession>>(emptyList())
    val workSessions: StateFlow<List<WorkSession>> = _workSessions

    private var _workSessionsState = MutableStateFlow<Int>(0)
    val workSessionsState: StateFlow<Int> = _workSessionsState

    private val db = Firebase.firestore

    fun setSessionsForWeek(currentUserEmail: String){
        db.collection("users").document(currentUserEmail)
            .collection("workSessions")
            .orderBy("date", Query.Direction.DESCENDING)
            .limit(7)
            .addSnapshotListener{ value, error ->
                if(error != null){
                    return@addSnapshotListener
                }
                if(value != null){
                    _workSessions.value = value.toObjects<WorkSession>().toList()
                    _workSession2.value = value.toObjects<WorkSession>().toList()[0]
                }
            }
        _workSessionsState.value = 0
    }

    fun setSessions(currentUserEmail: String){
        db.collection("users").document(currentUserEmail)
            .collection("workSessions")
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener{ value, error ->
                if(error != null){
                    return@addSnapshotListener
                }
                if(value != null){
                    _workSessions.value = value.toObjects<WorkSession>().toList()
                }
            }
        _workSessionsState.value = 3
    }

    fun setSessionsForThisMonth(currentUserEmail: String){
        var year = LocalDate.now().year
        var month = LocalDate.now().monthValue

        db.collection("users").document(currentUserEmail)
            .collection("workSessions")
            .whereGreaterThanOrEqualTo("date", "${LocalDate.now().year}-${LocalDate.now().monthValue}-01")
            .whereLessThanOrEqualTo("date", "${LocalDate.now().year}-${LocalDate.now().monthValue}-31")
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener{ value, error ->
                if(error != null){
                    return@addSnapshotListener
                }
                if(value != null){
                    _workSessions.value = value.toObjects<WorkSession>().toList()
                }
            }
        _workSessionsState.value = 1
    }

    fun setSessionsForLastMonth(currentUserEmail: String){
        var year = LocalDate.now().year
        var month = LocalDate.now().monthValue
        if(month == 1){
            month = 13
            year = LocalDate.now().year -1
        }
        db.collection("users").document(currentUserEmail)
            .collection("workSessions")
            .whereGreaterThanOrEqualTo("date", "${year}-${month-1}-01")
            .whereLessThanOrEqualTo("date", "${year}-${month-1}-31")
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener{ value, error ->
                if(error != null){
                    return@addSnapshotListener
                }
                if(value != null){
                    _workSessions.value = value.toObjects<WorkSession>().toList()
                }
            }
        _workSessionsState.value = 2
    }

    suspend fun startTime(currentUserEmail: String, currentTime: Long): Int{
        val status = checkStatus(currentUserEmail)
        if(status != 0)
            return status

        val newSession = hashMapOf(
            "date" to LocalDate.now().toString(),
            "startTime" to currentTime,
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

    suspend fun pauseTime(currentUserEmail: String, currentTime: Long): Int{
        val status = checkStatus(currentUserEmail)
        if(status != 1)
            return status

        val newPause = hashMapOf(
            "pauseStartTime" to currentTime,
            "pauseStartTimes" to FieldValue.arrayUnion(currentTime)
        )

        db.collection("users").document(currentUserEmail)
            .collection("workSessions").document(LocalDate.now().toString())
            .update(newPause)
            .await()

        return 1
    }

    suspend fun checkPaused(currentUserEmail: String): Boolean{
        val document = db.collection("users")
            .document(currentUserEmail)
            .collection("workSessions")
            .document(LocalDate.now().toString())
            .get()
            .await()
        if(document.getLong("pauseStartTime") == null){
            return false
        }
        return document.getLong("pauseStartTime") != 0L
    }

    suspend fun continueTime(currentUserEmail: String, currentTime: Long){

        try{
            val document = db.collection("users").document(currentUserEmail)
                .collection("workSessions").document(LocalDate.now().toString())
                .get()
                .await()
            _workSession.value = document.toObject()
        }catch (e: Exception){
            val e2 = e.message
        }

        val newPause = hashMapOf(
            "pauseStartTime" to 0L,
            "pausedForTimes" to FieldValue.arrayUnion(currentTime - _workSession.value?.pauseStartTime!!),
            "pauseEndTimes" to FieldValue.arrayUnion(currentTime)
        )

        db.collection("users").document(currentUserEmail)
            .collection("workSessions").document(LocalDate.now().toString())
            .update(newPause)
            .await()
    }

    suspend fun endTime(currentUserEmail: String, currentTime: Long): Int{
        val status = checkStatus(currentUserEmail)
        if(status != 1)
            return status


        val document = db.collection("users").document(currentUserEmail)
            .collection("workSessions").document(LocalDate.now().toString())
            .get()
            .await()

        _workSession.value = document.toObject()

        val newEndTime = hashMapOf(
            "endTime" to currentTime,
            "totalTime" to currentTime - _workSession.value?.startTime!!
         )

        db.collection("users").document(currentUserEmail)
            .collection("workSessions").document(LocalDate.now().toString())
            .update(newEndTime as Map<String, Any>)
            .await()

        return 1
    }
}