package dev.fluister.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.fluister.sdk.Fluister
import dev.fluister.sdk.FluisterFeedbackButton
import dev.fluister.sdk.FluisterFeedbackSheet

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Set screen name for context
        Fluister.setScreen("MainActivity")
        
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SampleScreen()
                    
                    // Include the feedback sheet
                    FluisterFeedbackSheet()
                }
            }
        }
    }
}

@Composable
fun SampleScreen() {
    val context = LocalContext.current
    
    Scaffold(
        floatingActionButton = {
            FluisterFeedbackButton()
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Fluister SDK Demo",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Tap the floating button to give feedback",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Alternative: Button trigger
            Button(
                onClick = { Fluister.show(context) }
            ) {
                Text("Give Feedback")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedButton(
                onClick = {
                    Fluister.setUserEmail("demo@example.com")
                    Fluister.show(context)
                }
            ) {
                Text("Give Feedback (with email)")
            }
        }
    }
}
