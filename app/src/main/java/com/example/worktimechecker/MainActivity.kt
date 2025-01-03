package com.example.worktimechecker

import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.worktimechecker.ui.theme.WorkTimeCheckerTheme
import com.example.worktimechecker.viewmodel.AuthViewModel
import com.example.worktimechecker.viewmodel.WorkSessionViewModel
import com.example.worktimechecker.viewmodel.UsersViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val authViewModel : AuthViewModel by viewModels()
        val usersViewModel : UsersViewModel by viewModels()
        val workSessionViewModel : WorkSessionViewModel by viewModels()
        setContent {
            WorkTimeCheckerTheme {
                Scaffold(modifier = Modifier.fillMaxSize().background(Color(0xFFe0e681))) { innerPadding ->
                    Navigator(modifier = Modifier.padding(innerPadding), authViewModel = authViewModel, usersViewModel = usersViewModel, workSessionViewModel = workSessionViewModel)
                }
            }
        }
    }
}
