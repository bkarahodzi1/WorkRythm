package com.example.worktimechecker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.worktimechecker.ui.theme.WorkTimeCheckerTheme
import com.example.worktimechecker.viewmodel.Auth
import com.example.worktimechecker.viewmodel.UsersViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val authViewModel : Auth by viewModels()
        val usersViewModel : UsersViewModel by viewModels()
        setContent {
            WorkTimeCheckerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Navigator(modifier = Modifier.padding(innerPadding), authViewModel = authViewModel, usersViewModel = usersViewModel)
                }
            }
        }
    }
}
