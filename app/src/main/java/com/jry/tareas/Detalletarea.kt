package com.jry.tareas

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import kotlinx.coroutines.delay

private const val ANIMATION_DURATION_MS = 100

@Composable
fun TaskDetailScreen(navController: NavController, taskId: Int) {
    val context = LocalContext.current
    val database = remember { TaskDatabase.getDatabase(context) }
    val task by database.taskDao().getTaskById(taskId).collectAsStateWithLifecycle(initialValue = null)

    var isExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(10)
        isExpanded = true
    }


    LaunchedEffect(isExpanded) {
        if (!isExpanded) {
            delay(ANIMATION_DURATION_MS.toLong())
            navController.popBackStack()
        }
    }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    val transition = updateTransition(targetState = isExpanded, label = "expansionTransition")

    val boxColor by transition.animateColor(transitionSpec = { tween(ANIMATION_DURATION_MS) }) {
        if (it) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.secondary
    }
    val contentColor by transition.animateColor(transitionSpec = { tween(ANIMATION_DURATION_MS) }) {
        if (it) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSecondary
    }
    val boxWidth by transition.animateDp(transitionSpec = { tween(ANIMATION_DURATION_MS) }) {
        if (it) screenWidth else 56.dp
    }
    val boxHeight by transition.animateDp(transitionSpec = { tween(ANIMATION_DURATION_MS) }) {
        if (it) screenHeight else 56.dp
    }
    val cornerRadius by transition.animateDp(transitionSpec = { tween(ANIMATION_DURATION_MS) }) {
        if (it) 0.dp else 28.dp
    }
    val boxPadding by transition.animateDp(transitionSpec = { tween(ANIMATION_DURATION_MS) }) {
        if (it) 0.dp else 16.dp
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .statusBarsPadding()
                .padding(start = boxPadding, top = boxPadding)
                .size(width = boxWidth, height = boxHeight)
                .clip(RoundedCornerShape(cornerRadius))
                .background(boxColor)
        ) {
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn(animationSpec = tween(delayMillis = 300)),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding()
                        .padding(16.dp)
                ) {
                    IconButton(onClick = { isExpanded = false }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Atrás",
                            tint = contentColor
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    when (val taskValue = task) {
                        null -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = contentColor)
                        }
                        else -> {
                            Text(
                                text = taskValue.title,
                                style = MaterialTheme.typography.headlineMedium,
                                color = contentColor
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = taskValue.description,
                                style = MaterialTheme.typography.bodyLarge,
                                color = contentColor
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = !isExpanded,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Atrás",
                        tint = MaterialTheme.colorScheme.onSecondary
                    )
                }
            }
        }
    }
}