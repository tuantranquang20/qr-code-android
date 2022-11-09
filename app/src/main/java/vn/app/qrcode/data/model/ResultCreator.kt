package vn.app.qrcode.data.model

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import vn.app.qrcode.ui.home.camera.mlkit.barcodedetection.BarcodeField

@Parcelize
data class ResultCreator(
    var barcodeFieldList: ArrayList<BarcodeField>,
    var rawValue: String,
    var image: Bitmap,
    var title: String,
    var nameItemResultCreator: String,
    var typeItemResultCreator: Int,
    var content: String = ""
): Parcelable
