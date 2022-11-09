package vn.app.qrcode.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import net.glxn.qrgen.core.scheme.Schema
import timber.log.Timber
import vn.app.qrcode.R
import vn.app.qrcode.data.constant.AUTHORITY
import vn.app.qrcode.data.constant.CreatedBy
import vn.app.qrcode.data.constant.DATE_TIME_ZONE_FORMAT
import vn.app.qrcode.data.constant.DIRECTORY_SAVE_IMAGE
import vn.app.qrcode.data.constant.FIRST_ADDRESS_RESULT
import vn.app.qrcode.data.constant.FORMAT_DATETIME_DISPLAY
import vn.app.qrcode.data.constant.FORMAT_DATE_DISPLAY
import vn.app.qrcode.data.constant.FORMAT_DATE_QRCODE
import vn.app.qrcode.data.constant.HEIGHT_QR_IMAGE
import vn.app.qrcode.data.constant.IMAGE_CACHE
import vn.app.qrcode.data.constant.IMAGE_SHARE
import vn.app.qrcode.data.constant.MAX_RESULT_ADDRESS
import vn.app.qrcode.data.constant.PopUpMessage
import vn.app.qrcode.data.constant.QUALITY_IMAGE
import vn.app.qrcode.data.constant.SUBJECT
import vn.app.qrcode.data.constant.SUFFIX_JPG_IMAGES
import vn.app.qrcode.data.constant.TAG_LOG_ERROR
import vn.app.qrcode.data.constant.TEXT_EMPTY
import vn.app.qrcode.data.constant.TITLE_SHARE
import vn.app.qrcode.data.constant.TYPE_SHARE_IMG
import vn.app.qrcode.data.constant.WIDTH_QR_IMAGE
import vn.app.qrcode.data.constant.WIFI_ENCRYPTION_TYPE_NONE
import vn.app.qrcode.data.constant.WIFI_ENCRYPTION_TYPE_WEP
import vn.app.qrcode.data.constant.WIFI_ENCRYPTION_TYPE_WPA
import vn.app.qrcode.data.model.QRCodeCreator
import vn.app.qrcode.data.model.QRCodeType
import vn.app.qrcode.data.model.ResultCreator
import vn.app.qrcode.ui.home.camera.mlkit.barcodedetection.BarcodeField
import kotlin.collections.HashMap


object AppUtils {
    fun findAttributeFromQRCode(
        rawValue: String,
        startPoint: String,
        endPoint: String
    ): String {
        var start = rawValue.indexOf(startPoint)
        var result = ""
        if (start > 0) {
            var newString = rawValue.substring(start)
            var end = newString.indexOf(endPoint)
            if (end > 0) {
                result = newString.substring(startPoint.length, end)
            } else {
                result = newString.substring(startPoint.length, newString.length - 1)
            }
        }
        return result
    }

    @SuppressLint("SimpleDateFormat")
    fun convertDateString(dateQRCode: String, fromFormat: String, toFormat: String): String {
        val inputDateFormat = SimpleDateFormat(fromFormat)
        val inputDate = inputDateFormat.parse(dateQRCode)
        val resultDateFormat = SimpleDateFormat(toFormat)
        val result = resultDateFormat.format(inputDate)
        return result.toString()
    }

    @SuppressLint("SimpleDateFormat")
    fun getStringDateFromQRCode(dateQRCode: String): String {
        val inputDateFormat = SimpleDateFormat(FORMAT_DATE_QRCODE)
        val inputDate = inputDateFormat.parse(dateQRCode)
        val resultDateFormat = SimpleDateFormat(FORMAT_DATE_DISPLAY)
        val result = resultDateFormat.format(inputDate)
        return result.toString()
    }

    @SuppressLint("SimpleDateFormat")
    fun getStringDateTimeZone(dateQRCode: String): String {
        val inputDateFormat = SimpleDateFormat(FORMAT_DATE_DISPLAY)
        val inputDate = inputDateFormat.parse(dateQRCode)
        val resultDateFormat = SimpleDateFormat(DATE_TIME_ZONE_FORMAT)
        val result = resultDateFormat.format(inputDate)
        return result.toString()
    }

