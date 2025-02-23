package components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicTooltip(
    text: String,
    modifier: Modifier = Modifier,
    content: @Composable (() -> Unit)
) {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        tooltip = {
            PlainTooltip {
                Text(text)
            }
        },
        state = rememberTooltipState(),
        modifier = modifier.pointerHoverIcon(PointerIcon.Hand)
    ) {
        content()
    }
}
