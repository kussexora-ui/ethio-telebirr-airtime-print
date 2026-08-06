package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.data.db.IdCardEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.EthioIdViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrScannerScreen(
    viewModel: EthioIdViewModel,
    cards: List<IdCardEntity>,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var inputCode by remember { mutableStateOf("") }
    var scannedCardResult by remember { mutableStateOf<IdCardEntity?>(null) }
    var isScanned by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text(
                    text = "የQR ኮድ ማረጋገጫ (Security QR Scanner)",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = EthioNavy
                )
            }
        }

        // Camera Simulation Viewfinder Box
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(160.dp)
                                .border(2.dp, EthioGreen, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = "Scan Viewfinder",
                                tint = EthioYellow,
                                modifier = Modifier.size(64.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "ካሜራውን የመታወቂያው QR ኮድ ላይ ያድርጉ",
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Manual Verification Input
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "በመታወቂያ ቁጥር ማረጋገጥ (Verify by ID No.)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = EthioNavy
                    )

                    OutlinedTextField(
                        value = inputCode,
                        onValueChange = { inputCode = it },
                        label = { Text("የመታወቂያ ቁጥር ወይም QR Code Payload") },
                        placeholder = { Text("e.g. ADDIS-2026-8841") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Button(
                        onClick = {
                            val match = cards.find {
                                it.idNumber.contains(inputCode, ignoreCase = true) ||
                                it.id.contains(inputCode, ignoreCase = true) ||
                                (inputCode.isNotBlank() && it.fullNameEnglish.contains(inputCode, ignoreCase = true))
                            } ?: cards.firstOrNull()

                            scannedCardResult = match
                            isScanned = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EthioGreen),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Verified, contentDescription = "Verify")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("እውነተኛነቱን አረጋግጥ (Verify Authenticity)")
                    }
                }
            }
        }

        // Verification Result Output
        if (isScanned) {
            item {
                val card = scannedCardResult
                if (card != null && card.paymentStatus == "APPROVED") {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = EthioGreen.copy(alpha = 0.12f)),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, EthioGreen)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.VerifiedUser, contentDescription = "Valid", tint = EthioGreen, modifier = Modifier.size(28.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("እውነተኛ ህጋዊ መታወቂያ! (VERIFIED GENUINE ID)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EthioGreen)
                                    Text("በህጋዊ የቀበሌ አስተዳደር የተረጋገጠ", fontSize = 11.sp, color = TextPrimary)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(thickness = 1.dp, color = EthioGreen.copy(alpha = 0.3f))
                            Spacer(modifier = Modifier.height(10.dp))

                            Text("ባለቤት: ${card.fullNameAmharic} (${card.fullNameEnglish})", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("የመታወቂያ ቁጥር: ${card.idNumber}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("አድራሻ: ${card.region} • ${card.zoneSubCity} • ${card.woreda}", fontSize = 11.sp)
                            Text("የሚያበቃበት ቀን: ${card.expiryDate} (+1 Year Rule)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EthioNavy)
                        }
                    }
                } else if (card != null && card.paymentStatus != "APPROVED") {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = EthioYellow.copy(alpha = 0.2f)),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFD97706))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, contentDescription = "Pending", tint = Color(0xFFD97706), modifier = Modifier.size(28.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("ያልተረጋገጠ / በምርመራ ላይ ያለ መታወቂያ", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("ይህ መታወቂያ የSAMPLE ማህተም ያለበት ሲሆን በSuper Admin Gezahegn Gelebo አልፀደቀም።", fontSize = 11.sp, color = TextPrimary)
                        }
                    }
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = EthioRed.copy(alpha = 0.12f)),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, EthioRed)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("⚠️ አልተገኘም (INVALID / UNREGISTERED ID)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EthioRed)
                            Text("በስርዓቱ ውስጥ ያልተመዘገበ ህገ-ወጥ ወይም የተሳሳተ መታወቂያ ነው።", fontSize = 11.sp, color = TextPrimary)
                        }
                    }
                }
            }
        }
    }
}
