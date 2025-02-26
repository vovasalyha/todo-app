package components

import LocalPadding
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector

data class FloatingMenuItem(
    val icon: ImageVector,
    val tooltipText: String,
    val onClick: () -> Unit
)

@Composable
fun ExpandableFloatingMenu(
    leftMenuItems: List<FloatingMenuItem>,
    rightMenuItems: List<FloatingMenuItem>
) {
    var isExpanded by remember { mutableStateOf(false) }

    Row(
        horizontalArrangement = Arrangement.spacedBy(LocalPadding.current.medium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Left side buttons
        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn() + slideInHorizontally { -it },
            exit = fadeOut() + slideOutHorizontally { -it }
        ) {
            leftMenuItems.forEach { item -> FloatingMenuButton(item) }
        }

        // Center menu toggling button
        MenuToggleButton(
            icon = Icons.Default.Settings,
            isExpanded = isExpanded,
            onExpandedToggled = { isExpanded = !isExpanded }
        )

        // Right side buttons
        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn() + slideInHorizontally { it },
            exit = fadeOut() + slideOutHorizontally { it }
        ) {
            rightMenuItems.forEach { item -> FloatingMenuButton(item) }
        }
    }
}

@Composable
private fun FloatingMenuButton(item: FloatingMenuItem) {
    BasicTooltip(text = item.tooltipText) {
        FloatingActionButton(
            onClick = item.onClick,
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.tooltipText
            )
        }
    }
}

@Composable
private fun MenuToggleButton(
    icon: ImageVector,
    isExpanded: Boolean,
    onExpandedToggled: () -> Unit
) {
    BasicTooltip(
        text = if (isExpanded) "Close menu" else "Open menu"
    ) {
        val rotation by animateFloatAsState(
            targetValue = if (isExpanded) 180f else 0f,
            animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
        )

        FloatingActionButton(
            onClick = onExpandedToggled,
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "Expandable menu",
                modifier = Modifier.graphicsLayer(rotationZ = rotation)
            )
        }
    }
}
