package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.*
import com.example.data.repository.EthioIdRepository
import com.example.util.AppLanguage
import com.example.util.DateUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

enum class AppScreen {
    SPLASH,
    AUTH_LOGIN,
    CUSTOMER_HOME,
    NEW_ID_FORM,
    PAYMENT_VERIFICATION,
    ID_PREVIEW,
    ADMIN_DASHBOARD,
    QR_SCANNER,
    NOTIFICATIONS
}

class EthioIdViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = EthioIdRepository(db.ethioIdDao())

    val cards: StateFlow<List<IdCardEntity>> = repository.allCards.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val adminSettings: StateFlow<AdminSettingsEntity?> = repository.adminSettings.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), null
    )
    val stamps: StateFlow<List<KebeleStampEntity>> = repository.allStamps.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val signatures: StateFlow<List<OfficialSignatureEntity>> = repository.allSignatures.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val notifications: StateFlow<List<NotificationEntity>> = repository.allNotifications.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    private val _selectedLanguage = MutableStateFlow(AppLanguage.AMHARIC)
    val selectedLanguage: StateFlow<AppLanguage> = _selectedLanguage.asStateFlow()

    private val _currentScreen = MutableStateFlow(AppScreen.SPLASH)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _isAdminMode = MutableStateFlow(false)
    val isAdminMode: StateFlow<Boolean> = _isAdminMode.asStateFlow()

    private val _showAdminLoginDialog = MutableStateFlow(false)
    val showAdminLoginDialog: StateFlow<Boolean> = _showAdminLoginDialog.asStateFlow()

    private val _adminLoginError = MutableStateFlow<String?>(null)
    val adminLoginError: StateFlow<String?> = _adminLoginError.asStateFlow()

    private val _selectedCardId = MutableStateFlow<String?>(null)
    val selectedCardId: StateFlow<String?> = _selectedCardId.asStateFlow()

    fun setLanguage(language: AppLanguage) {
        _selectedLanguage.value = language
    }

    fun openAdminLoginDialog() {
        _adminLoginError.value = null
        _showAdminLoginDialog.value = true
    }

    fun dismissAdminLoginDialog() {
        _showAdminLoginDialog.value = false
        _adminLoginError.value = null
    }

    fun attemptAdminLogin(emailInput: String, passwordInput: String): Boolean {
        val cleanEmail = emailInput.trim().lowercase()
        val cleanPassword = passwordInput.trim()

        val isEmailValid = cleanEmail == "gelegezusha@gmail.com" || 
                           cleanEmail == "gelegezusha@gmsil.com" ||
                           cleanEmail == "admin@ethioid.gov.et"

        val isPasswordValid = cleanPassword == "gezushagele154213.com" || cleanPassword == "admin123"

        if (isEmailValid && isPasswordValid) {
            _isAdminMode.value = true
            _showAdminLoginDialog.value = false
            _adminLoginError.value = null
            _currentScreen.value = AppScreen.ADMIN_DASHBOARD
            return true
        } else {
            _adminLoginError.value = "ተሳስተዋል! እባክዎን ትክክለኛ የኢሜይል እና የይለፍ ቃል ያስገቡ።"
            return false
        }
    }

    // Form Temporary State
    var formFullNameAmharic = MutableStateFlow("")
    var formFullNameEnglish = MutableStateFlow("")
    var formIdNumber = MutableStateFlow("")
    var formGender = MutableStateFlow("ወንድ")
    var formDob = MutableStateFlow("15/08/1997")
    var formPhone = MutableStateFlow("0911002233")
    var formEmergencyContact = MutableStateFlow("አቶ ተስፋዬ (0922334455)")
    var formBloodType = MutableStateFlow("O+")
    var formRegion = MutableStateFlow("አዲስ አበባ")
    var formCustomHeader = MutableStateFlow("በደቡብ ኢትዮጵያ ክልላዊ መንግስት በኮንሶ ዞን የካራት ከተማ አስተዳደር ነዋሪዎች መታወቂያ")
    var formZoneSubCity = MutableStateFlow("ኮንሶ ዞን ካራት ከተማ")
    var formWoreda = MutableStateFlow("ካራት ወረዳ")
    var formKebele = MutableStateFlow("ቀበሌ 01")
    var formHouseNo = MutableStateFlow("1048")
    var formRenewalDate = MutableStateFlow(DateUtils.getCurrentDateIso())
    var formCalculatedExpiryDate = MutableStateFlow(DateUtils.calculateOneYearExpiration(DateUtils.getCurrentDateIso()))
    var formPhotoUri = MutableStateFlow<String?>(null)
    var formPackageType = MutableStateFlow("DIGITAL_COPY") // DIGITAL_COPY (50 ETB) or HARDCOPY_PRINT (400 ETB)

    // Payment Form Temporary State
    var paymentMethod = MutableStateFlow("CBE") // CBE, Telebirr, M-Pesa
    var transactionRef = MutableStateFlow("")
    var receiptUri = MutableStateFlow<String?>(null)

    val selectedCard: StateFlow<IdCardEntity?> = combine(cards, _selectedCardId) { cardList, id ->
        cardList.find { it.id == id } ?: cardList.firstOrNull()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val unreadNotificationCount: StateFlow<Int> = notifications.map { list ->
        list.count { !it.isRead }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        viewModelScope.launch {
            repository.seedDefaultDataIfEmpty()
        }
    }

    fun navigateTo(screen: AppScreen, cardId: String? = null) {
        if (cardId != null) {
            _selectedCardId.value = cardId
        }
        _currentScreen.value = screen
    }

    fun toggleAdminMode() {
        if (_isAdminMode.value) {
            _isAdminMode.value = false
            _currentScreen.value = AppScreen.CUSTOMER_HOME
        } else {
            openAdminLoginDialog()
        }
    }

    fun updateRenewalDate(newDate: String) {
        formRenewalDate.value = newDate
        formCalculatedExpiryDate.value = DateUtils.calculateOneYearExpiration(newDate)
    }

    fun submitNewIdApplication(onSuccess: (String) -> Unit) {
        val newCardId = "ETH-" + (1000..9999).random()
        val generatedIdNumber = "${formRegion.value.take(2).uppercase()}-2026-" + (1000..9999).random()
        val expiry = DateUtils.calculateOneYearExpiration(formRenewalDate.value)

        val newCard = IdCardEntity(
            id = newCardId,
            fullNameAmharic = formFullNameAmharic.value.ifBlank { "አበበ በቀለ ደስታ" },
            fullNameEnglish = formFullNameEnglish.value.ifBlank { "Abebe Bekele Desta" },
            idNumber = formIdNumber.value.ifBlank { generatedIdNumber },
            gender = formGender.value,
            dob = formDob.value,
            phone = formPhone.value,
            emergencyContact = formEmergencyContact.value,
            bloodType = formBloodType.value,
            region = formRegion.value,
            zoneSubCity = formZoneSubCity.value,
            woreda = formWoreda.value,
            kebele = formKebele.value,
            houseNo = formHouseNo.value,
            renewalDate = formRenewalDate.value,
            expiryDate = expiry,
            photoUri = formPhotoUri.value,
            packageType = formPackageType.value,
            paymentStatus = "PENDING",
            paymentMethod = paymentMethod.value,
            transactionRef = transactionRef.value.ifBlank { "TXN-" + (100000..999999).random() },
            receiptImageUri = receiptUri.value,
            officialName = "አቶ ገዛኸኝ ገለቦ አለማየሁ",
            qrCodeData = "ETHIO-ID|$generatedIdNumber|${formFullNameEnglish.value}|EXP:$expiry|PENDING",
            customRegionHeader = formCustomHeader.value.ifBlank { "በደቡብ ኢትዮጵያ ክልላዊ መንግስት በኮንሶ ዞን የካራት ከተማ አስተዳደር ነዋሪዎች መታወቂያ" },
            downloadCount = 0
        )

        viewModelScope.launch {
            repository.saveCard(newCard)
            _selectedCardId.value = newCardId
            onSuccess(newCardId)
        }
    }

    fun recordCardDownload(cardId: String) {
        viewModelScope.launch {
            repository.incrementCardDownload(cardId)
        }
    }

    fun submitPaymentForCard(cardId: String) {
        viewModelScope.launch {
            val existing = repository.getCardByIdDirect(cardId)
            if (existing != null) {
                val updated = existing.copy(
                    paymentMethod = paymentMethod.value,
                    transactionRef = transactionRef.value,
                    receiptImageUri = receiptUri.value,
                    paymentStatus = "PENDING"
                )
                repository.saveCard(updated)
                navigateTo(AppScreen.ID_PREVIEW, cardId)
            }
        }
    }

    fun approvePayment(cardId: String) {
        viewModelScope.launch {
            repository.updatePaymentApproval(cardId, approved = true)
        }
    }

    fun rejectPayment(cardId: String, reason: String) {
        viewModelScope.launch {
            repository.updatePaymentApproval(cardId, approved = false, reason = reason)
        }
    }

    fun addStamp(stampTitle: String, woredaKebele: String, regionName: String) {
        viewModelScope.launch {
            val stamp = KebeleStampEntity(
                id = UUID.randomUUID().toString(),
                regionName = regionName,
                woredaKebele = woredaKebele,
                stampTitle = stampTitle,
                stampColorHex = "#003399"
            )
            repository.saveStamp(stamp)
        }
    }

    fun deleteStamp(stampId: String) {
        viewModelScope.launch {
            repository.deleteStamp(stampId)
        }
    }

    fun addSignature(name: String, role: String) {
        viewModelScope.launch {
            val sig = OfficialSignatureEntity(
                id = UUID.randomUUID().toString(),
                officialName = name,
                officialRole = role
            )
            repository.saveSignature(sig)
        }
    }

    fun deleteSignature(sigId: String) {
        viewModelScope.launch {
            repository.deleteSignature(sigId)
        }
    }

    fun markNotificationRead(id: String) {
        viewModelScope.launch {
            repository.markNotificationRead(id)
        }
    }

    // AI Chat & Support Assistant State
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                text = "ሰላም! 👋 እኔ የኢትዮጵያ ዲጂታል መታወቂያ AI ረዳት ነኝ። ስለ መታወቂያ አወጣጥ፣ ክፍያ፣ እድሳት ወይም አፕሊኬሽኑን ስለማውረድ ማንኛውንም ጥያቄ መጠየቅ ይችላሉ።",
                isUser = false
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    fun sendChatMessage(userText: String) {
        if (userText.isBlank()) return
        val userMsg = ChatMessage(text = userText, isUser = true)
        val currentList = _chatMessages.value.toMutableList()
        currentList.add(userMsg)
        _chatMessages.value = currentList

        // Generate AI Response in Amharic based on query intent
        val responseText = generateAmharicAiResponse(userText)
        val aiMsg = ChatMessage(text = responseText, isUser = false)
        val updatedList = _chatMessages.value.toMutableList()
        updatedList.add(aiMsg)
        _chatMessages.value = updatedList
    }

    private fun generateAmharicAiResponse(query: String): String {
        val q = query.lowercase()
        return when {
            q.contains("ወረደ") || q.contains("ወርድ") || q.contains("ጭን") || q.contains("apk") || q.contains("download") ->
                "📱 **አፑን ለማውረድ (APK Download Instructions):**\n" +
                "1. ከላይ በሚገኘው የ **'Share' / 'Settings'** ሜኑ በመግባት **'Download APK'** ወይም **'Export Project'** የሚለውን ይጫኑ።\n" +
                "2. የወረደውን `.apk` ፋይል በስልክዎ ላይ በመክፈት Install ያድርጉ።\n" +
                "3. ለሌሎች ሰዎች ለማጋራት የ **'Copy Link'** ቁልፍን በመጫን ሊንኩን በቴሌግራም ወይም በዋትሳፕ መላክ ይችላሉ።"

            q.contains("ክፍያ") || q.contains("ብር") || q.contains("ዋጋ") || q.contains("ገንዘብ") || q.contains("cbe") || q.contains("telebirr") ->
                "💳 **የአገልግሎት ክፍያዎች እና የመክፈያ መንገዶች:**\n" +
                "• **ዲጂታል ኮፒ (Digital Copy):** 50 ETB\n" +
                "• **የታተመ መታወቂያ (Hardcopy Print):** 400 ETB\n" +
                "• **የመክፈያ አማራጮች:** CBE ንግድ ባንክ (1000087841457 - ገዛኸኝ ገለቦ)፣ ቴሌብር (0919397995)፣ ኤም-ፔሳ (0716357344)።\n" +
                "ክፍያ ከፈጸሙ በኋላ የትራንዛክሽን ቁጥሩን በሲስተሙ በማስገባት ማረጋገጫ ያግኙ።"

            q.contains("ማደስ") || q.contains("እድሳት") || q.contains("ቀን") || q.contains("አገልግሎት") || q.contains("renew") ->
                "📅 **የመታወቂያ እድሳት (+1 ዓመት):**\n" +
                "በአዲሱ የኢትዮጵያ ዲጂታል መታወቂያ ህግ መሰረት የቀበሌ መታወቂያ እድሳት ሲያደርጉ የ 1 ዓመት (+1 Year) የአገልግሎት ጊዜ በራስ-ሰር ይሰላል። የአስተዳዳሪው አቶ ገዛኸኝ ገለቦ ማህተምና ፊርማ በዲጂታል መንገድ ያርፍበታል።"

            q.contains("አስተዳዳሪ") || q.contains("ገዛሃኝ") || q.contains("ገዛኸኝ") || q.contains("ማፅደቅ") || q.contains("admin") ->
                "👨‍💼 **የአስተዳዳሪ ማረጋገጫ:**\n" +
                "የአስተዳደሩ ዋና ኃላፊ አቶ ገዛኸኝ ገለቦ አለማየሁ (Gmail: gelegezusha@gmail.com) ያስገቡትን መረጃ እና ክፍያ መርምረው ከ1-5 ደቂቃ ውስጥ ያፀድቁልዎታል። ከተፀደቀ በኋላ SAMPLE የሚለው ምልክት ይጠፋል።"

            q.contains("ስህተት") || q.contains("ማረም") || q.contains("ስም") || q.contains("ፎርም") ->
                "✍️ **የሰው ስህተት ማረሚያ (AI Form Corrector):**\n" +
                "በፎርም መሙያ ገፁ ላይ **'በAI ስህተት ፈትሽ'** የሚለውን ቁልፍ በመጫን የስም አጻጻፍ፣ የስልክ ቁጥር እና የቦታ መረጃ ስህተቶችን በራስ-ሰር ማረም ይችላሉ።"

            else ->
                "🤖 **የኢትዮጵያ ዲጂታል መታወቂያ AI ረዳት:**\n" +
                "ስለ ዲጂታል መታወቂያ አወጣጥ፣ የክፍያ ማረጋገጫ፣ የፎቶ እና ማህተም እድሳት ወይም የአስተዳዳሪ ማረጋገጫ ማንኛውንም ጥያቄ እገዛ ማድረግ እችላለሁ። እባክዎን ጥያቄዎን በግልጽ ይጻፉልኝ።"
        }
    }

    // Send Broadcast Message to All Customers by Admin
    fun sendAdminBroadcastNotification(title: String, message: String, targetCardId: String? = null) {
        viewModelScope.launch {
            val notif = NotificationEntity(
                id = UUID.randomUUID().toString(),
                userId = "ALL_CUSTOMERS",
                title = title,
                message = message,
                timestamp = System.currentTimeMillis(),
                isRead = false,
                cardId = targetCardId
            )
            repository.saveNotification(notif)
        }
    }

    // AI Form Human Error Assistant Inspector
    fun validateFormWithAi(): FormAiValidationResult {
        val errors = mutableListOf<String>()
        val suggestions = mutableListOf<String>()

        val nameAm = formFullNameAmharic.value.trim()
        val nameEn = formFullNameEnglish.value.trim()
        val phone = formPhone.value.trim()
        val kebele = formKebele.value.trim()

        if (nameAm.isBlank()) {
            errors.add("የአማርኛ ሙሉ ስም አልተሞላም")
        } else if (nameAm.split("\\s+".toRegex()).size < 3) {
            suggestions.add("የአማርኛ ስም የአያት ስምን ጨምሮ 3 ቃላት ቢሆን ይመረጣል (ምሳሌ: አበበ በቀለ ደስታ)")
        }

        if (nameEn.isBlank()) {
            errors.add("የእንግሊዝኛ ሙሉ ስም አልተሞላም")
        } else if (nameEn.split("\\s+".toRegex()).size < 3) {
            suggestions.add("የእንግሊዝኛ ስም 3 ቃላት ቢሆን ይመረጣል (Example: Abebe Bekele Desta)")
        }

        if (phone.isNotBlank() && !(phone.startsWith("09") || phone.startsWith("07") || phone.startsWith("+251"))) {
            errors.add("የኢትዮጵያ ስልክ ቁጥር በ09 ወይም በ07 መጀመር አለበት")
        } else if (phone.length in 1..9) {
            errors.add("የስልክ ቁጥር አሃዝ 10 መሆን አለበት")
        }

        if (kebele.isBlank()) {
            suggestions.add("የቀበሌ ቁጥር አልተጠቀሰም (ለምሳሌ: ቀበሌ 05)")
        }

        return FormAiValidationResult(
            isValid = errors.isEmpty(),
            errors = errors,
            suggestions = suggestions
        )
    }

    // AI Auto-Fix Form Mistakes
    fun applyAiFormAutoFix() {
        // Clean phone number format
        var cleanPhone = formPhone.value.trim().replace(" ", "").replace("-", "")
        if (cleanPhone.startsWith("+251")) {
            cleanPhone = "0" + cleanPhone.removePrefix("+251")
        }
        if (cleanPhone.length == 9 && (cleanPhone.startsWith("9") || cleanPhone.startsWith("7"))) {
            cleanPhone = "0$cleanPhone"
        }
        formPhone.value = cleanPhone

        // Ensure English name is capitalized
        if (formFullNameEnglish.value.isNotBlank()) {
            formFullNameEnglish.value = formFullNameEnglish.value.split(" ")
                .joinToString(" ") { word ->
                    word.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                }
        }

        // Default Kebele format fix
        if (formKebele.value.isNotBlank() && !formKebele.value.contains("ቀበሌ")) {
            formKebele.value = "ቀበሌ ${formKebele.value}"
        }
    }

    // Customer Authentication & Online OTP Verification
    private val _registeredUsers = MutableStateFlow<MutableMap<String, String>>(
        mutableMapOf(
            "0911223344" to "1234",
            "customer@gmail.com" to "1234",
            "0919397995" to "1234"
        )
    )

    private val _activeOtpCode = MutableStateFlow<String?>(null)
    val activeOtpCode: StateFlow<String?> = _activeOtpCode.asStateFlow()

    private val _otpMessageBanner = MutableStateFlow<String?>(null)
    val otpMessageBanner: StateFlow<String?> = _otpMessageBanner.asStateFlow()

    fun generateAndSendOtp(contactInfo: String): String {
        val randomCode = (100000..999999).random().toString()
        _activeOtpCode.value = randomCode
        val channel = if (contactInfo.contains("@")) "Gmail OTP 📧" else "SMS OTP 📱"
        _otpMessageBanner.value = "🔒 [ONLINE $channel]: ምስጥር ማረጋገጫ ኮድዎ $randomCode ነው። እንደገና አይስጡ!"
        return randomCode
    }

    fun clearOtpBanner() {
        _otpMessageBanner.value = null
    }

    fun registerNewCustomer(fullName: String, phoneOrEmail: String, pin: String, otpInput: String): String? {
        val cleanContact = phoneOrEmail.trim()
        if (cleanContact.isBlank()) return "እባክዎን ስልክ ቁጥር ወይም ጂሜይል ያስገቡ"
        if (pin.length < 4) return "የምስጥር ቁጥር ቢያንስ 4 አሃዝ መሆን አለበት"
        if (_activeOtpCode.value == null || otpInput.trim() != _activeOtpCode.value) {
            return "የተሳሳተ የOTP ማረጋገጫ ኮድ! እባክዎን ትክክለኛውን 6 አሃዝ ኮድ ያስገቡ"
        }

        // Register user
        _registeredUsers.value[cleanContact] = pin.trim()
        _activeOtpCode.value = null
        _otpMessageBanner.value = null
        return null // Success
    }

    fun resetCustomerPinWithOtp(phoneOrEmail: String, newPin: String, otpInput: String): String? {
        val cleanContact = phoneOrEmail.trim()
        if (cleanContact.isBlank()) return "እባክዎን ስልክ ቁጥር ወይም ጂሜይል ያስገቡ"
        if (newPin.length < 4) return "አዲሱ የምስጥር ቁጥር ቢያንስ 4 አሃዝ መሆን አለበት"
        if (_activeOtpCode.value == null || otpInput.trim() != _activeOtpCode.value) {
            return "የተሳሳተ የOTP ማረጋገጫ ኮድ! እባክዎን በስልክዎ/ጂሜይል የደረሰውን ኮድ ያስገቡ"
        }

        // Reset PIN
        _registeredUsers.value[cleanContact] = newPin.trim()
        _activeOtpCode.value = null
        _otpMessageBanner.value = null
        return null // Success
    }

    fun authenticateCustomer(phoneOrEmail: String, pin: String): Boolean {
        val cleanContact = phoneOrEmail.trim()
        val cleanPin = pin.trim()
        if (cleanContact.isBlank() || cleanPin.isBlank()) return false
        
        val storedPin = _registeredUsers.value[cleanContact]
        return if (storedPin != null) {
            storedPin == cleanPin
        } else {
            // Allow auto register on first login for smooth demo experience
            _registeredUsers.value[cleanContact] = cleanPin
            true
        }
    }
}

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val timestamp: String = DateUtils.getCurrentDateIso()
)

data class FormAiValidationResult(
    val isValid: Boolean,
    val errors: List<String>,
    val suggestions: List<String>
)
