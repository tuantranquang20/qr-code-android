package vn.app.qrcode.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ContactNewFieldModel (
    val type: Int,
    val labelId: Int,
    val hintId: Int,
    val regex: Regex,
    val errorTextId: Int,
    var inputType: Int
): Parcelable
