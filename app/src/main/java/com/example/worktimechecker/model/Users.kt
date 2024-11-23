package com.example.worktimechecker.model

data class Users(
    val displayName: String = "",
    val email: String = "",
    val workSession: WorkSession = WorkSession()
)
