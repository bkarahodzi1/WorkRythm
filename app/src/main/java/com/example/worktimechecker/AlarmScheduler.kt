package com.example.worktimechecker

import com.example.worktimechecker.model.AlarmItem

interface AlarmScheduler {
    fun schedule(item: AlarmItem)
    fun cancel(item: AlarmItem)
}