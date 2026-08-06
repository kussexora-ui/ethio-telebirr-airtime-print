package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "admin_settings")
data class AdminSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val superAdminName: String = "Gezahegn Gelebo Alemayehu",
    val superAdminTitle: String = "የመታወቂያ ስርዓት ስራ አስኪያጅ",
    val cbeAccountHolder: String = "Gezahegn Gelebo Alemayehu",
    val cbeAccountNo: String = "1000087841457",
    val telebirrNo: String = "0919397995",
    val mpesaNo: String = "0716357344",
    val helplinePhone: String = "0912702062",
    val digitalCopyPriceEtb: Int = 50,
    val hardcopyPrintPriceEtb: Int = 400
)

@Entity(tableName = "kebele_stamps")
data class KebeleStampEntity(
    @PrimaryKey val id: String,
    val regionName: String,
    val woredaKebele: String,
    val stampTitle: String,
    val stampColorHex: String = "#003399",
    val stampImageUri: String? = null,
    val isDefault: Boolean = false
)

@Entity(tableName = "official_signatures")
data class OfficialSignatureEntity(
    @PrimaryKey val id: String,
    val officialName: String,
    val officialRole: String,
    val signatureImageUri: String? = null,
    val isDefault: Boolean = false
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val cardId: String? = null
)
