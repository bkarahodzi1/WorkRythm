package com.example.worktimechecker

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.worktimechecker.view.HomeScreen
import com.example.worktimechecker.view.LogInScreen
import com.example.worktimechecker.view.SignUpScreen
import com.example.worktimechecker.viewmodel.AuthViewModel
import com.example.worktimechecker.viewmodel.WorkSessionViewModel
import com.example.worktimechecker.viewmodel.UsersViewModel

@Composable
fun Navigator(modifier: Modifier = Modifier, authViewModel: AuthViewModel, usersViewModel: UsersViewModel, workSessionViewModel: WorkSessionViewModel){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login", builder = {
        composable("login"){
            LogInScreen(modifier, navController, authViewModel, usersViewModel)
        }
        composable("signup"){
            SignUpScreen(modifier, navController, authViewModel, usersViewModel)
        }
        composable("home"){
            HomeScreen(modifier, navController, authViewModel, usersViewModel, workSessionViewModel)
        }
    })
}