    @SuppressLint("SimpleDateFormat")
    fun getTimeFromDateTimeZone(dateQRCode: String): String {
        val inputDateFormat = SimpleDateFormat(DATE_TIME_ZONE_FORMAT)
        val inputDate = inputDateFormat.parse(dateQRCode)
        val resultDateFormat = SimpleDateFormat(FORMAT_DATETIME_DISPLAY)
        val result = resultDateFormat.format(inputDate)
        return result.toString()
    }

    @SuppressLint("SimpleDateFormat")
    fun getDateTimeFromTimeStamp(timeStamp: Long): String? {
        return try {
            val sdf = SimpleDateFormat(FORMAT_DATETIME_DISPLAY)
            val netDate = Date(timeStamp)
            sdf.format(netDate)
        } catch (e: Exception) {
            e.toString()
        }
    }

    fun md5(s: String): String? {
        try {
            // Create MD5 Hash
            val digest: MessageDigest = MessageDigest.getInstance("MD5")
            digest.update(s.toByteArray())
            val messageDigest: ByteArray = digest.digest()

            // Create Hex String
            val hexString = StringBuffer()
            for (i in messageDigest.indices) hexString.append(
                Integer.toHexString(
                    0xFF and messageDigest[i]
                        .toInt()
                )
            )
            return hexString.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return ""
    }

    fun createItemFromScanner(
        rawValue: String,
        displayName: String,
        typeQR: Int,
        content: String
    ): QRCodeCreator {
        return QRCodeCreator(
            type = typeQR,
            displayName = displayName,
            rawValue = rawValue,
            lastScan = Date().time,
            hashCode = md5(rawValue)!!,
            content = content
        )
    }

    fun createItemFromCreator(
        rawValue: String,
        displayName: String,
        typeQR: Int,
        content: String,
    ): QRCodeCreator {
        return QRCodeCreator(
            type = typeQR,
            displayName = displayName,
            rawValue = rawValue,
            createdBy = CreatedBy.MANUAL_INPUT.ordinal,
            hashCode = md5(rawValue)!!,
            content = content
        )
    }

    fun getNewResultCreator(
        barcodeFieldList: ArrayList<BarcodeField>,
        type: QRCodeType,
        displayName: String,
        rawValue: String,
        title: String,
        content: String
    ): ResultCreator {
        return ResultCreator(
            barcodeFieldList = barcodeFieldList,
            nameItemResultCreator = displayName,
            typeItemResultCreator = type.type,
            rawValue = rawValue,
            image = generateQRCode(rawValue),
            title = title,
            content = content
        )
    }

    fun getTimeStampSecond(): String? {
        val tsLong = System.currentTimeMillis() / 1000
        val ts = tsLong.toString()
        return ts
    }

    fun shareQrImage(activity: Activity, bitmap: Bitmap) {
        val contentUri = getContentUri(activity, bitmap)

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = TYPE_SHARE_IMG
        intent.putExtra(Intent.EXTRA_SUBJECT, SUBJECT)
        intent.putExtra(Intent.EXTRA_STREAM, contentUri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        activity.startActivity(Intent.createChooser(intent, TITLE_SHARE))
    }

    private fun getContentUri(context: Context, bitmap: Bitmap): Uri?{
        val imagesFolder = File(context.cacheDir, IMAGE_CACHE)
        var contentUri: Uri? = null
        try {
            imagesFolder.mkdirs()
            val file = File(imagesFolder, IMAGE_SHARE)
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, QUALITY_IMAGE, stream)
            stream.flush()
            stream.close()
            contentUri = FileProvider.getUriForFile(context, AUTHORITY, file)
        } catch (e: java.lang.Exception) {
            Timber.d(e)
        }
        return contentUri
    }

    fun saveImage(img: Bitmap, activity: Activity): Boolean {
        var savedImagePath: String? = null

        val imageFileName = "QR${getTimeStampSecond()}${SUFFIX_JPG_IMAGES}"
        val storageDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            DIRECTORY_SAVE_IMAGE
        )
        var success = true
        if (!storageDir.exists()) {
            success = storageDir.mkdirs()
        }
        if (success) {
            val imageFile = File(storageDir, imageFileName)
            savedImagePath = imageFile.getAbsolutePath()
            try {
                val fOut = FileOutputStream(imageFile)
                img.compress(Bitmap.CompressFormat.JPEG, QUALITY_IMAGE, fOut)
                fOut.close()
            } catch (e: java.lang.Exception) {
                Log.e(TAG_LOG_ERROR, e.toString())
            }

            // Add the image to the system gallery
            val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            val f = File(savedImagePath)
            val contentUri = Uri.fromFile(f)
            mediaScanIntent.setData(contentUri)
            activity.sendBroadcast(mediaScanIntent)
            return true
        }
        return false
    }

