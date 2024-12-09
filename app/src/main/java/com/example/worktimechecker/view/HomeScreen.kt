package com.example.worktimechecker.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.worktimechecker.R
import com.example.worktimechecker.model.AlarmItem
import com.example.worktimechecker.AndroidAlarmScheduler
import com.example.worktimechecker.viewmodel.AuthViewModel
import com.example.worktimechecker.viewmodel.AuthState
import com.example.worktimechecker.viewmodel.WorkSessionViewModel
import com.example.worktimechecker.viewmodel.UsersViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel, usersViewModel: UsersViewModel, workSessionViewModel: WorkSessionViewModel){

    val authState = authViewModel.authState.observeAsState()
    val user by usersViewModel.user.collectAsStateWithLifecycle()
    val workSessions by workSessionViewModel.workSessions.collectAsStateWithLifecycle()
    val workSession2 by workSessionViewModel.workSession2.collectAsStateWithLifecycle()

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var isSigningOut by remember { mutableStateOf(true) }
    var isReady by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    var selectedItemIndex by rememberSaveable {
        mutableIntStateOf(0)
    }

    val scheduler = AndroidAlarmScheduler(context)
    var alarmItem : AlarmItem? = null

    var isEnabled by remember{ mutableStateOf(false)}

    var selectedTabIndex by remember { mutableIntStateOf(0) }

    var hasNotificationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasNotificationPermission = isGranted
            if(hasNotificationPermission)
                isEnabled = true
        }
    )


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
        workSessionViewModel.setSessionsForWeek(user!!.email)
        if(workSessionViewModel.checkPaused(user!!.email)){
            isPaused = true
        }
        if(hasNotificationPermission)
            isEnabled = true
        isReady = true
        isSigningOut = false
    }


    if (isSigningOut) {
        Column(modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally)
        {
            CircularProgressIndicator()
        }

    }
    else if(isReady){
        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = true,
            drawerContent = {
                ModalDrawerSheet {
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(Color(0xFFe0e681))
                    ){

                    }
                    NavigationDrawerItem(label = {Text(text = "Home")},
                        selected = true,
                        onClick = {
                            navController.navigate("home")
                            selectedItemIndex = 0 },
                        icon = {
                            Icon(
                                imageVector = if (selectedItemIndex == 0) {
                                    Icons.Filled.Home
                                } else Icons.Outlined.Home,
                                contentDescription = "Home button"
                            )
                        }
                    )
                    NavigationDrawerItem(label = {Text(text = "Statistics")},
                        selected = false,
                        onClick = {
                            selectedItemIndex = 2
                            isSigningOut = true
                            navController.navigate("statistics") },
                        icon = {
                            Image(
                                painter = painterResource(id = R.drawable.piechart),
                                contentDescription = "Statistics",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    )
                    NavigationDrawerItem(label = {Text(text = "Sign out")},
                        selected = false,
                        onClick = {
                            selectedItemIndex = 1
                            isSigningOut = true
                            authViewModel.signOut()
                            usersViewModel.signOutUser() //workSessionViewModel.removeSessions()
                                  },
                        icon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Sign out button"
                            )
                        }
                    )
                    Row(
                        modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ){
                        var showDialog by remember { mutableStateOf(false) }
                        if(showDialog){
                            AlertDialog(
                                onDismissRequest = { showDialog = false },
                                confirmButton = {
                                    Button(onClick = {
                                        showDialog = false
                                        // Navigate to app settings
                                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                            data = Uri.parse("package:${context.packageName}")
                                        }
                                        context.startActivity(intent)
                                    }) {
                                        Text("Go to Settings")
                                    }
                                },
                                dismissButton = {
                                    Button(onClick = { showDialog = false }) {
                                        Text("Cancel")
                                    }
                                },
                                title = { Text("Revoke Permission") },
                                text = { Text("To disable the notification permission, please go to app settings and revoke the permission manually.") }
                            )
                        }
                        Switch(
                            modifier = Modifier.scale(0.75f),
                            checked = isEnabled,
                            onCheckedChange = {
                                if(isEnabled){
                                    showDialog = true
                                }
                                else
                                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(text = if(isEnabled){
                            "Disable Notifications"
                        }
                        else{
                            "Enable Notifications"
                        })
                    }
                }
            }
        ) {
            Scaffold(
                topBar = {
                    val coroutineScope = rememberCoroutineScope()
                    TopAppBar(
                        modifier = Modifier
                            .background(Color(0xFFe0e681))
                            .padding(0.dp),
                        title = {},
                        navigationIcon = {
                            IconButton(onClick = {
                                coroutineScope.launch {
                                    drawerState.open()
                                }
                            }) {
                                Icon(
                                    Icons.Rounded.Menu,
                                    contentDescription = "Menu Button"
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = {navController.navigate("statistics")}) {
                                Image(
                                    painter = painterResource(id = R.drawable.piechart),
                                    contentDescription = "Statistics",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    )
                }
            ) {
                paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                    Text(text = "Welcome", fontSize = 24.sp)
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
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(4.dp),
                        onClick = {
                            coroutineScope.launch {
                                val currentTime = LocalTime.now().toNanoOfDay() / 1000000
                                val resultOfEndingDay = workSessionViewModel.startTime(authViewModel.getCurrentUser()?.email.toString(), currentTime)
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
                                else if (hasNotificationPermission){
                                    alarmItem = AlarmItem(
                                        time = LocalDateTime.now()
                                            .plusSeconds(5),
                                        message = "Consider taking a break"
                                    )
                                    alarmItem?.let(scheduler::schedule)
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
                                .padding(horizontal = 16.dp),
                            shape = RoundedCornerShape(4.dp),
                            onClick = {
                                coroutineScope.launch {
                                    val resultOfEndingDay = workSessionViewModel.pauseTime(authViewModel.getCurrentUser()?.email.toString(), LocalTime.now().toNanoOfDay() / 1000000)
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
                                        isPaused = true
                                        alarmItem?.let(scheduler::cancel)
                                    }
                                } },
                            enabled = authState.value != AuthState.Loading
                        ) {
                            Text(text = "Break")
                        }
                    }else{
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            shape = RoundedCornerShape(4.dp),
                            onClick = {
                                isPaused = false
                                coroutineScope.launch {
                                    workSessionViewModel.continueTime(authViewModel.getCurrentUser()?.email.toString(),  LocalTime.now().toNanoOfDay() / 1000000)
                                }
                                alarmItem = AlarmItem(
                                    time = LocalDateTime.now()
                                        .plusSeconds(5),
                                    message = "test"
                                )
                                alarmItem?.let(scheduler::schedule)
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
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(4.dp),
                        onClick = {
                            coroutineScope.launch {
                                val resultOfEndingDay = workSessionViewModel.endTime(authViewModel.getCurrentUser()?.email.toString(), LocalTime.now().toNanoOfDay() / 1000000)
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
                                else if(isPaused){
                                    workSessionViewModel.continueTime(authViewModel.getCurrentUser()?.email.toString(),  LocalTime.now().toNanoOfDay() / 1000000)
                                    isPaused = false
                                }
                            }
                        },
                        enabled = authState.value != AuthState.Loading
                    ) {
                        Text(text = "End day")
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(text = "Today's work", fontSize = 24.sp)

                    Spacer(modifier = Modifier.height(16.dp))

                    if (workSession2 != null && workSession2!!.date == LocalDate.now().toString()) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(0.65f),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Work started at:")
                                Text(text = formatTime(workSession2!!.startTime))
                            }

                            if(isPaused){
                                Row(
                                    modifier = Modifier.fillMaxWidth(0.65f),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "Break started at:")
                                    Text(text = formatTime(workSession2!!.pauseStartTime))
                                }
                            }

                            else if(workSession2!!.pausedForTimes.isNotEmpty()){
                                Row(
                                    modifier = Modifier.fillMaxWidth(0.65f),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "Last break lasted:")
                                    Text(text = formatTime(workSession2!!.pausedForTimes[workSession2!!.pausedForTimes.size - 1]))
                                }
                            }

                            if(workSession2!!.endTime != 0L){
                                Row(
                                    modifier = Modifier.fillMaxWidth(0.65f),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "Work ended at:")
                                    Text(text = formatTime(workSession2!!.endTime))
                                }
                            }
                            else{
                                Text(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),text = "")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(text = "Previous work", fontSize = 24.sp)

                    Spacer(modifier = Modifier.height(16.dp))
                    TabRow(selectedTabIndex = selectedTabIndex,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RectangleShape
                            )
                    ) {
                        Tab(
                            selected = 0 == selectedTabIndex,
                            onClick = {
                                if(selectedTabIndex != 0){
                                    workSessionViewModel.setSessionsForWeek(user!!.email)
                                }
                                selectedTabIndex = 0
                            },
                            text = { Text(text = "Last Week")},
                            selectedContentColor = MaterialTheme.colorScheme.background,
                            unselectedContentColor = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.background(if (0 == selectedTabIndex) MaterialTheme.colorScheme.primary else Color.Transparent)
                        )
                        Tab(
                            selected = 1 == selectedTabIndex,
                            onClick = {
                                if(selectedTabIndex != 1){
                                    workSessionViewModel.setSessionsForThisMonth(user!!.email)
                                }
                                selectedTabIndex = 1
                            },
                            text = { Text(text = "Current Month")},
                            selectedContentColor = MaterialTheme.colorScheme.background,
                            unselectedContentColor = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.background(if (1 == selectedTabIndex) MaterialTheme.colorScheme.primary else Color.Transparent)
                        )
                        Tab(
                            selected = 2 == selectedTabIndex,
                            onClick = {
                                if(selectedTabIndex != 2){
                                    workSessionViewModel.setSessionsForLastMonth(user!!.email)
                                }
                                selectedTabIndex = 2
                            },
                            text = { Text(text = "Last Month")},
                            selectedContentColor = MaterialTheme.colorScheme.background,
                            unselectedContentColor = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.background(if (2 == selectedTabIndex) MaterialTheme.colorScheme.primary else Color.Transparent)
                        )
                        /*Tab(
                            selected = 3 == selectedTabIndex,
                            onClick = {
                                selectedTabIndex = 3
                            },
                            text = { Text(text = "Calendar")},
                            selectedContentColor = MaterialTheme.colorScheme.background,
                            unselectedContentColor = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.background(if (3 == selectedTabIndex) MaterialTheme.colorScheme.primary else Color.Transparent)
                        )*/
                    }

                    LazyColumn(modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(225.dp)
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = RectangleShape
                        )
                        .background(MaterialTheme.colorScheme.background)
                    )
                    {
                        items(workSessions) { session ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                                    .height(40.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxHeight(),
                                    contentAlignment = Alignment.Center // Ensures text is vertically and horizontally centered
                                ) {
                                    Text(
                                        text = getDayOfWeek(session.date) + " " + getDayAndMonth(session.date),
                                        color = MaterialTheme.colorScheme.background,
                                        fontSize = 20.sp
                                    )
                                }
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp)
                            ) {
                                val startTimeFormatted = formatTime(session.startTime)

                                Text(
                                    text = "Work started at:",
                                    modifier = Modifier.weight(1f),
                                )
                                Text(
                                    text = startTimeFormatted,
                                    modifier = Modifier.weight(1f),
                                )
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp)
                            ) {
                                val endTimeFormatted = formatTime(session.endTime)

                                Text(
                                    text = "Work ended at:",
                                    modifier = Modifier.weight(1f),
                                )
                                Text(
                                    text = endTimeFormatted,
                                    modifier = Modifier.weight(1f),
                                )
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp)
                            ) {
                                Text(
                                    text = "Worked for:",
                                    modifier = Modifier.weight(1f),
                                )
                                Text(
                                    text = formatTime(session.totalTime),
                                    modifier = Modifier.weight(1f),
                                )
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp),
                                verticalAlignment = Alignment.Bottom,
                            ) {
                                Text(
                                    text = "Number of breaks taken:",
                                    modifier = Modifier.weight(1f),
                                )
                                Text(
                                    text = session.pauseStartTimes.size.toString(),
                                    modifier = Modifier.weight(1f),
                                )
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp),
                                verticalAlignment = Alignment.Bottom,
                            ) {
                                Text(
                                    text = "Average break time:",
                                    modifier = Modifier.weight(1f),
                                )
                                Text(
                                    text = formatTime((session.pausedForTimes.sum().toDouble() / session.pausedForTimes.size).toLong()).toString(),
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun formatTime(timeInMillis: Long): String {
    if (timeInMillis == 0L)
        return ""
    val hours = TimeUnit.MILLISECONDS.toHours(timeInMillis)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(timeInMillis) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(timeInMillis) % 60
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}

fun getDayOfWeek(dateString: String, pattern: String = "yyyy-MM-dd"): String {
    if (dateString.isEmpty())
        return ""
    val formatter = DateTimeFormatter.ofPattern(pattern)
    val date = LocalDate.parse(dateString, formatter)
    return date.dayOfWeek.toString()
}

fun getDayAndMonth(dateString: String): String {
    if (dateString.isEmpty())
        return ""
    val day = dateString.substring(8, 10) // Extracts "dd"
    val month = dateString.substring(5, 7) // Extracts "MM"
    return "$day/$month"
}