package com.example.data.repository

import com.example.data.db.*
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class EthioIdRepository(private val dao: EthioIdDao) {

    val allCards: Flow<List<IdCardEntity>> = dao.getAllCards()
    val adminSettings: Flow<AdminSettingsEntity?> = dao.getAdminSettings()
    val allStamps: Flow<List<KebeleStampEntity>> = dao.getAllStamps()
    val allSignatures: Flow<List<OfficialSignatureEntity>> = dao.getAllSignatures()
    val allNotifications: Flow<List<NotificationEntity>> = dao.getAllNotifications()

    fun getCardById(id: String): Flow<IdCardEntity?> = dao.getCardById(id)
    suspend fun getCardByIdDirect(id: String): IdCardEntity? = dao.getCardByIdDirect(id)

    suspend fun saveCard(card: IdCardEntity) {
        dao.insertCard(card)
    }

    suspend fun incrementCardDownload(cardId: String) {
        dao.incrementDownloadCount(cardId)
    }

    suspend fun updatePaymentApproval(cardId: String, approved: Boolean, reason: String? = null) {
        val newStatus = if (approved) "APPROVED" else "REJECTED"
        dao.updatePaymentStatus(cardId, newStatus, reason)

        // Send Push Notification
        val card = dao.getCardByIdDirect(cardId)
        val title = if (approved) "መታወቂያዎ ተፀድቋል! 🎉" else "የመታወቂያ ክፍያ አልተቀበለም ⚠️"
        val message = if (approved)
            "መታወቂያዎ በስኬት ተዘጋጅቷል። አሁን ያለ ምንም SAMPLE ማህተም ማውረድ ይችላሉ። (ID: ${card?.idNumber ?: cardId})"
        else
            "ክፍያዎ አልፀደቀም: ${reason ?: "እባክዎን የትራንዛክሽን ቁጥሩን ያረጋግጡ።"}"

        dao.insertNotification(
            NotificationEntity(
                id = UUID.randomUUID().toString(),
                userId = "customer",
                title = title,
                message = message,
                timestamp = System.currentTimeMillis(),
                cardId = cardId
            )
        )
    }

    suspend fun saveAdminSettings(settings: AdminSettingsEntity) {
        dao.insertAdminSettings(settings)
    }

    suspend fun saveStamp(stamp: KebeleStampEntity) {
        dao.insertStamp(stamp)
    }

    suspend fun deleteStamp(id: String) {
        dao.deleteStamp(id)
    }

    suspend fun saveSignature(sig: OfficialSignatureEntity) {
        dao.insertSignature(sig)
    }

    suspend fun deleteSignature(id: String) {
        dao.deleteSignature(id)
    }

    suspend fun deleteCard(id: String) {
        dao.deleteCard(id)
    }

    suspend fun markNotificationRead(id: String) {
        dao.markNotificationRead(id)
    }

    suspend fun saveNotification(notification: NotificationEntity) {
        dao.insertNotification(notification)
    }

    suspend fun seedDefaultDataIfEmpty() {
        // Seed default Admin Settings
        val existingSettings = dao.getAdminSettings()
        dao.insertAdminSettings(
            AdminSettingsEntity(
                id = 1,
                superAdminName = "Gezahegn Gelebo Alemayehu",
                superAdminTitle = "የመታወቂያ ስርዓት ስራ አስኪያጅ",
                cbeAccountHolder = "Gezahegn Gelebo Alemayehu",
                cbeAccountNo = "1000087841457",
                telebirrNo = "0919397995",
                mpesaNo = "0716357344",
                helplinePhone = "0912702062",
                digitalCopyPriceEtb = 50,
                hardcopyPrintPriceEtb = 400
            )
        )

        // Seed default stamps
        dao.insertStamp(
            KebeleStampEntity(
                id = "default_addis_seal",
                regionName = "አዲስ አበባ",
                woredaKebele = "ቦሌ ክፍለ ከተማ ወረዳ 03",
                stampTitle = "የቀበሌ 03 አስተዳደር ማህተም",
                stampColorHex = "#003399",
                isDefault = true
            )
        )
        dao.insertStamp(
            KebeleStampEntity(
                id = "hawassa_seal",
                regionName = "ሲዳማ ክልል",
                woredaKebele = "ሀዋሳ ከተማ Tabor ቀበሌ 01",
                stampTitle = "የሀዋሳ ከተማ ቀበሌ ማህተም",
                stampColorHex = "#006633",
                isDefault = false
            )
        )

        // Seed default official signature
        dao.insertSignature(
            OfficialSignatureEntity(
                id = "sig_gezahegn",
                officialName = "አቶ ገዛኸኝ ገለቦ አለማየሁ",
                officialRole = "ዋና አስተዳዳሪና ማረጋገጫ ኃላፊ",
                isDefault = true
            )
        )

        // Seed sample customer card if none exists
        val currentCards = dao.getAllCards()
        // We can check if any card exists
        val initialCard = IdCardEntity(
            id = "ETH-2026-001",
            fullNameAmharic = "አቤል ተስፋዬ በቀለ",
            fullNameEnglish = "Abel Tesfaye Bekele",
            idNumber = "ADDIS-2026-8841",
            gender = "ወንድ",
            dob = "15/08/1997",
            phone = "0911223344",
            emergencyContact = "ትግስት ተስፋዬ (0922334455)",
            bloodType = "O+",
            region = "አዲስ አበባ",
            zoneSubCity = "ቦሌ ክፍለ ከተማ",
            woreda = "ወረዳ 03",
            kebele = "ቀበሌ 05",
            houseNo = "1204/B",
            renewalDate = "2026-08-06",
            expiryDate = "2027-08-06", // +1 Year Auto-calculated
            packageType = "DIGITAL_COPY",
            paymentStatus = "APPROVED",
            paymentMethod = "CBE",
            transactionRef = "CBE-TXN-9988112",
            officialName = "አቶ ገዛኸኝ ገለቦ አለማየሁ",
            qrCodeData = "ETHIO-ID|ADDIS-2026-8841|Abel Tesfaye Bekele|2026-08-06|2027-08-06|APPROVED"
        )
        dao.insertCard(initialCard)

        // Seed initial notification
        dao.insertNotification(
            NotificationEntity(
                id = "notif_welcome",
                userId = "customer",
                title = "እንኳን ወደ የኢትዮጵያ መታወቂያ መተግበሪያ በደህና መጡ! 🇪🇹",
                message = "የቀበሌ መታወቂያዎን በ2 ደቂቃ ውስጥ ያድሱ ወይም አዲስ ያውጡ። የ+1 ዓመት እድሳት በራስ-ሰር ይሰላል።",
                timestamp = System.currentTimeMillis(),
                cardId = "ETH-2026-001"
            )
        )
    }
}
