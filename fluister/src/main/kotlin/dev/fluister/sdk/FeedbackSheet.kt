package dev.fluister.sdk

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FluisterFeedbackSheet() {
    val isShowing by Fluister.isShowing
    
    if (isShowing) {
        val sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = false
        )
        val scope = rememberCoroutineScope()
        
        ModalBottomSheet(
            onDismissRequest = { Fluister.dismiss() },
            sheetState = sheetState
        ) {
            FeedbackSheetContent(
                onDismiss = {
                    scope.launch {
                        sheetState.hide()
                        Fluister.dismiss()
                    }
                }
            )
        }
    }
}

@Composable
private fun FeedbackSheetContent(
    onDismiss: () -> Unit
) {
    var selectedSentiment by remember { mutableStateOf<Sentiment?>(null) }
    var message by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var emailOptIn by remember { mutableStateOf(false) }
    
    var widgetConfig by remember { mutableStateOf<WidgetConfig?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var submitState by remember { mutableStateOf<SubmitState>(SubmitState.Idle) }
    
    val scope = rememberCoroutineScope()
    val api = Fluister.getApi()
    val context = Fluister.getContextData()
    
    // Fetch config on mount
    LaunchedEffect(Unit) {
        api.fetchConfig().onSuccess { config ->
            widgetConfig = config
        }
    }
    
    // Pre-fill email if set
    LaunchedEffect(context.userEmail) {
        context.userEmail?.let { email = it }
    }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = "How are you feeling?",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Sentiment picker
        SentimentPicker(
            selected = selectedSentiment,
            onSelect = { selectedSentiment = it },
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // Message field
        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            placeholder = { Text("Tell us more...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            maxLines = 5
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Email field (conditional)
        if (widgetConfig?.emailFollowupEnabled == true) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email (optional)") },
                placeholder = { Text("your@email.com") },
                modifier = Modifier.fillMaxWidth()
            )
            
            if (email.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = emailOptIn,
                        onCheckedChange = { emailOptIn = it }
                    )
                    Text(
                        text = "I'd like a follow-up",
                        fontSize = 14.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Submit button
        Button(
            onClick = {
                selectedSentiment?.let { sentiment ->
                    isLoading = true
                    submitState = SubmitState.Loading
                    
                    scope.launch {
                        val result = api.submitFeedback(
                            sentiment = sentiment,
                            message = message.ifBlank { null },
                            pageUrl = context.pageUrl,
                            sessionId = context.sessionId,
                            userEmail = email.ifBlank { null },
                            emailFollowupOptedIn = emailOptIn && email.isNotEmpty(),
                            appVersion = context.appVersion
                        )
                        
                        result.onSuccess {
                            submitState = SubmitState.Success
                            kotlinx.coroutines.delay(1000)
                            onDismiss()
                        }.onFailure {
                            submitState = SubmitState.Error(it.message ?: "Unknown error")
                            isLoading = false
                        }
                    }
                }
            },
            enabled = selectedSentiment != null && !isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF667EEA),
                disabledContainerColor = Color(0xFF667EEA).copy(alpha = 0.5f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            when (submitState) {
                SubmitState.Idle -> Text("Submit Feedback")
                SubmitState.Loading -> CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White
                )
                SubmitState.Success -> Text("✓ Sent!")
                is SubmitState.Error -> Text("Try Again")
            }
        }
        
        // Error message
        if (submitState is SubmitState.Error) {
            Text(
                text = (submitState as SubmitState.Error).message,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Powered by
        Text(
            text = "Powered by Fluister",
            fontSize = 12.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

private sealed interface SubmitState {
    object Idle : SubmitState
    object Loading : SubmitState
    object Success : SubmitState
    data class Error(val message: String) : SubmitState
}
