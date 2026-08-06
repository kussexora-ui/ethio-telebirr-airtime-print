package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.IdCardEntity
import com.example.ui.components.KebeleIdCardView
import com.example.ui.theme.*
import com.example.ui.viewmodel.EthioIdViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IdPreviewScreen(
    viewModel: EthioIdViewModel,
    card: IdCardEntity?,
    onNavigateToPayment: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()

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
                    text = "የመታወቂያ ቅድመ-እይታ (ID Preview & Download)",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = EthioNavy
                )
            }
        }

        if (card == null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("ምንም የተመረጠ መታወቂያ አልተገኘም።", fontSize = 14.sp)
                    }
                }
            }
        } else {
            // Approval Status Alert Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (card.paymentStatus == "APPROVED") EthioGreen.copy(alpha = 0.12f) else EthioYellow.copy(alpha = 0.2f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = if (card.paymentStatus == "APPROVED") EthioGreen else Color(0xFFD97706)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (card.paymentStatus == "APPROVED") Icons.Default.VerifiedUser else Icons.Default.Pending,
                            contentDescription = "Status",
                            tint = if (card.paymentStatus == "APPROVED") EthioGreen else Color(0xFFD97706),
                            modifier = Modifier.size(32.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = if (card.paymentStatus == "APPROVED") "መታወቂያዎ ተፀድቋል! (APPROVED)" else "መታወቂያው በምርመራ ላይ ነው (UNDER REVIEW)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (card.paymentStatus == "APPROVED") EthioGreen else Color(0xFF92400E)
                            )
                            Text(
                                text = if (card.paymentStatus == "APPROVED")
                                    "ያለ ምንም SAMPLE ማህተም ኦፊሴላዊ መታወቂያዎን በPDF ወይም JPG ማውረድ ይችላሉ።"
                                else
                                    "ክፍያዎ በአስተዳዳሪው Gezahegn Gelebo እስከሚፀድቅ ድረስ የSAMPLE ማህተም ይታያል።",
                                fontSize = 11.sp,
                                color = TextPrimary,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }

            // Kebele ID Card Render Component
            item {
                Text(
                    text = "የተዘጋጀው የቀበሌ መታወቂያ (2-Sided Render)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = EthioNavy
                )
                Spacer(modifier = Modifier.height(8.dp))

                KebeleIdCardView(
                    card = card,
                    localLanguageCode = selectedLanguage.code,
                    showFlipButton = true
                )
            }

            // Download & Action Buttons
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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "የማውረጃና የህትመት አማራጮች",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = EthioNavy
                            )

                            Surface(
                                color = if (card.paymentStatus == "APPROVED") EthioGreen.copy(alpha = 0.15f)
                                        else if (card.downloadCount == 0) EthioYellow.copy(alpha = 0.2f)
                                        else EthioRed.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = if (card.paymentStatus == "APPROVED") "ተፀድቋል (UNLIMITED)"
                                           else if (card.downloadCount == 0) "1 ነፃ ሙከራ ይቀራል"
                                           else "ክፍያ ይጠበቃል",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (card.paymentStatus == "APPROVED") EthioGreen
                                           else if (card.downloadCount == 0) Color(0xFFB45309)
                                           else EthioRed,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        if (card.paymentStatus != "APPROVED" && card.downloadCount >= 1) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, EthioRed.copy(alpha = 0.5f)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Warning, contentDescription = "Limit", tint = EthioRed, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "ማስጠንቀቂያ: የ1 ጊዜ ነፃ ሙከራዎ አልቋል! በቀጣይ ለማውረድ ወይም ለህትመት ለማዘዝ እባክዎን ክፍያ ይፈፅሙ።",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EthioRed,
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    if (card.paymentStatus == "APPROVED") {
                                        viewModel.recordCardDownload(card.id)
                                        Toast.makeText(context, "የመታወቂያ PDF ኮፒ ወርዷል (Ethio-ID-${card.idNumber}.pdf Saved)", Toast.LENGTH_LONG).show()
                                    } else if (card.downloadCount == 0) {
                                        viewModel.recordCardDownload(card.id)
                                        Toast.makeText(context, "🎉 የ1 ጊዜ ነፃ ሙከራዎ ተጠቅመዋል! (Ethio-ID-${card.idNumber}.pdf Saved)", Toast.LENGTH_LONG).show()
                                    } else {
                                        Toast.makeText(context, "⛔ የነፃ ሙከራ አልቋል! ለማውረድ ወይም ለህትመት እባክዎን ክፍያ ይፈፅሙ።", Toast.LENGTH_LONG).show()
                                        onNavigateToPayment(card.id)
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (card.paymentStatus == "APPROVED" || card.downloadCount == 0) EthioGreen else Color.Gray
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.PictureAsPdf, contentDescription = "PDF")
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("PDF አውርድ")
                            }

                            OutlinedButton(
                                onClick = {
                                    if (card.paymentStatus == "APPROVED") {
                                        viewModel.recordCardDownload(card.id)
                                        Toast.makeText(context, "High-DPI JPG ምስል ተቀምጧል (Saved)", Toast.LENGTH_LONG).show()
                                    } else if (card.downloadCount == 0) {
                                        viewModel.recordCardDownload(card.id)
                                        Toast.makeText(context, "🎉 የ1 ጊዜ ነፃ ሙከራዎ ተጠቅመዋል! (JPG Saved)", Toast.LENGTH_LONG).show()
                                    } else {
                                        Toast.makeText(context, "⛔ የነፃ ሙከራ አልቋል! ለማውረድ ወይም ለህትመት እባክዎን ክፍያ ይፈፅሙ።", Toast.LENGTH_LONG).show()
                                        onNavigateToPayment(card.id)
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Image, contentDescription = "JPG")
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("JPG ምስል")
                            }
                        }

                        // Telegram Online Print Order Button
                        Button(
                            onClick = {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/nationalidprintandphotoediting"))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "ቴሌግራም መክፈት አልተቻለም: https://t.me/nationalidprintandphotoediting", Toast.LENGTH_LONG).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0088CC)), // Telegram Blue
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Telegram Order", tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("ለህትመት ወይም ፎቶ ለማስተካከያ በቴሌግራም ይዘዙ (Telegram Order)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        if (card.paymentStatus != "APPROVED") {
                            Button(
                                onClick = { onNavigateToPayment(card.id) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = EthioNavy),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Payment, contentDescription = "Pay")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("ክፍያ ፈፅም / ደረሰኝ ላክ (Complete Payment)", fontSize = 13.sp)
                            }
                        }
                    }
                }
            }

            // Direct Helpline Call Box
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF93C5FD))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "እርዳታ ይፈልጋሉ?",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = EthioNavy
                            )
                            Text(
                                text = "የቀጥታ ስልክ መስመር: 0912702062",
                                fontSize = 12.sp,
                                color = Color(0xFF1D4ED8)
                            )
                        }

                        IconButton(onClick = { }) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = "Call",
                                tint = EthioGreen,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
