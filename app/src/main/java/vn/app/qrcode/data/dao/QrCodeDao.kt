package vn.app.qrcode.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import vn.app.qrcode.data.model.QRCodeCreator

@Dao
interface QrCodeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: QRCodeCreator)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(listItems: List<QRCodeCreator>)

    @Update
    suspend fun update(item: QRCodeCreator)

    @Update
    suspend fun updateItems(listItems: List<QRCodeCreator>)

    @Query("UPDATE qr_code SET is_favorite = :isFavorite WHERE id IN (:listIdItems)")
    suspend fun updateFavoriteItems(listIdItems: List<Long>, isFavorite: Boolean)

    @Delete
    suspend fun delete(item: QRCodeCreator)

    @Query("DELETE FROM qr_code WHERE id IN (:idList)")
    suspend fun deleteItems(idList: List<Long>)

    @Query("DELETE FROM qr_code")
    suspend fun clear()

    @Query("SELECT * FROM qr_code WHERE id = :id")
    fun getQrCodeById(id: Long): Flow<QRCodeCreator>

    @Query("SELECT * FROM qr_code WHERE hash_code = :hashCode")
    fun getQrCodeByHashCode(hashCode: String):QRCodeCreator

    // qr code item for history screen filter
    @Query("SELECT * FROM qr_code " +
            "WHERE last_scan IS NOT NULL " +
            "AND type IN (:types) " +
            "ORDER BY last_scan DESC")
    fun getQrCodesHistoryByType(types: List<Int>): Flow<List<QRCodeCreator>>

    // qr code item for history screen
    @Query("SELECT * FROM qr_code WHERE last_scan IS NOT NULL ORDER BY last_scan DESC")
    fun getAllQrCodeHistory(): Flow<List<QRCodeCreator>>

    // qr code item for your code - studio screen filter
    @Query("SELECT * FROM qr_code " +
            "WHERE created_by = :createdBy " +
            "AND type IN (:types) " +
            "ORDER BY created_at DESC")
    fun getQrCodesYourCodeByType(types: List<Int>, createdBy: Int): Flow<List<QRCodeCreator>>

    // qr code item for your code - studio screen
    @Query("SELECT * FROM qr_code WHERE created_by = :createdBy ORDER BY created_at DESC")
    fun getAllQrCodeYourCode(createdBy: Int): Flow<List<QRCodeCreator>>

    // qr code item for favorite - studio screen filter
    @Query("SELECT * FROM qr_code " +
            "WHERE is_favorite " +
            "AND type IN (:types) " +
            "ORDER BY created_at DESC")
    fun getQrCodesFavoriteByType(types: List<Int>): Flow<List<QRCodeCreator>>

    // qr code item for favorite - studio screen
    @Query("SELECT * FROM qr_code WHERE is_favorite ORDER BY created_at DESC")
    fun getAllQrCodeFavorite(): Flow<List<QRCodeCreator>>

    @Query("SELECT EXISTS (SELECT 1 FROM qr_code WHERE hash_code = :hashCode)")
    fun isExists(hashCode: String): Boolean
}
