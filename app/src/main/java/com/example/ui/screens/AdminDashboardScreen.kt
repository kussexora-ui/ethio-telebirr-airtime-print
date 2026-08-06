package com.example.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.IdCardEntity
import com.example.data.db.KebeleStampEntity
import com.example.data.db.OfficialSignatureEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.EthioIdViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: EthioIdViewModel,
    cards: List<IdCardEntity>,
    stamps: List<KebeleStampEntity>,
    signatures: List<OfficialSignatureEntity>,
    onPreviewCard: (String) -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Orders, 1: Seals, 2: Signatures, 3: Locations

    var showAddStampDialog by remember { mutableStateOf(false) }
    var showAddSignatureDialog by remember { mutableStateOf(false) }
    var showBroadcastDialog by remember { mutableStateOf(false) }

    val pendingCards = cards.filter { it.paymentStatus == "PENDING" }
    val approvedCards = cards.filter { it.paymentStatus == "APPROVED" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground)
    ) {
        // Admin Profile Banner Header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = EthioNavy)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(EthioRed),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin", tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Gezahegn Gelebo Alemayehu",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "የመታወቂያ ስርዓት ባለቤትና ዋና አስተዳዳሪ (Super Admin)",
                                fontSize = 11.sp,
                                color = EthioYellow
                            )
                        }
                    }

                    Button(
                        onClick = { showBroadcastDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = EthioGreen),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Campaign, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "ለደንበኞች መልዕክት ላክ",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Control Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = EthioNavy,
            edgePadding = 16.dp
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("የትዕዛዝ መቆጣጠሪያ (${pendingCards.size})", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.ShoppingBag, contentDescription = "Orders") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("የማህተም መቆጣጠሪያ", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Verified, contentDescription = "Stamps") }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("የአስተዳደር ፊርማ", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Draw, contentDescription = "Signatures") }
            )
            Tab(
                selected = selectedTab == 3,
                onClick = { selectedTab = 3 },
                text = { Text("የቀበሌያት ዝርዝር", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.LocationCity, contentDescription = "Locations") }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tab Contents
        when (selectedTab) {
            0 -> OrdersControlTab(
                pendingCards = pendingCards,
                approvedCards = approvedCards,
                onApprove = { cardId ->
                    viewModel.approvePayment(cardId)
                    Toast.makeText(context, "ትዕዛዙ ተፀድቋል! የSAMPLE ማህተም ተነስቷል። ኖቲፊኬሽን ተልኳል።", Toast.LENGTH_LONG).show()
                },
                onReject = { cardId ->
                    viewModel.rejectPayment(cardId, "የክፍያ ትራንዛክሽን ቁጥር አልተረጋገጠም።")
                    Toast.makeText(context, "ትዕዛዙ አልተቀበለም።", Toast.LENGTH_SHORT).show()
                },
                onPreviewCard = onPreviewCard
            )

            1 -> StampsControlTab(
                stamps = stamps,
                onAddStampClick = { showAddStampDialog = true },
                onDeleteStamp = { viewModel.deleteStamp(it) }
            )

            2 -> SignaturesControlTab(
                signatures = signatures,
                onAddSignatureClick = { showAddSignatureDialog = true },
                onDeleteSignature = { viewModel.deleteSignature(it) }
            )

            3 -> LocationsRegistryTab()
        }

        if (showBroadcastDialog) {
            com.example.ui.components.AdminBroadcastDialog(
                onDismiss = { showBroadcastDialog = false },
                onSendBroadcast = { title, body ->
                    viewModel.sendAdminBroadcastNotification(title, body)
                    showBroadcastDialog = false
                    Toast.makeText(context, "ማስታወቂያው ለሁሉም ደንበኞች በስኬት ተልኳል! 📢", Toast.LENGTH_LONG).show()
                }
            )
        }
    }

    // Add Stamp Dialog
    if (showAddStampDialog) {
        var regionInput by remember { mutableStateOf("አዲስ አበባ") }
        var woredaInput by remember { mutableStateOf("ቦሌ ወረዳ 04") }
        var titleInput by remember { mutableStateOf("የቀበሌ 04 አስተዳደር ማህተም") }

        AlertDialog(
            onDismissRequest = { showAddStampDialog = false },
            title = { Text("አዲስ የቀበሌ ማህተም ጨምር (Add Kebele Stamp)") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = regionInput,
                        onValueChange = { regionInput = it },
                        label = { Text("ክልል / ከተማ") }
                    )
                    OutlinedTextField(
                        value = woredaInput,
                        onValueChange = { woredaInput = it },
                        label = { Text("ክፍለ ከተማ / ወረዳ") }
                    )
                    OutlinedTextField(
                        value = titleInput,
                        onValueChange = { titleInput = it },
                        label = { Text("የማህተም ስም") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addStamp(titleInput, woredaInput, regionInput)
                        showAddStampDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EthioGreen)
                ) {
                    Text("መዝግብ (Save)")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddStampDialog = false }) {
                    Text("ሰርዝ")
                }
            }
        )
    }

    // Add Signature Dialog
    if (showAddSignatureDialog) {
        var nameInput by remember { mutableStateOf("ወ/ሮ መሰረት ተስፋዬ") }
        var roleInput by remember { mutableStateOf("የቀበሌው ም/አስተዳዳሪ") }

        AlertDialog(
            onDismissRequest = { showAddSignatureDialog = false },
            title = { Text("አዲስ የአስተዳዳሪ ፊርማ ጨምር (Add Official Signature)") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("የአስተዳዳሪው ሙሉ ስም") }
                    )
                    OutlinedTextField(
                        value = roleInput,
                        onValueChange = { roleInput = it },
                        label = { Text("ኃላፊነት (Role)") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addSignature(nameInput, roleInput)
                        showAddSignatureDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EthioGreen)
                ) {
                    Text("መዝግብ (Save)")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddSignatureDialog = false }) {
                    Text("ሰርዝ")
                }
            }
        )
    }
}

