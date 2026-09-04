package com.repolenspro.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.repolenspro.core.ui.components.ErrorStateItem as CoreErrorStateItem

@Composable
fun ErrorStateItem(
    modifier: Modifier = Modifier,
    message: String,
    onRetry: () -> Unit
) {
    CoreErrorStateItem(
        modifier = modifier,
        message = message,
        onRetry = onRetry
    )
}
