package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.IdCardEntity
import com.example.ui.components.KebeleIdCardView
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.EthioIdViewModel

import com.example.util.LanguageUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerHomeScreen(
    viewModel: EthioIdViewModel,
    cards: List<IdCardEntity>,
    onNavigateToNewForm: () -> Unit,
    onNavigateToPreview: (String) -> Unit,
    onNavigateToPayment: (String) -> Unit
) {
    val activeCard = cards.firstOrNull()
    val context = androidx.compose.ui.platform.LocalContext.current
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val uiLabels = remember(selectedLanguage) { LanguageUtils.getUiLabels(selectedLanguage.code) }
    var showAiChatSheet by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(LightBackground)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
        // Hero Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = EthioNavy)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(EthioNavy, EthioNavyLight)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Surface(
                                color = EthioGreen,
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = "Official",
                                        tint = EthioYellow,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = uiLabels.subTitle,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            // Helpline Quick Call Button
                            Surface(
                                color = Color.White.copy(alpha = 0.15f),
                                shape = CircleShape,
                                modifier = Modifier.clickable { }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Phone,
                                        contentDescription = "Helpline",
                                        tint = EthioYellow,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "0912702062",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = uiLabels.createNewIdBtn,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            lineHeight = 26.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "• ${uiLabels.expiryRuleNotice}\n• ${uiLabels.selectPackageLabel}",
                            fontSize = 12.sp,
                            color = Color(0xFFCBD5E1),
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = onNavigateToNewForm,
                            colors = ButtonDefaults.buttonColors(containerColor = EthioGreen),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddCard,
                                contentDescription = "Apply",
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${uiLabels.createNewIdBtn} (Apply Now)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Active ID Card Section
        if (activeCard != null) {
            item {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = uiLabels.appTitle,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = EthioNavy
                        )

                        Surface(
                            color = if (activeCard.paymentStatus == "APPROVED") EthioGreen.copy(alpha = 0.15f) else EthioYellow.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (activeCard.paymentStatus == "APPROVED") "ተፀድቋል (APPROVED)" else "በምርመራ ላይ (PENDING)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (activeCard.paymentStatus == "APPROVED") EthioGreen else Color(0xFFB45309),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    KebeleIdCardView(
                        card = activeCard,
                        localLanguageCode = selectedLanguage.code,
                        showFlipButton = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onNavigateToPreview(activeCard.id) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = "Download")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("አውርድ (Download)")
                        }

                        if (activeCard.paymentStatus != "APPROVED") {
                            Button(
                                onClick = { onNavigateToPayment(activeCard.id) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = EthioNavy),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Payment, contentDescription = "Payment")
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("ክፍያ ፈፅም")
                            }
                        }
                    }
                }
            }
        }

        // Service Pricing Packages
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Sell,
                            contentDescription = "Price",
                            tint = EthioNavy,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "የአገልግሎት ፓኬጆችና ታሪፍ (Pricing Packages)",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = EthioNavy
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Package 1: Digital PDF/JPG (50 ETB)
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, EthioGreen, RoundedCornerShape(12.dp)),
                            colors = CardDefaults.cardColors(containerColor = EthioGreen.copy(alpha = 0.05f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Surface(
                                    color = EthioGreen,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "ዲጂታል ኮፒ",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "50 ETB",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = EthioGreen
                                )
                                Text(
                                    text = "PDF & High-DPI JPG ዲጂታል ኮፒ ወዲያውኑ ማውረድ",
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    lineHeight = 15.sp
                                )
                            }
                        }

                        // Package 2: Hardcopy Print (400 ETB)
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, EthioNavy, RoundedCornerShape(12.dp)),
                            colors = CardDefaults.cardColors(containerColor = EthioNavy.copy(alpha = 0.05f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Surface(
                                    color = EthioNavy,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "የታተመ ህትመት",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "400 ETB",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = EthioNavy
                                )
                                Text(
                                    text = "በላስቲክ የተሸፈነ Hardcopy ID Print እና ማድረሻ",
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Bank Payment Merchant Account Info Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)), // Warm Yellow Highlight
                border = androidx.compose.foundation.BorderStroke(1.dp, EthioYellow)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "💳 ህጋዊ የክፍያ ተቀባይ መረጃ (Merchant Account)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF92400E)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "የአካውንት ባለቤት: Gezahegn Gelebo Alemayehu (አቶ ገዛኸኝ ገለቦ)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• ንግድ ባንክ (CBE): 1000087841457\n• ቴሌብር (Telebirr): 0919397995\n• M-Pesa: 0716357344\n• የእርዳታ መስመር: 0912702062",
                        fontSize = 12.sp,
                        color = Color(0xFF78350F),
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Recent Orders List
        if (cards.size > 1) {
            item {
                Text(
                    text = "የቀደሙ የመታወቂያ እድሳቶች (Past History)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = EthioNavy
                )
            }

            items(cards.drop(1)) { card ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToPreview(card.id) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(EthioNavy.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Badge, contentDescription = "ID", tint = EthioNavy)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = card.fullNameAmharic,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "${card.idNumber} • Expiry: ${card.expiryDate}",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Icon(Icons.Default.ChevronRight, contentDescription = "View", tint = TextSecondary)
                    }
                }
            }
        }
    }

        // Floating AI Support Assistant Button
        ExtendedFloatingActionButton(
            onClick = { showAiChatSheet = true },
            icon = {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = "AI Assistant",
                    tint = EthioYellow
                )
            },
            text = { Text("የAI ጥያቄና መልስ ረዳት", fontWeight = FontWeight.Bold, color = Color.White) },
            containerColor = EthioNavy,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )

        if (showAiChatSheet) {
            com.example.ui.components.AiSupportChatSheet(
                viewModel = viewModel,
                onDismiss = { showAiChatSheet = false }
            )
        }
    }
}
