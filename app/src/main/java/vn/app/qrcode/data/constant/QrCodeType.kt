package vn.app.qrcode.data.constant

import vn.app.qrcode.R
import vn.app.qrcode.data.model.QRCodeType

enum class QrCodeType {
    ALL,
    EMAIL,
    MESSAGE,
    CONTACT,
    URL,
    WIFI,
    LOCATION,
    EVENT,
    TEXT,
    NAME_CARD,
    FACEBOOK,
    TWITTER,
    INSTAGRAM,
}

enum class QrItemType{
    HISTORY_ITEM,
    YOUR_CODE_ITEM,
    FAVORITE_ITEM
}

object QrCodeTypeData {
    val listQrCodeType = listOf(
        QRCodeType(
            QrCodeType.ALL.ordinal,
            R.string.filter_item_all
        ),
        QRCodeType(
            QrCodeType.EMAIL.ordinal,

            R.string.filter_item_email
        ),
        QRCodeType(
            QrCodeType.MESSAGE.ordinal,

            R.string.filter_item_message
        ),
        QRCodeType(
            QrCodeType.CONTACT.ordinal,

            R.string.filter_item_contact
        ),
        QRCodeType(
            QrCodeType.URL.ordinal,

            R.string.filter_item_url
        ),
        QRCodeType(
            QrCodeType.WIFI.ordinal,

            R.string.filter_item_wifi
        ),
        QRCodeType(
            QrCodeType.LOCATION.ordinal,

            R.string.filter_item_location
        ),
        QRCodeType(
            QrCodeType.EVENT.ordinal,

            R.string.filter_item_event
        ),
        QRCodeType(
            QrCodeType.TEXT.ordinal,

            R.string.filter_item_text
        ),
        QRCodeType(
            QrCodeType.NAME_CARD.ordinal,

            R.string.filter_item_name_card
        ),
        QRCodeType(
            QrCodeType.FACEBOOK.ordinal,

            R.string.filter_item_facebook
        ),
        QRCodeType(
            QrCodeType.TWITTER.ordinal,

            R.string.filter_item_twitter
        ),
        QRCodeType(
            QrCodeType.INSTAGRAM.ordinal,

            R.string.filter_item_instagram
        ),
    )
}
