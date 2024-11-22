package com.example.worktimechecker.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.worktimechecker.model.Users
import com.example.worktimechecker.viewmodel.Auth
import com.example.worktimechecker.viewmodel.AuthState
import com.example.worktimechecker.viewmodel.UsersViewModel

@Composable
fun HomeScreen(modifier: Modifier = Modifier, navController: NavController, authViewModel: Auth, usersViewModel: UsersViewModel){

    val authState = authViewModel.authState.observeAsState()
    val user by usersViewModel.user.collectAsStateWithLifecycle()

    LaunchedEffect(authState.value) {
        when(authState.value){
            is AuthState.Unauthenticated -> navController.navigate("login")
            else -> Unit
        }
    }

    LaunchedEffect(Unit) {
        val currentUser = authViewModel.getCurrentUser()
        if (currentUser?.email != null) {
            usersViewModel.getUserByEmail(currentUser.email!!)
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Home Page", fontSize = 32.sp)

        if (authState.value is AuthState.Authenticated) {
            if(!user?.email.isNullOrEmpty()){
                Text(text = user!!.displayName)
            }
            else{
                val currentUser = authViewModel.getCurrentUser()
                usersViewModel.createUser(currentUser?.displayName.toString(),
                    currentUser?.email.toString()
                )
            }
        }

        TextButton(onClick = {
            usersViewModel.signOutUser()
            authViewModel.signOut()
        }) {
            Text(text = "Sign out")
        }
    }

}