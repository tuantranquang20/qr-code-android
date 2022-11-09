package vn.app.qrcode.data.constant

import vn.app.qrcode.BuildConfig

const val DEFAULT_PAGE = 1
const val MAX_IMAGE_DIMENSION = 1024
const val TAG_LOG_ERROR = "LogError"

// Result Scan Fragment
const val REQUEST_CODE_COMMON = 0
const val REQUEST_CODE_PHOTO_LIBRARY = 1
const val REQUEST_CODE_CONNECT_WIFI = 2
const val REQUEST_CODE_COPY_TEXT = 3
const val REQUEST_CODE_OPEN_CONTACT = 4

const val LINK_BROWSER_FACEBOOK = "fb://facewebmodal/f?href="

const val LAYOUT_WEIGHT_DATA_FIELD_FIRST = 0.0f
enum class WorkflowState {
    NOT_STARTED,
    DETECTING,
    DETECTED,
}

// Date format
const val FORMAT_DATE_QRCODE = "yyyyMMdd"
const val FORMAT_DATE_DISPLAY = "dd/MM/yyyy"
const val FORMAT_DATETIME_DISPLAY = "HH:mm:ss dd/MM/yyyy"
const val FORMAT_DATETIME_NO_SECOND_DISPLAY = "HH:mm dd/MM/yyyy"
const val DIRECTORY_SAVE_IMAGE = "/QRGenerator"
const val TIME_FORMAT = "HH:mm"
const val DATE_TIME_ZONE_FORMAT = "yyyyMMdd'T'HHmmss'Z'"

// Create Wifi
const val WIFI_ENCRYPTION_TYPE_NONE = "None"
const val WIFI_ENCRYPTION_TYPE_WPA = "WPA"
const val WIFI_ENCRYPTION_TYPE_WEP = "WEP"

// Camera Fragment
const val MIN_SLIDER_ZOOM = 0.0f
const val MAX_SLIDER_ZOOM = 99.0f
const val STEP_BUTTON_ZOOM = 10.0f

// Share , Save Image
const val QUALITY_IMAGE = 100
const val AUTHORITY = "vn.app.qrcode.fileprovider"
const val IMAGE_CACHE = "images"
const val IMAGE_SHARE = "shared_image.png"
const val SUBJECT = "Subject Here"
const val TITLE_SHARE = "Share via"
const val TYPE_SHARE_IMG = "image/png"
// Share Image
const val SUFFIX_JPG_IMAGES = ".jpg"

// Database
const val DATABASE_CREATOR_NAME = "qr_code_db"

const val TAG_HISTORY_FILTER = "Tag_history_filter"
val REGEX_FACEBOOK_URL =
    Regex("""(?:https?://)?(?:www\.)?facebook\.com/(?:\w*#!/)?(?:pages/)?(?:[\w\-]*/)*([\w\-.]*)""")
val REGEX_TWITTER_URL =
    Regex("""(?:https?://)?(?:www\.)?twitter\.com/(?:\w*#!/)?(?:pages/)?(?:[\w\-]*/)*([\w\-.]*)""")
val REGEX_INSTAGRAM_URL =
    Regex("""(?:(?:http|https)://)?(?:www.)?(?:instagram.com|instagr.am|instagr.com)/(\w*)""")
val REGEX_URL =
    Regex("""https?://(www\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}\b([-a-zA-Z0-9()@:%_+.~#?&/=]*)""")
val REGEX_PHONE = Regex("""^[+]?[0-9]{8,15}$""")
val REGEX_NOT_NULL = Regex("[-a-zA-Z0-9@:%._+~#=].{0,100}")
val REGEX_EMAIL = Regex("^(([a-zA-Z0-9]+)([.]{1})?)*[a-zA-Z0-9]@([a-zA-Z0-9]+[.])+[a-zA-Z0-9]+$")
val REGEX_PASSWORD_WIFI = Regex("[-a-zA-Z0-9@:%._+~#=].{7,}")
val REGEX_BCC_EMAIL = Regex("([?|&])(bcc|cc)=([^&^?^:^;]+)")
val REGEX_GEO_LOCATION = Regex("^(GEO|geo):([-+.0-9]+),([-+.0-9]+)([\\s]|\$)")
val REGEX_QUERY_LOCATION = Regex("\\s(QUERY):([^&^?^:^;]+)([\\s]|$)")
val REGEX_CONTACT_TYPE = Regex("[\\s]*(TYPE_QRCODE):([^&^?^:^;]+)([\\s]|$)")
val REGEX_CONTACT_BDAY = Regex("\\s(BDAY):([^&^?^:^;]+)\\s")
val REGEX_CONTACT_NOTE = Regex("\\s(NOTE):([^&^?^:^;]+)\\s")

const val MAX_FIELD_TO_MESSAGE = 4
const val TEXT_EMPTY = ""
const val TEXT_SPACE = " "

const val FIRST_POSITION = 0

enum class TYPE_LIST_CODE {
    YOUR_CODE,
    FAVORITE,
}

enum class PopUpMessage {
    SUCCESS,
    FAIL
}

//Format Email Raw Value QRCode
const val EMAIL_PREFIX_ADDRESS = "mailto:"
const val EMAIL_SUFFIX_ADDRESS = "?"
const val EMAIL_PREFIX_SUBJECT = "subject="
const val EMAIL_PREFIX_BODY = "body="
const val EMAIL_SUFFIX_OTHER = "&"

//Format VCard Raw Value QRCode
const val VCARD_BEGIN = "BEGIN:VCARD"
const val VCARD_PREFIX_NAME = "N:"
const val VCARD_PREFIX_COMPANY = "ORG:"
const val VCARD_PREFIX_TITLE = "TITLE:"
const val VCARD_PREFIX_PHONE = "TEL:"
const val VCARD_PREFIX_WEB = "URL:"
const val VCARD_PREFIX_EMAIL = "EMAIL:"
const val VCARD_PREFIX_ADDRESS = "ADR:"
const val VCARD_PREFIX_NOTE = "NOTE:"
const val VCARD_PREFIX_BIRTHDAY = "BDAY:"
const val VCARD_END = "END:VCARD"

const val PERCENT_WIDTH_DIALOG = 0.85
const val PERCENT_HEIGHT_DIALOG = 0.85

// Location creator
const val TAG_MAP_DIALOG = "MapDialogFragment"
const val ZOOM_CAMERA_MAP = 15f
const val MAX_RESULT_ADDRESS = 1
const val LOCATION_PERMISSION_ID = 1999
const val FIRST_ADDRESS_RESULT = 0
const val TAG_LOCATION_LOADING_DIALOG = "LocationLoadingDialog"
const val MAX_LOCATION_LATITUDE = 90.0
const val MIN_LOCATION_LATITUDE = -90.0
const val MAX_LOCATION_LONGITUDE = 180.0
const val MIN_LOCATION_LONGITUDE = -180.0

const val SMS_TO = "smsto:"
const val SMS_BODY = "sms_body"

// save and share
const val WIDTH_QR_IMAGE = 512
const val HEIGHT_QR_IMAGE = 512
const val TEXT_INTENT_TYPE = "text/plain"
const val MARKET_URI = "market://details?id=com.tokyotechlab.qrcodescanner"
const val PLAY_GOOGLE_URI = "http://play.google.com/store/apps/details?id=com.tokyotechlab.qrcodescanner"

