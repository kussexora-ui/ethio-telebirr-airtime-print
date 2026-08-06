package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.EthioIdViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginAuthScreen(
    viewModel: EthioIdViewModel,
    onCustomerLoginSuccess: () -> Unit,
    onAdminLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    var selectedAuthTab by remember { mutableIntStateOf(0) } // 0: Customer, 1: Admin

    // Customer Login State
    var customerPhoneOrEmail by remember { mutableStateOf("") }
    var customerPin by remember { mutableStateOf("") }
    var isPinVisible by remember { mutableStateOf(false) }

    // Admin Login State
    var adminEmailInput by remember { mutableStateOf("") }
    var adminPasswordInput by remember { mutableStateOf("") }
    var isAdminPassVisible by remember { mutableStateOf(false) }

    // Register Customer Dialog State
    var showRegisterDialog by remember { mutableStateOf(false) }
    var regFullName by remember { mutableStateOf("") }
    var regContact by remember { mutableStateOf("") }
    var regPin by remember { mutableStateOf("") }
    var regOtpInput by remember { mutableStateOf("") }
    var isRegOtpSent by remember { mutableStateOf(false) }

    // Forgot Password/PIN Dialog State
    var showForgotPinDialog by remember { mutableStateOf(false) }
    var forgotContact by remember { mutableStateOf("") }
    var forgotOtpInput by remember { mutableStateOf("") }
    var forgotNewPin by remember { mutableStateOf("") }
    var isForgotOtpSent by remember { mutableStateOf(false) }

    val adminLoginError by viewModel.adminLoginError.collectAsState()
    val otpMessageBanner by viewModel.otpMessageBanner.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // OTP Live Banner Notice
        if (otpMessageBanner != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = EthioYellow)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.MarkEmailUnread, contentDescription = "OTP", tint = EthioNavy)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = otpMessageBanner ?: "",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = EthioNavy,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { viewModel.clearOtpBanner() }) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = EthioNavy)
                    }
                }
            }
        }
        // Header Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = EthioNavy)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(EthioYellow)
                        .border(2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Shield Logo",
                        tint = EthioNavy,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "የኢትዮጵያ ዲጂታል መታወቂያ",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Secure Mobile Account Login",
                    fontSize = 11.sp,
                    color = EthioYellow
                )
            }
        }

        // Main Login Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Tab switch: Customer vs Admin
                TabRow(
                    selectedTabIndex = selectedAuthTab,
                    containerColor = Color(0xFFF1F5F9),
                    contentColor = EthioNavy
                ) {
                    Tab(
                        selected = selectedAuthTab == 0,
                        onClick = { selectedAuthTab = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Person, contentDescription = "Customer", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("የደንበኛ መግቢያ", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                            }
                        }
                    )
                    Tab(
                        selected = selectedAuthTab == 1,
                        onClick = { selectedAuthTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin", modifier = Modifier.size(16.dp), tint = EthioRed)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("የአስተዳዳሪ መግቢያ", fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = EthioRed)
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (selectedAuthTab == 0) {
                    // CUSTOMER LOGIN TAB
                    Text(
                        text = "በስልክ ቁጥር ወይም በጂሜይል ይግቡ",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = EthioNavy
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = customerPhoneOrEmail,
                        onValueChange = { customerPhoneOrEmail = it },
                        label = { Text("ስልክ ቁጥር ወይም ጂሜይል") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone", tint = EthioNavy) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = customerPin,
                        onValueChange = { customerPin = it },
                        label = { Text("የምስጥር ቁጥር (Secret PIN/Password)") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "PIN", tint = EthioNavy) },
                        trailingIcon = {
                            IconButton(onClick = { isPinVisible = !isPinVisible }) {
                                Icon(
                                    imageVector = if (isPinVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle Visibility",
                                    tint = Color.Gray
                                )
                            }
                        },
                        visualTransformation = if (isPinVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "አዲስ አካውንት መመዝገቢያ",
                            fontSize = 11.5.sp,
                            color = EthioNavy,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                isRegOtpSent = false
                                regOtpInput = ""
                                showRegisterDialog = true
                            }
                        )
                        Text(
                            text = "የይለፍ ቃል / PIN ረስተዋል? (Forgot PIN)",
                            fontSize = 11.5.sp,
                            color = EthioRed,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                isForgotOtpSent = false
                                forgotOtpInput = ""
                                showForgotPinDialog = true
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (customerPhoneOrEmail.isNotBlank() && customerPin.isNotBlank()) {
                                val success = viewModel.authenticateCustomer(customerPhoneOrEmail, customerPin)
                                if (success) {
                                    Toast.makeText(context, "እንኳን በደህና መጡ! በስኬት ገብተዋል", Toast.LENGTH_SHORT).show()
                                    onCustomerLoginSuccess()
                                } else {
                                    Toast.makeText(context, "የተሳሳተ ስልክ/ጂሜይል ወይም የምስጥር ቁጥር!", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(context, "እባክዎን ስልክ/ጂሜይል እና የምስጥር ቁጥር ያስገቡ", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EthioGreen),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LockOpen, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("ወደ አካውንት ግባ (Login)", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = {
                            isRegOtpSent = false
                            showRegisterDialog = true
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = EthioNavy),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("አዲስ አካውንት ይክፈቱ (Register New Account)", fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                } else {
                    // ADMIN LOGIN TAB
                    Text(
                        text = "የአስተዳዳሪ የደህንነት መግቢያ",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = EthioRed
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        color = EthioRed.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = "Admin", tint = EthioRed, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ዋና አስተዳዳሪ: ገዛሃኝ አስተዳዳሪ",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = EthioNavy
                            )
                        }
                    }

                    if (adminLoginError != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = adminLoginError ?: "",
                            color = EthioRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = adminEmailInput,
                        onValueChange = { adminEmailInput = it },
                        label = { Text("የአስተዳዳሪ ጂሜይል") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email", tint = EthioNavy) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = adminPasswordInput,
                        onValueChange = { adminPasswordInput = it },
                        label = { Text("የአስተዳዳሪ የይለፍ ቃል") },
                        leadingIcon = { Icon(Icons.Default.Key, contentDescription = "Pass", tint = EthioNavy) },
                        trailingIcon = {
                            IconButton(onClick = { isAdminPassVisible = !isAdminPassVisible }) {
                                Icon(
                                    imageVector = if (isAdminPassVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle Visibility",
                                    tint = Color.Gray
                                )
                            }
                        },
                        visualTransformation = if (isAdminPassVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val success = viewModel.attemptAdminLogin(adminEmailInput, adminPasswordInput)
                            if (success) {
                                Toast.makeText(context, "እንኳን ወደ አስተዳዳሪ ገፅ በሰላም መጡ ገዛሃኝ!", Toast.LENGTH_LONG).show()
                                onAdminLoginSuccess()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EthioNavy),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("እንደ አስተዳዳሪ ግባ (Admin Login)", fontSize = 14.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }

        // Security Notice
        Text(
            text = "🔒 ደህንነቱ የተጠበቀ የኢትዮጵያ ዲጂታል መታወቂያ አገልግሎት",
            fontSize = 11.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }

    // Register New Customer Dialog with OTP Verification
    if (showRegisterDialog) {
        AlertDialog(
            onDismissRequest = { showRegisterDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, tint = EthioNavy)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("አዲስ ደንበኛ መመዝገቢያ", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = EthioNavy)
                        Text("Online OTP Customer Registration", fontSize = 10.5.sp, color = EthioGreen)
                    }
                }
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = regFullName,
                        onValueChange = { regFullName = it },
                        label = { Text("ሙሉ ስም (Full Name)") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = EthioNavy) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )

                    OutlinedTextField(
                        value = regContact,
                        onValueChange = { regContact = it },
                        label = { Text("ስልክ ቁጥር ወይም ጂሜይል (Phone/Gmail)") },
                        leadingIcon = { Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = EthioNavy) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )

                    OutlinedTextField(
                        value = regPin,
                        onValueChange = { regPin = it },
                        label = { Text("አዲስ የምስጥር ቁጥር (4-6 PIN)") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = EthioNavy) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    if (!isRegOtpSent) {
                        Button(
                            onClick = {
                                if (regContact.isNotBlank() && regPin.length >= 4) {
                                    val code = viewModel.generateAndSendOtp(regContact)
                                    isRegOtpSent = true
                                    Toast.makeText(context, "ማረጋገጫ ኮድ ተልኳል! ($code)", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(context, "እባክዎን ስልክ/ጂሜይል እና ቢያንስ 4 አሃዝ ምስጥር ቁጥር ያስገቡ", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EthioNavy),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Sms, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("የOTP ማረጋገጫ ኮድ በጂሜይል/ስልክ ላክ", fontSize = 12.sp)
                            }
                        }
                    } else {
                        OutlinedTextField(
                            value = regOtpInput,
                            onValueChange = { regOtpInput = it },
                            label = { Text("የደረሰዎት 6 አሃዝ OTP ኮድ") },
                            leadingIcon = { Icon(Icons.Default.Verified, contentDescription = null, tint = EthioGreen) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )

                        TextButton(
                            onClick = {
                                val code = viewModel.generateAndSendOtp(regContact)
                                Toast.makeText(context, "አዲስ OTP ኮድ ተልኳል: $code", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Text("ኮድ አልደረሰዎትም? እንደገና ላክ (Resend OTP)", fontSize = 11.5.sp, color = EthioNavy)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val errorMsg = viewModel.registerNewCustomer(regFullName, regContact, regPin, regOtpInput)
                        if (errorMsg == null) {
                            customerPhoneOrEmail = regContact
                            customerPin = regPin
                            showRegisterDialog = false
                            Toast.makeText(context, "አካውንትዎ በስኬት ተመዝግቧል! አሁን ገብተዋል", Toast.LENGTH_LONG).show()
                            onCustomerLoginSuccess()
                        } else {
                            Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                        }
                    },
                    enabled = isRegOtpSent,
                    colors = ButtonDefaults.buttonColors(containerColor = EthioGreen)
                ) {
                    Text("ተመዝገብ (Register & Verify)", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRegisterDialog = false }) {
                    Text("ሰርዝ", color = Color.Gray)
                }
            }
        )
    }

    // Forgot Password / PIN Dialog with OTP
    if (showForgotPinDialog) {
        AlertDialog(
            onDismissRequest = { showForgotPinDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LockReset, contentDescription = null, tint = EthioRed)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("የምስጥር ቁጥር መቀየሪያ", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = EthioNavy)
                        Text("Forgot Password / PIN Recovery", fontSize = 10.5.sp, color = EthioRed)
                    }
                }
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = forgotContact,
                        onValueChange = { forgotContact = it },
                        label = { Text("የተመዘገበበት ስልክ ወይም ጂሜይል") },
                        leadingIcon = { Icon(Icons.Default.ContactPhone, contentDescription = null, tint = EthioNavy) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )

                    if (!isForgotOtpSent) {
                        Button(
                            onClick = {
                                if (forgotContact.isNotBlank()) {
                                    val code = viewModel.generateAndSendOtp(forgotContact)
                                    isForgotOtpSent = true
                                    Toast.makeText(context, "የይለፍ ቃል መቀየሪያ OTP ኮድ ተልኳል! ($code)", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(context, "እባክዎን ስልክ ወይም ጂሜይል ያስገቡ", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EthioNavy),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("የOTP ማረጋገጫ ኮድ ላክ", fontSize = 12.sp)
                        }
                    } else {
                        OutlinedTextField(
                            value = forgotOtpInput,
                            onValueChange = { forgotOtpInput = it },
                            label = { Text("የደረሰዎት 6 አሃዝ OTP ኮድ") },
                            leadingIcon = { Icon(Icons.Default.Pin, contentDescription = null, tint = EthioGreen) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )

                        OutlinedTextField(
                            value = forgotNewPin,
                            onValueChange = { forgotNewPin = it },
                            label = { Text("አዲስ ምስጥር ቁጥር (New Secret PIN)") },
                            leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = EthioNavy) },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val errorMsg = viewModel.resetCustomerPinWithOtp(forgotContact, forgotNewPin, forgotOtpInput)
                        if (errorMsg == null) {
                            customerPhoneOrEmail = forgotContact
                            customerPin = forgotNewPin
                            showForgotPinDialog = false
                            Toast.makeText(context, "የምስጥር ቁጥርዎ በተሳካ ሁኔታ ተቀይሯል!", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                        }
                    },
                    enabled = isForgotOtpSent,
                    colors = ButtonDefaults.buttonColors(containerColor = EthioGreen)
                ) {
                    Text("ምስጥር ቁጥር ቀይር (Reset PIN)", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showForgotPinDialog = false }) {
                    Text("ሰርዝ", color = Color.Gray)
                }
            }
        )
    }
}
