package com.example.worktimechecker.view


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapPosition
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.worktimechecker.R
import com.example.worktimechecker.viewmodel.AuthViewModel
import com.example.worktimechecker.viewmodel.WorkSessionViewModel
import com.example.worktimechecker.viewmodel.UsersViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel, usersViewModel: UsersViewModel, workSessionViewModel: WorkSessionViewModel){
    val authState = authViewModel.authState.observeAsState()
    val user by usersViewModel.user.collectAsStateWithLifecycle()
    val workSessions by workSessionViewModel.workSessions.collectAsStateWithLifecycle()
    val workSessionsState by workSessionViewModel.workSessionsState.collectAsStateWithLifecycle()


    var workDuration = 0L
    if(workSessions.isNotEmpty())
        workDuration = workSessions.sumOf { it.totalTime } / workSessions.size

    var breakDuration: Long = 0L
    var breaks: Int = 0
    for (work in workSessions){
        if(work.pausedForTimes.isNotEmpty()) {
            breakDuration += work.pausedForTimes.sum()
            breaks += work.pausedForTimes.size
        }
    }
    if (breaks != 0)
        breakDuration /= breaks

    val totalDuration = (workDuration + breakDuration).takeIf { it > 0 } ?: 1L
    val workSweepAngle = (workDuration.toFloat() / totalDuration) * 360f
    val breakSweepAngle = (breakDuration.toFloat() / totalDuration) * 360f

    var isSigningOut by remember { mutableStateOf(false) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    var selectedItemIndex by rememberSaveable {
        mutableIntStateOf(2)
    }

    val isDropDownExpanded = remember {
        mutableStateOf(false)
    }

    val times = listOf("Last Week", "Current Month", "Last Month", "All Time")

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
                    selected = false,
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
                    selected = true,
                    onClick = {
                        selectedItemIndex = 2
                        isSigningOut = true
                        navController.navigate("statistics")
                    },
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
                        usersViewModel.signOutUser()
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Sign out button"
                        )
                    }
                )
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
                    }
                )
            }
        ) {
            paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(text = "Statistics", fontSize = 32.sp)

                val primaryColor = MaterialTheme.colorScheme.primary
                val secondaryColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                if(workDuration == 0L){
                    Column(
                    modifier = Modifier.fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally, // Aligns content horizontally
                    verticalArrangement = Arrangement.Center // Aligns content vertically within the column
                )
                    {
                        Spacer(modifier = Modifier.height(24.dp))

                        Text(text = "", fontSize = 16.sp)

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .fillMaxSize(),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Box {
                                Row(
                                    horizontalArrangement = Arrangement.Start,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { isDropDownExpanded.value = true }
                                ) {
                                    Text(text = times[workSessionsState])
                                    Icon(
                                        imageVector = Icons.Filled.ArrowDropDown,
                                        contentDescription = "DropDown Icon"
                                    )
                                }
                                DropdownMenu(
                                    expanded = isDropDownExpanded.value,
                                    onDismissRequest = { isDropDownExpanded.value = false }) {
                                    times.forEachIndexed { index, time ->
                                        DropdownMenuItem(
                                            text = { Text(text = time) },
                                            onClick = {
                                                isDropDownExpanded.value = false
                                                if(index == 0){
                                                    workSessionViewModel.setSessionsForWeek(user!!.email)
                                                }
                                                else if(index == 1){
                                                    workSessionViewModel.setSessionsForThisMonth(user!!.email)
                                                }
                                                else if(index == 2){
                                                    workSessionViewModel.setSessionsForLastMonth(user!!.email)
                                                }
                                                else if(index == 3){
                                                    workSessionViewModel.setSessions(user!!.email)
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "No data",
                                fontSize = 32.sp,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                        }
                    }
                }
                else{
                    Spacer(modifier = Modifier.height(24.dp))

                    Text(text = "Average work and break time", fontSize = 16.sp)

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Box {
                            Row(
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { isDropDownExpanded.value = true }
                            ) {
                                Text(text = times[workSessionsState])
                                Icon(
                                    imageVector = Icons.Filled.ArrowDropDown,
                                    contentDescription = "DropDown Icon"
                                )
                            }
                            DropdownMenu(
                                expanded = isDropDownExpanded.value,
                                onDismissRequest = { isDropDownExpanded.value = false }) {
                                times.forEachIndexed { index, time ->
                                    DropdownMenuItem(
                                        text = { Text(text = time) },
                                        onClick = {
                                            isDropDownExpanded.value = false
                                            if(index == 0){
                                                workSessionViewModel.setSessionsForWeek(user!!.email)
                                            }
                                            else if(index == 1){
                                                workSessionViewModel.setSessionsForThisMonth(user!!.email)
                                            }
                                            else if(index == 2){
                                                workSessionViewModel.setSessionsForLastMonth(user!!.email)
                                            }
                                            else if(index == 3){
                                                workSessionViewModel.setSessions(user!!.email)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                    Canvas(modifier = Modifier.size(200.dp)) {
                        drawArc(
                            color = primaryColor,
                            startAngle = 0f,
                            sweepAngle = workSweepAngle,
                            useCenter = true
                        )
                        drawArc(
                            color = secondaryColor,
                            startAngle = workSweepAngle,
                            sweepAngle = breakSweepAngle,
                            useCenter = true
                        )
                    }
                    Spacer(modifier = Modifier.width(24.dp))

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(color = primaryColor)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Work")
                            Spacer(modifier = Modifier.width(16.dp))
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(color = secondaryColor)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Rest")
                        }
                    }
                    val totalWork: MutableList<Long> = emptyList<Long>().toMutableList()
                    for (work in workSessions){
                        totalWork += work.totalTime
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    Text(text = "Work time over period", fontSize = 16.sp)

                    Spacer(modifier = Modifier.height(8.dp))

                    Canvas(modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(16.dp)) {

                        val reversedWorkSessions = workSessions.reversed()
                        val paddingLeft = 60.dp.toPx() // Extra space for Y-axis labels
                        val paddingBottom = 40.dp.toPx() // Extra space for X-axis labels
                        val chartWidth = size.width - paddingLeft - 16.dp.toPx()
                        val chartHeight = size.height - paddingBottom - 16.dp.toPx()
                        val stepX = chartWidth / (reversedWorkSessions.size - 1).coerceAtLeast(1)
                        val maxDuration = reversedWorkSessions.maxOfOrNull { it.totalTime } ?: 1L
                        val scaleFactor = if (maxDuration >= 3600000L) {
                            maxDuration / 3600000f // Scale to hours
                        } else {
                            maxDuration / 60000f   // Scale to minutes
                        }
                        val timeUnit = if (maxDuration >= 3600000L) "h" else "min"
                        val scaleY = chartHeight / scaleFactor

                        // Threshold for displaying dates vertically
                        val verticalLabelThreshold = 7

                        // Draw axes
                        drawLine(
                            color = Color.Gray,
                            start = Offset(paddingLeft, size.height - paddingBottom),
                            end = Offset(size.width - 16.dp.toPx(), size.height - paddingBottom),
                            strokeWidth = 4f
                        )
                        drawLine(
                            color = Color.Gray,
                            start = Offset(paddingLeft, size.height - paddingBottom),
                            end = Offset(paddingLeft, 16.dp.toPx()),
                            strokeWidth = 4f
                        )
                        val flipped = workSessions.size > 6 // Flip dates vertically if there are too many
                        val labelOffset = if (flipped) 40.dp.toPx() else 20.dp.toPx() // Adjust label offset
                        val dateYOffset = if (flipped) 30.dp.toPx() else 10.dp.toPx() // Adjust date offset

                        // Draw X-axis labels (dates from `workSessions`)
                        reversedWorkSessions.forEachIndexed { index, session ->
                            val x = paddingLeft + index * stepX
                            drawContext.canvas.nativeCanvas.apply {
                                val paint = android.graphics.Paint().apply {
                                    color = android.graphics.Color.BLACK
                                    textSize = 30f
                                    textAlign = android.graphics.Paint.Align.CENTER
                                }

                                if (reversedWorkSessions.size >= verticalLabelThreshold) {
                                    // Draw vertical labels
                                    save()
                                    rotate(-90f, x, size.height - paddingBottom / 2)
                                    drawText(
                                        session.date,
                                        x - 10.dp.toPx(),
                                        size.height - paddingBottom / 2,
                                        paint
                                    )
                                    restore()
                                } else {
                                    // Draw horizontal labels
                                    drawText(
                                        session.date,
                                        x,
                                        size.height - paddingBottom / 2,
                                        paint
                                    )
                                }
                            }
                        }

                        // Draw Y-axis labels (time units)
                        val ySteps = 5 // Number of divisions on Y-axis
                        for (i in 0..ySteps) {
                            val yValue = (i * maxDuration / ySteps / if (timeUnit == "h") 3600000f else 60000f).toInt()
                            val y = size.height - paddingBottom - (i * chartHeight / ySteps)

                            drawContext.canvas.nativeCanvas.apply {
                                drawText(
                                    "$yValue $timeUnit",
                                    paddingLeft - 10.dp.toPx(),
                                    y,
                                    android.graphics.Paint().apply {
                                        color = android.graphics.Color.BLACK
                                        textSize = 30f
                                        textAlign = android.graphics.Paint.Align.RIGHT
                                    }
                                )
                            }
                        }

                        // Draw data points and lines
                        var previousPoint: Offset? = null
                        reversedWorkSessions.forEachIndexed { index, session ->
                            val x = paddingLeft + index * stepX
                            val y = size.height - paddingBottom - ((session.totalTime / if (timeUnit == "h") 3600000f else 60000f) * scaleY)

                            // Draw line to previous point
                            previousPoint?.let {
                                drawLine(
                                    color = primaryColor,
                                    start = it,
                                    end = Offset(x, y),
                                    strokeWidth = 4f
                                )
                            }
                            previousPoint = Offset(x, y)

                            // Draw point
                            drawCircle(
                                color = primaryColor,
                                center = Offset(x, y),
                                radius = 6f
                            )
                        }

                        // Add axis legends
                        drawContext.canvas.nativeCanvas.apply {
                            // X-axis label
                            if(flipped){
                                drawText(
                                    "Dates",
                                    20.dp.toPx(),
                                    size.height,
                                    android.graphics.Paint().apply {
                                        color = android.graphics.Color.BLACK
                                        textSize = 40f
                                        textAlign = android.graphics.Paint.Align.CENTER
                                    }
                                )
                            }
                            else{
                                drawText(
                                    "Dates",
                                    size.width / 2,
                                    size.height,
                                    android.graphics.Paint().apply {
                                        color = android.graphics.Color.BLACK
                                        textSize = 40f
                                        textAlign = android.graphics.Paint.Align.CENTER
                                    }
                                )
                            }


                            // Y-axis label
                            drawContext.canvas.nativeCanvas.apply {
                                save()
                                rotate(-90f, paddingLeft / 4, size.height / 2) // Adjust rotation pivot
                                drawText(
                                    "Time",
                                    paddingLeft / 4, // Shift it closer to the Y-axis
                                    size.height / 2,
                                    android.graphics.Paint().apply {
                                        color = android.graphics.Color.BLACK
                                        textSize = 40f
                                        textAlign = android.graphics.Paint.Align.CENTER
                                    }
                                )
                                restore()
                            }
                        }
                    }
                }
            }
        }
    }
}