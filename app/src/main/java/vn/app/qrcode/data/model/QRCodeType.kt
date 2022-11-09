package vn.app.qrcode.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class QRCodeType (
    val type: Int,
    var nameId: Int,
): Parcelable
