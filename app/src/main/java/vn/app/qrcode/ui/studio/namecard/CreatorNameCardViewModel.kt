package vn.app.qrcode.ui.studio.namecard

import com.base.common.base.viewmodel.BaseViewModel
import com.base.common.base.viewmodel.CommonEvent
import vn.app.qrcode.data.model.QRCodeCreator

class CreatorNameCardViewModel() : BaseViewModel<CommonEvent>() {
    var qrCodeCreator: QRCodeCreator?= null
    var rawValue: String =""
    var isValid = true
    var numberPhoneSelected = ""
}
