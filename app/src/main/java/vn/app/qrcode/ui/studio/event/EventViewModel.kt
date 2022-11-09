package vn.app.qrcode.ui.studio.event

import android.app.Application
import com.base.common.base.viewmodel.BaseViewModel
import com.base.common.base.viewmodel.CommonEvent
import java.text.SimpleDateFormat
import java.util.Date
import vn.app.qrcode.R
import vn.app.qrcode.data.constant.DATE_TIME_ZONE_FORMAT
import vn.app.qrcode.data.model.QRCodeType
import vn.app.qrcode.data.model.ResultCreator
import vn.app.qrcode.data.model.qrcodeobject.EventQRCode
import vn.app.qrcode.ui.home.camera.mlkit.barcodedetection.BarcodeField
import vn.app.qrcode.utils.AppUtils

class EventViewModel(
    private val application: Application,
) : BaseViewModel<CommonEvent>() {

    lateinit var eventResultCreator: ResultCreator

    fun addNewQrCode(
        type: QRCodeType,
        eventName: String,
        startTime: Long,
        endTime: Long,
        location: String,
        description: String,
    ) {
        val sdf = SimpleDateFormat(DATE_TIME_ZONE_FORMAT)
        val startTimeString = sdf.format(Date(startTime))
        val endTimeString = sdf.format(Date(endTime))
        val eventQrCode = EventQRCode(eventName, startTimeString, endTimeString, location, description)
        val eventBarcode = ArrayList<BarcodeField>()
        eventBarcode.add(
            BarcodeField(
                application.getString(R.string.label_event_name),
                eventName
            )
        )
        eventBarcode.add(
            BarcodeField(
                application.getString(R.string.label_event_start_datetime),
                AppUtils.getDateTimeFromTimeStamp(startTime).toString()
            )
        )
        eventBarcode.add(
            BarcodeField(
                application.getString(R.string.label_event_end_datetime),
                AppUtils.getDateTimeFromTimeStamp(endTime).toString()
            )
        )
        eventBarcode.add(
            BarcodeField(
                application.getString(R.string.label_event_location),
                location
            )
        )
        eventBarcode.add(
            BarcodeField(
                application.getString(R.string.label_event_description),
                description
            )
        )

        eventResultCreator = AppUtils.getNewResultCreator(
            barcodeFieldList = eventBarcode,
            type = type,
            displayName = eventName,
            rawValue = eventQrCode.getRawValue(),
            title = application.getString(R.string.result_creator_email),
            content = eventQrCode.toJson()
        )
    }
}
