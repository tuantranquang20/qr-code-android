package vn.app.qrcode.data.model

data class InputField(
    var fieldType: Int,
    var fieldName: Int,
    var fieldValue: String,
    var inputType: Int,
    var regex: Regex,
    var error: String?,
    var maxLength: Int,
    var stringErrorId: Int
)