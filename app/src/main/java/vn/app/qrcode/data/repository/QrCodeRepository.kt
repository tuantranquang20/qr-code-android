package vn.app.qrcode.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import vn.app.qrcode.data.dao.QrCodeDao
import vn.app.qrcode.data.model.QRCodeCreator

class QrCodeRepository(private val qrCodeDao: QrCodeDao) {
    fun getAllQrCodeHistory(): LiveData<List<QRCodeCreator>> {
        return qrCodeDao.getAllQrCodeHistory().asLiveData()
    }

    fun getQrCodeById(id: Long): LiveData<QRCodeCreator> {
        return qrCodeDao.getQrCodeById(id).asLiveData()
    }

    fun getQrCodeByHashCode(hashCode: String): QRCodeCreator {
        return qrCodeDao.getQrCodeByHashCode(hashCode)
    }

    fun getQrCodesHistoryByType(types: List<Int>): LiveData<List<QRCodeCreator>> {
        return qrCodeDao.getQrCodesHistoryByType(types).asLiveData()
    }

    // get item in studio screen
    fun getAllQrCodeYourCode(createdBy: Int): LiveData<List<QRCodeCreator>> {
        return qrCodeDao.getAllQrCodeYourCode(createdBy).asLiveData()
    }

    // get item in filter studio screen
    fun getQrCodesYourCodeByType(types: List<Int>, createdBy: Int): LiveData<List<QRCodeCreator>> {
        return qrCodeDao.getQrCodesYourCodeByType(types, createdBy).asLiveData()
    }

    fun getAllQrCodeFavorite(): LiveData<List<QRCodeCreator>> {
        return qrCodeDao.getAllQrCodeFavorite().asLiveData()
    }

    fun getQrCodesFavoriteByType(types: List<Int>): LiveData<List<QRCodeCreator>> {
        return qrCodeDao.getQrCodesFavoriteByType(types).asLiveData()
    }
    suspend fun insert(newQrCode: QRCodeCreator) {
        qrCodeDao.insert(newQrCode)
    }

    suspend fun insertAll(listQrCode: List<QRCodeCreator>) {
        qrCodeDao.insertAll(listQrCode)
    }

    suspend fun update(updateQrCode: QRCodeCreator) {
        qrCodeDao.update(updateQrCode)
    }

    suspend fun delete(deleteQrCode: QRCodeCreator) {
        qrCodeDao.delete(deleteQrCode)
    }

    suspend fun deleteItems(listItems: List<Long>) {
        qrCodeDao.deleteItems(listItems)
    }

    suspend fun clear() {
        qrCodeDao.clear()
    }

    suspend fun updateItems(listQrCodes: List<QRCodeCreator>) {
        qrCodeDao.updateItems(listQrCodes)
    }

    suspend fun updateFavoriteItems(listIdItems: List<Long>, isFavorite: Boolean) {
        qrCodeDao.updateFavoriteItems(listIdItems, isFavorite)
    }

    fun checkExist(hashCode: String): Boolean{
        return qrCodeDao.isExists(hashCode)
    }

}
