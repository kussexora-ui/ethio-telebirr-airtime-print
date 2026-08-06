package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "id_cards")
data class IdCardEntity(
    @PrimaryKey val id: String,
    val fullNameAmharic: String,
    val fullNameEnglish: String,
    val idNumber: String,
    val gender: String, // "ወንድ" or "ሴት"
    val dob: String, // e.g. "12/05/1996"
    val phone: String,
    val emergencyContact: String,
    val bloodType: String,
    val region: String,
    val zoneSubCity: String,
    val woreda: String,
    val kebele: String,
    val houseNo: String,
    val renewalDate: String, // e.g. "2026-08-06"
    val expiryDate: String, // Auto-calculated +1 Year (e.g. "2027-08-06")
    val photoUri: String? = null,
    val packageType: String, // "DIGITAL_COPY" or "HARDCOPY_PRINT"
    val paymentStatus: String, // "PENDING", "APPROVED", "REJECTED"
    val paymentMethod: String, // "CBE", "Telebirr", "M-Pesa"
    val transactionRef: String,
    val receiptImageUri: String? = null,
    val sealId: String = "default_addis_seal",
    val officialSignatureId: String = "sig_gezahegn",
    val officialName: String = "አቶ ገዛኸኝ ገለቦ አለማየሁ",
    val qrCodeData: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val rejectionReason: String? = null,
    val customRegionHeader: String = "በደቡብ ኢትዮጵያ ክልላዊ መንግስት በኮንሶ ዞን የካራት ከተማ አስተዳደር ነዋሪዎች መታወቂያ",
    val downloadCount: Int = 0
)
