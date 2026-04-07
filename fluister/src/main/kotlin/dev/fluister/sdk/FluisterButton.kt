package dev.fluister.sdk

import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Ready-to-use floating action button that shows the Fluister feedback sheet
 */
@Composable
fun FluisterFeedbackButton(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    FloatingActionButton(
        onClick = { Fluister.show(context) },
        containerColor = Color(0xFF667EEA),
        contentColor = Color.White,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 6.dp
        ),
        modifier = modifier.size(56.dp)
    ) {
        Text(
            text = "💬",
            fontSize = 24.sp
        )
    }
}