    fun createQRCODE(schema: Schema): Bitmap {
        return net.glxn.qrgen.android.QRCode.from(schema).bitmap()
    }

    fun generateQRCode(text: String): Bitmap {
        val bitmap = Bitmap.createBitmap(WIDTH_QR_IMAGE, HEIGHT_QR_IMAGE, Bitmap.Config.ARGB_8888)
        val codeWriter = MultiFormatWriter()
        try {
            val hints = HashMap<EncodeHintType, String>()
            hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
            val bitMatrix =
                codeWriter.encode(
                    text,
                    BarcodeFormat.QR_CODE,
                    WIDTH_QR_IMAGE,
                    HEIGHT_QR_IMAGE,
                    hints
                )
            for (x in 0 until WIDTH_QR_IMAGE) {
                for (y in 0 until HEIGHT_QR_IMAGE) {
                    val color = if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
                    bitmap.setPixel(x, y, color)
                }
            }
        } catch (e: WriterException) {
            Timber.d(e)
        }
        return bitmap
    }

    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun getAddress(context: Context, lat: Double, long: Double): String {
        return try {
            val geoCoder = Geocoder(context, Locale.getDefault())
            val address = geoCoder.getFromLocation(lat, long, MAX_RESULT_ADDRESS)
            address?.get(FIRST_ADDRESS_RESULT)
                ?.getAddressLine(FIRST_ADDRESS_RESULT) ?: TEXT_EMPTY
        } catch (e: Exception) {
            TEXT_EMPTY
        }
    }

    fun Toast.showNotification(activity: Activity, type: Int, message: String) {
        val layout = activity.layoutInflater.inflate (
            R.layout.layout_popup_message,
            activity.findViewById(R.id.layout_notification_container)
        )

        // set the text of the TextView of the message
        val textView = layout.findViewById<TextView>(R.id.tv_message_notification)
        textView.text = message
        val iconNotification = layout.findViewById<ImageView>(R.id.icon_notification)
        val layoutContent = layout.findViewById<LinearLayout>(R.id.layout_notification_content)
        when(type) {
            PopUpMessage.SUCCESS.ordinal -> {
                iconNotification.setImageResource(R.drawable.ic_checked_1)
                layoutContent.setBackgroundColor(ContextCompat.getColor(activity.applicationContext, R.color.success1))
            }
            PopUpMessage.FAIL.ordinal -> {
                iconNotification.setImageResource(R.drawable.ic_fail)
                layoutContent.setBackgroundColor(ContextCompat.getColor(activity.applicationContext, R.color.error1))
            }
        }

        // use the application extension function
        this.apply {
            setGravity(Gravity.TOP or Gravity.FILL_HORIZONTAL, 0, 12)
            duration = Toast.LENGTH_SHORT
            view = layout
            show()
        }
    }

    fun getNameEncryption(id: Int): String {
        return when (id) {
            Barcode.WiFi.TYPE_OPEN -> WIFI_ENCRYPTION_TYPE_NONE
            Barcode.WiFi.TYPE_WPA -> WIFI_ENCRYPTION_TYPE_WPA
            Barcode.WiFi.TYPE_WEP -> WIFI_ENCRYPTION_TYPE_WEP
            else -> ""
        }
    }

    fun Fragment.vibratePhone() {
        val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(200)
        }
    }
}