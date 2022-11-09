package vn.app.qrcode.ui.studio.resultcreator

import androidx.lifecycle.viewModelScope
import com.base.common.base.viewmodel.BaseViewModel
import com.base.common.base.viewmodel.CommonEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import vn.app.qrcode.data.model.QRCodeCreator
import vn.app.qrcode.data.repository.QrCodeRepository
import vn.app.qrcode.utils.AppUtils

class ResultCreatorViewModel(private val qrCodeRepository: QrCodeRepository) :
    BaseViewModel<CommonEvent>() {

    var qrCodeCreator: QRCodeCreator? = null
    var isUpdateItem = true

    fun createNewQRCode() {
        viewModelScope.launch {
            qrCodeCreator?.let { qrCodeRepository.insert(it) }
            withContext(Dispatchers.IO) {
                qrCodeCreator =
                    qrCodeCreator?.let { qrCodeRepository.getQrCodeByHashCode(it.hashCode) }
            }
        }
    }

    fun updateQRCode() {
        viewModelScope.launch {
            qrCodeCreator?.let { qrCodeRepository.update(it) }
            isUpdateItem = false
        }
    }

    fun updateQRCode(qrCode: QRCodeCreator) {
        viewModelScope.launch {
            qrCodeRepository.update(qrCode)
            isUpdateItem = false
        }
    }

    fun checkQRCodeExist(rawValue: String): QRCodeCreator {
        return qrCodeRepository.getQrCodeByHashCode(AppUtils.md5(rawValue) ?: "")
    }
}
