package vn.app.qrcode.ui.studio.wifi

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.base.common.base.viewmodel.BaseViewModel
import com.base.common.base.viewmodel.CommonEvent
import com.google.mlkit.vision.barcode.common.Barcode
import vn.app.qrcode.data.model.QRCodeCreator

class CreatorWifiViewModel() : BaseViewModel<CommonEvent>() {
    var qrCodeCreator: QRCodeCreator?= null
    var rawValue: String =""
    var isValid = true

    private val _encryptionType = MutableLiveData<Int>()
    val encryptionType: LiveData<Int>
        get() = _encryptionType

    init {
        _encryptionType.value = Barcode.WiFi.TYPE_WPA
    }

    fun setEncryptionType(encryptionType: Int) {
        _encryptionType.value = encryptionType
    }
}
