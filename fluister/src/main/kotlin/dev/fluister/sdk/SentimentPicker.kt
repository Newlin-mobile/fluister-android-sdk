package dev.fluister.sdk

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun SentimentPicker(
    selected: Sentiment?,
    onSelect: (Sentiment) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Sentiment.entries.forEach { sentiment ->
            val isSelected = selected == sentiment
            val scale by animateFloatAsState(
                targetValue = if (isSelected) 1.3f else 1.0f,
                label = "sentiment_scale"
            )
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onSelect(sentiment) }
                    .padding(8.dp)
            ) {
                Text(
                    text = sentiment.emoji,
                    fontSize = 40.sp,
                    modifier = Modifier.scale(scale)
                )
                
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .size(6.dp, 6.dp)
                            .scale(1.0f)
                    ) {
                        Text(
                            text = "●",
                            fontSize = 8.sp,
                            color = Color(0xFF667EEA)
                        )
                    }
                }
            }
        }
    }
}
