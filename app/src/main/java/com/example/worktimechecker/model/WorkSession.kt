package com.example.worktimechecker.model

data class WorkSession(
    val date: String = "",
    val startTime: Long = 0L,
    val endTime: Long = 0L,
    val pauseStartTime: Long = 0L,
    val pausedForTimes: List<Long> = emptyList(),
    val pauseStartTimes: List<Long> = emptyList(),
    val pauseEndTimes: List<Long> = emptyList(),
    val totalTime: Long = 0L
)