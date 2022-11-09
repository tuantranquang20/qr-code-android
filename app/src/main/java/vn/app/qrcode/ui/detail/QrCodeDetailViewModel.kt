package vn.app.qrcode.ui.detail

import androidx.lifecycle.viewModelScope
import com.base.common.base.viewmodel.BaseViewModel
import com.base.common.base.viewmodel.CommonEvent
import kotlinx.coroutines.launch
import vn.app.qrcode.data.model.QRCodeCreator
import vn.app.qrcode.data.repository.QrCodeRepository

class QrCodeDetailViewModel(private val qrCodeRepository: QrCodeRepository) : BaseViewModel<CommonEvent>() {
    fun updateQRCode(qrCodeItem: QRCodeCreator) {
        viewModelScope.launch {
            qrCodeRepository.update(qrCodeItem)
        }
    }
}
