package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EthioIdDao {

    // ID Cards
    @Query("SELECT * FROM id_cards ORDER BY createdAt DESC")
    fun getAllCards(): Flow<List<IdCardEntity>>

    @Query("SELECT * FROM id_cards WHERE id = :id")
    fun getCardById(id: String): Flow<IdCardEntity?>

    @Query("SELECT * FROM id_cards WHERE id = :id")
    suspend fun getCardByIdDirect(id: String): IdCardEntity?

    @Query("SELECT * FROM id_cards WHERE paymentStatus = :status ORDER BY createdAt DESC")
    fun getCardsByPaymentStatus(status: String): Flow<List<IdCardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: IdCardEntity)

    @Update
    suspend fun updateCard(card: IdCardEntity)

    @Query("UPDATE id_cards SET paymentStatus = :status, rejectionReason = :reason WHERE id = :cardId")
    suspend fun updatePaymentStatus(cardId: String, status: String, reason: String? = null)

    @Query("UPDATE id_cards SET downloadCount = downloadCount + 1 WHERE id = :cardId")
    suspend fun incrementDownloadCount(cardId: String)

    @Query("DELETE FROM id_cards WHERE id = :id")
    suspend fun deleteCard(id: String)

    // Admin Settings
    @Query("SELECT * FROM admin_settings WHERE id = 1")
    fun getAdminSettings(): Flow<AdminSettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdminSettings(settings: AdminSettingsEntity)

    // Kebele Stamps
    @Query("SELECT * FROM kebele_stamps ORDER BY woredaKebele ASC")
    fun getAllStamps(): Flow<List<KebeleStampEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStamp(stamp: KebeleStampEntity)

    @Query("DELETE FROM kebele_stamps WHERE id = :id")
    suspend fun deleteStamp(id: String)

    // Signatures
    @Query("SELECT * FROM official_signatures ORDER BY officialName ASC")
    fun getAllSignatures(): Flow<List<OfficialSignatureEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSignature(sig: OfficialSignatureEntity)

    @Query("DELETE FROM official_signatures WHERE id = :id")
    suspend fun deleteSignature(id: String)

    // Notifications
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationRead(id: String)
}
