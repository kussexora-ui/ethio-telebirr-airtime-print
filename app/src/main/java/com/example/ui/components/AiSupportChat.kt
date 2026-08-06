package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.ChatMessage
import com.example.ui.viewmodel.EthioIdViewModel
import com.example.ui.viewmodel.FormAiValidationResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiSupportChatSheet(
    viewModel: EthioIdViewModel,
    onDismiss: () -> Unit
) {
    val chatMessages by viewModel.chatMessages.collectAsState()
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(EthioNavy),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SmartToy,
                            contentDescription = "AI Assistant",
                            tint = EthioYellow,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "የኢትዮጵያ ዲጂታል መታወቂያ AI ረዳት",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = EthioNavy
                        )
                        Text(
                            text = "AI Customer Support Assistant",
                            fontSize = 11.sp,
                            color = EthioGreen
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            // Quick Questions Chips
            Text("የተለመዱ ጥያቄዎች (Quick Suggestions):", fontSize = 11.5.sp, color = Color.Gray)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                AssistChip(
                    onClick = { viewModel.sendChatMessage("አፑን እንዴት አወርዳለሁ?") },
                    label = { Text("አፑን ማውረድ", fontSize = 11.sp) },
                    leadingIcon = { Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp)) }
                )
                AssistChip(
                    onClick = { viewModel.sendChatMessage("የመታወቂያ ክፍያ ስንት ነው?") },
                    label = { Text("ክፍያና ዋጋ", fontSize = 11.sp) },
                    leadingIcon = { Icon(Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(14.dp)) }
                )
                AssistChip(
                    onClick = { viewModel.sendChatMessage("የእድሳት ጊዜ ስንት ነው?") },
                    label = { Text("የ+1 ዓመት እድሳት", fontSize = 11.sp) },
                    leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(14.dp)) }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Message List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(chatMessages) { msg ->
                    ChatMessageBubble(msg = msg)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Message Input
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("ጥያቄዎን እዚህ ይጻፉ...", fontSize = 13.sp) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                FloatingActionButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            viewModel.sendChatMessage(inputText)
                            inputText = ""
                        }
                    },
                    containerColor = EthioNavy,
                    contentColor = Color.White,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send", modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
fun ChatMessageBubble(msg: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (msg.isUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            color = if (msg.isUser) EthioGreen.copy(alpha = 0.9f) else EthioNavy.copy(alpha = 0.08f),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (msg.isUser) 16.dp else 2.dp,
                bottomEnd = if (msg.isUser) 2.dp else 16.dp
            ),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = msg.text,
                    fontSize = 13.sp,
                    color = if (msg.isUser) Color.White else EthioNavy,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = msg.timestamp,
                    fontSize = 9.sp,
                    color = if (msg.isUser) Color.White.copy(alpha = 0.7f) else Color.Gray,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminBroadcastDialog(
    onDismiss: () -> Unit,
    onSendBroadcast: (title: String, body: String) -> Unit
) {
    var notifTitle by remember { mutableStateOf("የመታወቂያ አገልግሎት ማስታወቂያ 📢") }
    var notifBody by remember { mutableStateOf("ለተከበራችሁ ደንበኞች፡ የቀበሌ መታወቂያ ክፍያ በቴሌብር እና በCBE በማጠናቀቅ የ+1 ዓመት እድሳት በደቂቃዎች ውስጥ ያግኙ።") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Campaign, contentDescription = "Broadcast", tint = EthioNavy)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("ለደንበኞች መልዕክት መላኪያ", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = EthioNavy)
                    Text("Send Broadcast Announcement to All Users", fontSize = 10.5.sp, color = Color.Gray)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = notifTitle,
                    onValueChange = { notifTitle = it },
                    label = { Text("የመልዕክቱ ርዕስ (Title)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                OutlinedTextField(
                    value = notifBody,
                    onValueChange = { notifBody = it },
                    label = { Text("የመልዕክቱ ዝርዝር (Message Body)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(8.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (notifTitle.isNotBlank() && notifBody.isNotBlank()) {
                        onSendBroadcast(notifTitle, notifBody)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = EthioNavy)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("መልዕክት ላክ (Send Broadcast)", color = Color.White)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("ሰርዝ", color = Color.Gray)
            }
        }
    )
}

@Composable
fun AiFormErrorAssistant(
    validationResult: FormAiValidationResult,
    onAutoFixClick: () -> Unit
) {
    if (!validationResult.isValid || validationResult.suggestions.isNotEmpty()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = EthioYellow.copy(alpha = 0.12f)),
            border = BorderStroke(1.dp, EthioYellow)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoFixHigh,
                        contentDescription = "AI Fix",
                        tint = EthioNavy,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "የሰው ስህተት ማረሚያ (AI Assistant Inspector)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = EthioNavy
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                validationResult.errors.forEach { err ->
                    Row(
                        modifier = Modifier.padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = EthioRed, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = err, fontSize = 12.sp, color = EthioRed, fontWeight = FontWeight.SemiBold)
                    }
                }

                validationResult.suggestions.forEach { sug ->
                    Row(
                        modifier = Modifier.padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Lightbulb, contentDescription = null, tint = EthioNavy, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = sug, fontSize = 11.5.sp, color = EthioNavy)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onAutoFixClick,
                    colors = ButtonDefaults.buttonColors(containerColor = EthioNavy),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoFixNormal, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("በAI ስህተቶችን በራስ-ሰር አስተካክል (Auto-Fix Form Errors)", fontSize = 12.sp, color = Color.White)
                    }
                }
            }
        }
    }
}
