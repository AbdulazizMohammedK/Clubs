package com.devfalah.ui.screen.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.devfalah.ui.screen.notification.composable.AppBar
import com.devfalah.ui.screen.notification.composable.NotificationItem
import com.devfalah.ui.theme.LightBackgroundColor
import com.devfalah.viewmodels.notifications.NotificationState
import com.devfalah.viewmodels.notifications.NotificationsUIState
import com.devfalah.viewmodels.notifications.NotificationsViewModel

@Composable
fun NotificationScreen(
    navController: NavController,
    viewModel: NotificationsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    NotificationContent(state, viewModel::onNotificationClick)
}

@Composable
fun NotificationContent(
    state: NotificationsUIState,
    onNotificationClick: (NotificationState) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(color = LightBackgroundColor),
        contentPadding = PaddingValues(vertical = 16.dp),
    ) {
        items(state.notifications) {
            NotificationItem(notification = it, onNotificationClick)
        }
    }
}

