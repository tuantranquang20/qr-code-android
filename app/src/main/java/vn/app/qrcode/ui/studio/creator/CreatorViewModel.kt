package vn.app.qrcode.ui.studio.creator

import com.base.common.base.viewmodel.BaseViewModel
import com.base.common.base.viewmodel.CommonEvent
import vn.app.qrcode.data.constant.QrCodeType
import vn.app.qrcode.data.constant.QrCodeTypeData

class CreatorViewModel() : BaseViewModel<CommonEvent>() {
    val listQrType = QrCodeTypeData.listQrCodeType.filter {
        it.type != QrCodeType.ALL.ordinal || it.type != QrCodeType.LOCATION.ordinal
    }
}