@Composable
private fun OrdersControlTab(
    pendingCards: List<IdCardEntity>,
    approvedCards: List<IdCardEntity>,
    onApprove: (String) -> Unit,
    onReject: (String) -> Unit,
    onPreviewCard: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "የሚጠበቁ ክፍያዎች (Pending Approval Requests)",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = EthioNavy
            )
        }

        if (pendingCards.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Text(
                        text = "ምንም የሚጠበቅ የክፍያ ጥያቄ የለም። ሁሉም ተፀድቀዋል። 👍",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(pendingCards) { card ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EthioYellow)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = card.fullNameAmharic,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EthioNavy
                                )
                                Text(
                                    text = "ID: ${card.idNumber} • ${if (card.packageType == "HARDCOPY_PRINT") "400 ETB Print" else "50 ETB Digital"}",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }

                            Surface(
                                color = EthioYellow.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "በምርመራ ላይ",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFB45309),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Transaction Proof Info Box
                        Surface(
                            color = Color(0xFFF1F5F9),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = "የትራንዛክሽን ቁጥር: ${card.transactionRef}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EthioNavy
                                )
                                Text(
                                    text = "የክፍያ መንገድ: ${card.paymentMethod}",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { onApprove(card.id) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = EthioGreen),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Approve")
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("አፅድቅ (Approve)", fontSize = 12.sp)
                            }

                            OutlinedButton(
                                onClick = { onPreviewCard(card.id) },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Visibility, contentDescription = "View")
                            }

                            Button(
                                onClick = { onReject(card.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = EthioRed),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("ሰርዝ", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "የተፀደቁ የመታወቂያ እድሳቶች (${approvedCards.size})",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = EthioNavy
            )
        }

        items(approvedCards) { card ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onPreviewCard(card.id) },
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(card.fullNameAmharic, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("${card.idNumber} • Expiry: ${card.expiryDate}", fontSize = 11.sp, color = TextSecondary)
                    }

                    Icon(Icons.Default.Verified, contentDescription = "Approved", tint = EthioGreen)
                }
            }
        }
    }
}

@Composable
private fun StampsControlTab(
    stamps: List<KebeleStampEntity>,
    onAddStampClick: () -> Unit,
    onDeleteStamp: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "የቀበሌያት ማህተም መቆጣጠሪያ (PNG Seals)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = EthioNavy
                )

                Button(
                    onClick = onAddStampClick,
                    colors = ButtonDefaults.buttonColors(containerColor = EthioGreen),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("አዲስ ማህተም")
                }
            }
        }

        items(stamps) { stamp ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(EthioNavy.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Verified, contentDescription = "Seal", tint = EthioNavy)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(stamp.stampTitle, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("${stamp.regionName} • ${stamp.woredaKebele}", fontSize = 11.sp, color = TextSecondary)
                        }
                    }

                    if (!stamp.isDefault) {
                        IconButton(onClick = { onDeleteStamp(stamp.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = EthioRed)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SignaturesControlTab(
    signatures: List<OfficialSignatureEntity>,
    onAddSignatureClick: () -> Unit,
    onDeleteSignature: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "የአስተዳደር ስምና ፊርማ (Signatures)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = EthioNavy
                )

                Button(
                    onClick = onAddSignatureClick,
                    colors = ButtonDefaults.buttonColors(containerColor = EthioNavy),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("አዲስ ፊርማ")
                }
            }
        }

        items(signatures) { sig ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(sig.officialName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EthioNavy)
                        Text(sig.officialRole, fontSize = 11.sp, color = TextSecondary)
                    }

                    if (!sig.isDefault) {
                        IconButton(onClick = { onDeleteSignature(sig.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = EthioRed)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LocationsRegistryTab() {
    val regions = listOf("አዲስ አበባ", "ሲዳማ ክልል", "ኦሮሚያ ክልል", "አማራ ክልል", "ደቡብ ኢትዮጵያ", "ድሬዳዋ", "ሐረሪ")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "የቀበሌያትና የክልሎች ዝርዝር (Locations Registry)",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = EthioNavy
            )
        }

        items(regions) { reg ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Location", tint = EthioGreen)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(reg, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    Text("ንቁ (Active)", fontSize = 11.sp, color = EthioGreen, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
