package com.example.worktimechecker.model

data class WorkSession(
    val startTime: Long = 0L,
    val pausedTime: Long = 0L,
    val pausedTimes: List<Long> = emptyList(),
    val totalTime: Long = 0L
)
