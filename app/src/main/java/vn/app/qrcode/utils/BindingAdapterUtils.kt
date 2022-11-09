package vn.app.qrcode.utils

import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.TextAppearanceSpan
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.text.SimpleDateFormat
import java.util.Date
import vn.app.qrcode.R
import vn.app.qrcode.data.constant.FORMAT_DATETIME_DISPLAY
import vn.app.qrcode.data.constant.QrCodeType
import vn.app.qrcode.data.constant.QrItemType
import vn.app.qrcode.data.model.QRCodeCreator
import vn.app.qrcode.data.model.QRCodeType


@BindingAdapter("displayTime", "qrItemType")
fun bindServerDate(textView: TextView, qrCodeCreator: QRCodeCreator, qrItemType: QrItemType) {
    val timeDisplay = when (qrItemType) {
        QrItemType.HISTORY_ITEM -> {
            if(qrCodeCreator.lastScan != null) {
                qrCodeCreator.lastScan!!
            } else {
                qrCodeCreator.createdAt
            }
        }
        QrItemType.YOUR_CODE_ITEM -> {
            qrCodeCreator.createdAt
        }
        QrItemType.FAVORITE_ITEM -> {
            qrCodeCreator.createdAt
        }
    }
    textView.text = SimpleDateFormat(FORMAT_DATETIME_DISPLAY).format(Date(timeDisplay))
}

@BindingAdapter("iconQrCode")
fun bindIcon(imageView: ImageView, type: Int) {
    when (type) {
        QrCodeType.EMAIL.ordinal -> imageView.setImageResource(R.drawable.ic_email)
        QrCodeType.MESSAGE.ordinal -> imageView.setImageResource(R.drawable.ic_message)
        QrCodeType.CONTACT.ordinal -> imageView.setImageResource(R.drawable.ic_phone)
        QrCodeType.URL.ordinal -> imageView.setImageResource(R.drawable.ic_url)
        QrCodeType.WIFI.ordinal -> imageView.setImageResource(R.drawable.ic_wifi)
//        QrCodeType.LOCATION.ordinal -> imageView.setImageResource(R.drawable.ic_location)
        QrCodeType.EVENT.ordinal -> imageView.setImageResource(R.drawable.ic_event)
        QrCodeType.TEXT.ordinal -> imageView.setImageResource(R.drawable.ic_text)
        QrCodeType.NAME_CARD.ordinal -> imageView.setImageResource(R.drawable.ic_name_card)
        QrCodeType.FACEBOOK.ordinal -> imageView.setImageResource(R.drawable.ic_facebook)
        QrCodeType.TWITTER.ordinal -> imageView.setImageResource(R.drawable.ic_twitter)
        QrCodeType.INSTAGRAM.ordinal -> imageView.setImageResource(R.drawable.ic_instagram)
        else -> imageView.setImageResource(R.drawable.ic_text)
    }
}

@BindingAdapter("nameQrCode")
fun bindNameQrCode(textView: TextView, qrCodeType: QRCodeType) {
    if (qrCodeType.nameId == 0) return
    textView.text = textView.resources.getString(qrCodeType.nameId)
}

@BindingAdapter("tvToolbar")
fun bindTvToolBar(textView: TextView, idTittle: Int) {
    if (idTittle == 0) return
    textView.text = String.format(
        textView.resources.getString(R.string.txt_tool_bar),
        textView.resources.getString(idTittle)
    )
}

@BindingAdapter("hintEditTextUrl")
fun bindHintEditText(editText: EditText, type: Int) {
    when (type) {
        QrCodeType.URL.ordinal -> editText.setHint(R.string.txt_hint_url)
        QrCodeType.FACEBOOK.ordinal -> editText.setHint(R.string.txt_hint_fb_url)
        QrCodeType.TWITTER.ordinal -> editText.setHint(R.string.txt_hint_tw_url)
        QrCodeType.INSTAGRAM.ordinal -> editText.setHint(R.string.txt_hint_ig_url)
    }
}

@BindingAdapter("imgFooterUrl")
fun bindFooterImageUrl(imageView: ImageView, type: Int) {
    when (type) {
        QrCodeType.FACEBOOK.ordinal -> imageView.setImageResource(R.drawable.img_facebook)
        QrCodeType.TWITTER.ordinal -> imageView.setImageResource(R.drawable.img_twitter)
        QrCodeType.INSTAGRAM.ordinal -> imageView.setImageResource(R.drawable.img_instagram)
        else -> imageView.setImageResource(R.drawable.img_facebook)
    }
}

@BindingAdapter("textFooterUrl")
fun bindFooterUrlText(textView: TextView, qrCodeType: QRCodeType) {
    if (qrCodeType.nameId == 0) return

    val prefixText = textView.resources.getString(R.string.txt_prefix_footer_url)
    val contentText = String.format(
        textView.resources.getString(R.string.txt_subfix_footer_url),
        textView.resources.getString(qrCodeType.nameId)
    )
    val prefixContent = SpannableString(prefixText)
    prefixContent.setSpan(
        TextAppearanceSpan(textView.context, R.style.PrefixTextFooter),
        0,
        prefixText.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    val content = SpannableString(contentText)
    content.setSpan(
        TextAppearanceSpan(textView.context, R.style.ContentTextFooter),
        0,
        contentText.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    val builder = SpannableStringBuilder()
    builder.append(prefixContent)
    builder.append(" ")
    builder.append(content)

    textView.text = builder
}

@BindingAdapter("text_error")
fun bindTextError(textView: TextView, error: String?) {
    if (error.isNullOrEmpty()) {
        textView.visibility = View.GONE
    } else {
        textView.visibility = View.VISIBLE
        textView.text = error
    }
}

@BindingAdapter("max_length")
fun bindMaxLength(editText: EditText, length: Int) {
    val maxLength = editText.resources.getInteger(length)
    val filterArray = arrayOfNulls<InputFilter>(1)
    filterArray[0] = LengthFilter(maxLength)
    editText.filters = filterArray
}
