package com.example.ui.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.EthioIdViewModel
import com.example.util.DateUtils

import com.example.util.LanguageUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewIdFormScreen(
    viewModel: EthioIdViewModel,
    onNavigateToPayment: (String) -> Unit,
    onBack: () -> Unit
) {
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val uiLabels = remember(selectedLanguage) { LanguageUtils.getUiLabels(selectedLanguage.code) }

    val fullNameAmharic by viewModel.formFullNameAmharic.collectAsState()
    val fullNameEnglish by viewModel.formFullNameEnglish.collectAsState()
    val idNumber by viewModel.formIdNumber.collectAsState()
    val gender by viewModel.formGender.collectAsState()
    val dob by viewModel.formDob.collectAsState()
    val phone by viewModel.formPhone.collectAsState()
    val emergencyContact by viewModel.formEmergencyContact.collectAsState()
    val bloodType by viewModel.formBloodType.collectAsState()
    val region by viewModel.formRegion.collectAsState()
    val customHeader by viewModel.formCustomHeader.collectAsState()
    val zoneSubCity by viewModel.formZoneSubCity.collectAsState()
    val woreda by viewModel.formWoreda.collectAsState()
    val kebele by viewModel.formKebele.collectAsState()
    val houseNo by viewModel.formHouseNo.collectAsState()
    val renewalDate by viewModel.formRenewalDate.collectAsState()
    val calculatedExpiryDate by viewModel.formCalculatedExpiryDate.collectAsState()
    val packageType by viewModel.formPackageType.collectAsState()

    var isSubmitting by remember { mutableStateOf(false) }

    val regionList = listOf("አዲስ አበባ", "ሲዳማ ክልል", "ኦሮሚያ ክልል", "አማራ ክልል", "ደቡብ ኢትዮጵያ", "ድሬዳዋ", "ሐረሪ")
    val genderOptions = listOf("ወንድ", "ሴት")
    val bloodTypes = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")

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
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                    Text(
                        text = "${uiLabels.newIdNav} Form",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = EthioNavy
                    )
                }
            }
        }

        // AI Form Human Error Inspection Banner
        item {
            val validationResult = viewModel.validateFormWithAi()
            com.example.ui.components.AiFormErrorAssistant(
                validationResult = validationResult,
                onAutoFixClick = {
                    viewModel.applyAiFormAutoFix()
                }
            )
        }

        // Section 1: Personal Details
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
                        text = "1. ${uiLabels.fullNameAmharicLabel}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = EthioGreen
                    )

                    // Full Name Amharic/Regional
                    OutlinedTextField(
                        value = fullNameAmharic,
                        onValueChange = { viewModel.formFullNameAmharic.value = it },
                        label = { Text("${uiLabels.fullNameAmharicLabel} *") },
                        placeholder = { Text("e.g. አበበ በቀለ ደስታ") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    // Full Name English
                    OutlinedTextField(
                        value = fullNameEnglish,
                        onValueChange = { viewModel.formFullNameEnglish.value = it },
                        label = { Text("${uiLabels.fullNameEnglishLabel} *") },
                        placeholder = { Text("e.g. Abebe Bekele Desta") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    // ID Number (or Auto Generated)
                    OutlinedTextField(
                        value = idNumber,
                        onValueChange = { viewModel.formIdNumber.value = it },
                        label = { Text("የመታወቂያ ቁጥር (ID Number)") },
                        placeholder = { Text("ባዶ ቢተው በራስ-ሰር ይፈጠራል (e.g. ADDIS-2026-9812)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    // Gender Selector Row
                    Text("ፆታ (Sex):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        genderOptions.forEach { option ->
                            FilterChip(
                                selected = (gender == option),
                                onClick = { viewModel.formGender.value = option },
                                label = { Text(option) },
                                leadingIcon = {
                                    if (gender == option) {
                                        Icon(Icons.Default.Check, contentDescription = "Selected", modifier = Modifier.size(16.dp))
                                    }
                                }
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Date of Birth
                        OutlinedTextField(
                            value = dob,
                            onValueChange = { viewModel.formDob.value = it },
                            label = { Text("የትውልድ ቀን") },
                            placeholder = { Text("15/08/1997") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )

                        // Blood Type
                        OutlinedTextField(
                            value = bloodType,
                            onValueChange = { viewModel.formBloodType.value = it },
                            label = { Text("የደም ዓይነት") },
                            placeholder = { Text("O+") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    // Phone & Emergency Contact
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { viewModel.formPhone.value = it },
                        label = { Text("ስልክ ቁጥር (Phone Number) *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = emergencyContact,
                        onValueChange = { viewModel.formEmergencyContact.value = it },
                        label = { Text("የቅርብ ተጠሪ ስም እና ስልክ (Emergency Contact)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }
        }

        // Section 2: Address Details
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
                        text = "2. የቀበሌ አድራሻ እና የመታወቂያ ርዕስ (Address & Custom Header)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = EthioGreen
                    )

                    // Custom ID Card Header Title
                    Column {
                        Text(
                            text = "የመታወቂያ ርዕስ / የክልልና ዞን ስም (Custom ID Header Title) *",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = EthioNavy
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = customHeader,
                            onValueChange = { viewModel.formCustomHeader.value = it },
                            placeholder = { Text("e.g. በደቡብ ኢትዮጵያ ክልላዊ መንግስት በኮንሶ ዞን የካራት ከተማ አስተዳደር ነዋሪዎች መታወቂያ") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2,
                            shape = RoundedCornerShape(10.dp)
                        )
                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                        ) {
                            SuggestionChip(
                                onClick = {
                                    viewModel.formCustomHeader.value = "በደቡብ ኢትዮጵያ ክልላዊ መንግስት በኮንሶ ዞን የካራት ከተማ አስተዳደር ነዋሪዎች መታወቂያ"
                                },
                                label = { Text("ደቡብ ኢትዮጵያ / ኮንሶ ዞን ምሳሌ", fontSize = 10.5.sp) },
                                icon = { Icon(Icons.Default.BorderColor, contentDescription = null, modifier = Modifier.size(12.dp)) }
                            )
                        }
                    }

                    // Region Selector Chips
                    Text("ክልል / ከተማ አስተዳደር (Region):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                    ) {
                        item {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                regionList.forEach { reg ->
                                    FilterChip(
                                        selected = (region == reg),
                                        onClick = { viewModel.formRegion.value = reg },
                                        label = { Text(reg) }
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = zoneSubCity,
                            onValueChange = { viewModel.formZoneSubCity.value = it },
                            label = { Text("ክፍለ ከተማ / ዞን") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = woreda,
                            onValueChange = { viewModel.formWoreda.value = it },
                            label = { Text("ወረዳ") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = kebele,
                            onValueChange = { viewModel.formKebele.value = it },
                            label = { Text("ቀበሌ") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = houseNo,
                            onValueChange = { viewModel.formHouseNo.value = it },
                            label = { Text("የቤት ቁጥር") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }
        }

        // Section 3: Automated Expiration Date (+1 Year Rule)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = EthioNavy.copy(alpha = 0.05f)),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, EthioGreen)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Event, contentDescription = "Expiration", tint = EthioGreen)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "3. የእድሳት እና የማብቂያ ቀን (+1 Year Rule)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = EthioNavy
                        )
                    }

                    OutlinedTextField(
                        value = renewalDate,
                        onValueChange = { viewModel.updateRenewalDate(it) },
                        label = { Text("የታደሰበት ቀን (Renewal Date: YYYY-MM-DD)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        trailingIcon = {
                            Icon(Icons.Default.CalendarToday, contentDescription = "Calendar")
                        }
                    )

                    // Automated Expiration Highlight Box
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFDC2626).copy(alpha = 0.1f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDC2626))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "በራስ-ሰር የተሰላ የማብቂያ ቀን (+1 ዓመት):",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF991B1B)
                                )
                                Text(
                                    text = calculatedExpiryDate,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF991B1B)
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.AutoMode,
                                contentDescription = "Auto Calculated",
                                tint = Color(0xFF991B1B),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
        }

        // Section 4: Package Selection
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "4. የአገልግሎት ፓኬጅ ይምረጡ (Select Service Package)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = EthioGreen
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Digital Copy (50 ETB)
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.formPackageType.value = "DIGITAL_COPY" }
                                .border(
                                    width = if (packageType == "DIGITAL_COPY") 2.dp else 1.dp,
                                    color = if (packageType == "DIGITAL_COPY") EthioGreen else Color(0xFFCBD5E1),
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            colors = CardDefaults.cardColors(
                                containerColor = if (packageType == "DIGITAL_COPY") EthioGreen.copy(alpha = 0.1f) else Color.White
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("ዲጂታል ኮፒ", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("50 ETB", fontSize = 16.sp, fontWeight = FontWeight.Black, color = EthioGreen)
                                Text("PDF & JPG Download", fontSize = 10.sp, color = TextSecondary)
                            }
                        }

                        // Hardcopy Print (400 ETB)
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.formPackageType.value = "HARDCOPY_PRINT" }
                                .border(
                                    width = if (packageType == "HARDCOPY_PRINT") 2.dp else 1.dp,
                                    color = if (packageType == "HARDCOPY_PRINT") EthioNavy else Color(0xFFCBD5E1),
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            colors = CardDefaults.cardColors(
                                containerColor = if (packageType == "HARDCOPY_PRINT") EthioNavy.copy(alpha = 0.1f) else Color.White
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("የታተመ ህትመት", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("400 ETB", fontSize = 16.sp, fontWeight = FontWeight.Black, color = EthioNavy)
                                Text("Laminated Hardcopy", fontSize = 10.sp, color = TextSecondary)
                            }
                        }
                    }
                }
            }
        }

        // Submit Button
        item {
            Button(
                onClick = {
                    isSubmitting = true
                    viewModel.submitNewIdApplication { newCardId ->
                        isSubmitting = false
                        onNavigateToPayment(newCardId)
                    }
                },
                enabled = !isSubmitting,
                colors = ButtonDefaults.buttonColors(containerColor = EthioGreen),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Icon(Icons.Default.ArrowForward, contentDescription = "Submit")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ወደ ክፍያ ቀጥል (Proceed to Payment)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
