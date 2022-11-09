package vn.app.qrcode.data.constant

object EventConstant {
    const val EVENT_LOGIN = 1
    const val EVENT_MAIN = 2

    //Session TimeOut
    const val EVENT_SESSION = 10000
}

enum class CreatedBy {
    QR_CODE_SCAN,
    MANUAL_INPUT
}
