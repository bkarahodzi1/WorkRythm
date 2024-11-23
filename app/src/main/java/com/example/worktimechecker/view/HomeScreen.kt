package com.example.worktimechecker.view

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.worktimechecker.viewmodel.AuthViewModel
import com.example.worktimechecker.viewmodel.AuthState
import com.example.worktimechecker.viewmodel.WorkSessionViewModel
import com.example.worktimechecker.viewmodel.UsersViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel, usersViewModel: UsersViewModel, workSessionViewModel: WorkSessionViewModel){

    val authState = authViewModel.authState.observeAsState()
    val user by usersViewModel.user.collectAsStateWithLifecycle()
    val workSession by workSessionViewModel.workSession.collectAsStateWithLifecycle()

    val coroutineScope = rememberCoroutineScope()

    var isSigningOut by remember { mutableStateOf(false) }

    var isPaused by remember { mutableStateOf(false) }

    val context = LocalContext.current

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

        if (isSigningOut) {
            CircularProgressIndicator()
        } else {

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
                isSigningOut = true
                authViewModel.signOut()
                usersViewModel.signOutUser()
            }) {
                Text(text = "Sign out")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                shape = RoundedCornerShape(4.dp),
                onClick = {
                    coroutineScope.launch {
                        val resultOfEndingDay = workSessionViewModel.startTime(authViewModel.getCurrentUser()?.email.toString())
                        if(resultOfEndingDay == 1) {
                            Toast.makeText(
                                context,
                                "Session has already been started",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        else if(resultOfEndingDay == 2){
                            Toast.makeText(
                                context,
                                "Session can't be started again",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                },
                enabled = authState.value != AuthState.Loading
            ) {
                Text(text = "Start day")
            }

            Spacer(modifier = Modifier.height(8.dp))
            if(!isPaused){
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    shape = RoundedCornerShape(4.dp),
                    onClick = {
                        isPaused = true
                        coroutineScope.launch {
                            val resultOfEndingDay = workSessionViewModel.pauseTime(authViewModel.getCurrentUser()?.email.toString())
                            if(resultOfEndingDay == 0) {
                                Toast.makeText(
                                    context,
                                    "Session hasn't been started yet",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            else if(resultOfEndingDay == 2){
                                Toast.makeText(
                                    context,
                                    "Session has already been ended",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    },
                    enabled = authState.value != AuthState.Loading
                ) {
                    Text(text = "Break")
                }
            }else{
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    shape = RoundedCornerShape(4.dp),
                    onClick = {
                        isPaused = false
                        coroutineScope.launch {
                            workSessionViewModel.continueTime(authViewModel.getCurrentUser()?.email.toString())
                        }
                    },
                    enabled = authState.value != AuthState.Loading
                ) {
                    Text(text = "Continue working")
                }
            }


            Spacer(modifier = Modifier.height(8.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                shape = RoundedCornerShape(4.dp),
                onClick = {
                    coroutineScope.launch {
                        val resultOfEndingDay = workSessionViewModel.endTime(authViewModel.getCurrentUser()?.email.toString())
                        if(resultOfEndingDay == 0) {
                            Toast.makeText(
                                context,
                                "Session hasn't been started yet",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        else if(resultOfEndingDay == 2){
                            Toast.makeText(
                                context,
                                "Session has already been ended",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        else{
                            workSessionViewModel.continueTime(authViewModel.getCurrentUser()?.email.toString())
                        }
                    }
                },
                enabled = authState.value != AuthState.Loading
            ) {
                Text(text = "End day")
            }
        }
    }

}