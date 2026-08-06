package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.IdCardEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.EthioIdViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    viewModel: EthioIdViewModel,
    card: IdCardEntity?,
    onPaymentSubmitted: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val paymentMethod by viewModel.paymentMethod.collectAsState()
    val transactionRef by viewModel.transactionRef.collectAsState()
    val receiptUri by viewModel.receiptUri.collectAsState()

    val amountEtb = if (card?.packageType == "HARDCOPY_PRINT") 400 else 50

    val cbeAccount = "1000087841457"
    val telebirrNo = "0919397995"
    val mpesaNo = "0716357344"
    val merchantName = "Gezahegn Gelebo Alemayehu"

    fun copyToClipboard(text: String, label: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "$label ተገልብጧል (Copied: $text)", Toast.LENGTH_SHORT).show()
    }

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
                    text = "የክፍያ ስርዓት (Payment Verification)",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = EthioNavy
                )
            }
        }

        // Amount Due Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = EthioNavy)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "የሚከፈለው ጠቅላላ ሂሳብ:",
                            fontSize = 12.sp,
                            color = Color(0xFFCBD5E1)
                        )
                        Text(
                            text = "$amountEtb ETB",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = EthioYellow
                        )
                        Text(
                            text = if (amountEtb == 50) "ዲጂታል ኮፒ (PDF/JPG)" else "የታተመ ህትመት (Hardcopy Print)",
                            fontSize = 11.sp,
                            color = Color.White
                        )
                    }

                    Surface(
                        color = EthioGreen,
                        shape = CircleShape,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Payments, contentDescription = "Pay", tint = Color.White)
                        }
                    }
                }
            }
        }

        // Merchant Bank & Telebirr Accounts
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "ህጋዊ ሂሳብ ቁጥሮች (Official Accounts)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = EthioNavy
                    )

                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Person, contentDescription = "Owner", tint = EthioNavy)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "የአካውንት ባለቤት: $merchantName",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = EthioNavy
                            )
                        }
                    }

                    // Account Option 1: CBE
                    AccountCopyRow(
                        title = "1. የኢትዮጵያ ንግድ ባንክ (CBE)",
                        accountNumber = cbeAccount,
                        icon = Icons.Default.AccountBalance,
                        onCopy = { copyToClipboard(cbeAccount, "CBE Account") }
                    )

                    // Account Option 2: Telebirr
                    AccountCopyRow(
                        title = "2. ቴሌብር (Telebirr)",
                        accountNumber = telebirrNo,
                        icon = Icons.Default.PhoneAndroid,
                        onCopy = { copyToClipboard(telebirrNo, "Telebirr Number") }
                    )

                    // Account Option 3: M-Pesa
                    AccountCopyRow(
                        title = "3. M-Pesa",
                        accountNumber = mpesaNo,
                        icon = Icons.Default.Smartphone,
                        onCopy = { copyToClipboard(mpesaNo, "M-Pesa Number") }
                    )
                }
            }
        }

        // Transaction Ref & Screenshot Upload Box
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
                        text = "የክፍያ ማረጋገጫ ማስገቢያ (Payment Proof)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = EthioGreen
                    )

                    OutlinedTextField(
                        value = transactionRef,
                        onValueChange = { viewModel.transactionRef.value = it },
                        label = { Text("የትራንዛክሽን ቁጥር (Transaction Ref No.) *") },
                        placeholder = { Text("ምሳሌ: CBE-99881122 ወይም ቴሌብር TXN") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        leadingIcon = {
                            Icon(Icons.Default.ReceiptLong, contentDescription = "Ref")
                        }
                    )

                    // Payment Screenshot Picker Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF1F5F9))
                            .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(12.dp))
                            .clickable {
                                // Simulate receipt upload image
                                viewModel.receiptUri.value = "simulated_receipt_image_uri"
                                Toast.makeText(context, "የክፍያ ደረሰኝ ስክሪንሾት ተመርጧል (Receipt Attached)", Toast.LENGTH_SHORT).show()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = if (receiptUri != null) Icons.Default.CheckCircle else Icons.Default.AddPhotoAlternate,
                                contentDescription = "Receipt",
                                tint = if (receiptUri != null) EthioGreen else TextSecondary,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (receiptUri != null) "የክፍያ ደረሰኝ ተያይዟል ✔️" else "የክፍያ ደረሰኝ ስክሪንሾት ያያይዙ (Attach Receipt Photo)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (receiptUri != null) EthioGreen else TextSecondary
                            )
                        }
                    }
                }
            }
        }

        // Submit Payment Button
        item {
            Button(
                onClick = {
                    if (card != null) {
                        viewModel.submitPaymentForCard(card.id)
                        Toast.makeText(context, "የክፍያ መረጃው ለአስተዳዳሪው ተልኳል። በጥቂት ደቂቃዎች ይፀድቃል።", Toast.LENGTH_LONG).show()
                        onPaymentSubmitted()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = EthioNavy),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Icon(Icons.Default.Send, contentDescription = "Submit")
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "የክፍያ ማረጋገጫውን ላክ (Submit Payment Proof)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun AccountCopyRow(
    title: String,
    accountNumber: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onCopy: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFF1F5F9))
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = title, tint = EthioNavy, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(text = accountNumber, fontSize = 14.sp, fontWeight = FontWeight.Black, color = EthioNavy)
            }
        }

        IconButton(onClick = onCopy) {
            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = EthioGreen)
        }
    }
}
