package vn.app.qrcode.data.constant

import android.text.InputType
import vn.app.qrcode.R
import vn.app.qrcode.data.model.InputField

enum class InputFieldType {
    EMAIL,
    PHONE,
    FACEBOOK,
    POSITION,
    BIRTHDAY,
    NOTE,
}

object InputFieldData {
    val inputFieldDefault = mapOf(
        InputFieldType.PHONE.ordinal to InputField(
            InputFieldType.PHONE.ordinal,
            fieldName = R.string.label_sms_to,
            fieldValue = TEXT_EMPTY,
            inputType = InputType.TYPE_CLASS_PHONE,
            regex = REGEX_PHONE,
            error = TEXT_EMPTY,
            maxLength = R.integer.max_length_text_name,
            stringErrorId = R.string.txt_error_invalid
        ),
        InputFieldType.EMAIL.ordinal to InputField(
            InputFieldType.EMAIL.ordinal,
            fieldName = R.string.hint_email_address,
            fieldValue = TEXT_EMPTY,
            inputType = InputType.TYPE_CLASS_TEXT,
            regex = REGEX_EMAIL,
            error = TEXT_EMPTY,
            maxLength = R.integer.max_length_text_email,
            stringErrorId = R.string.txt_error_invalid
        ),
        InputFieldType.FACEBOOK.ordinal to InputField(
            InputFieldType.FACEBOOK.ordinal,
            fieldName = R.string.label_contact_facebook,
            fieldValue = TEXT_EMPTY,
            inputType = InputType.TYPE_CLASS_TEXT,
            regex = REGEX_NOT_NULL,
            error = TEXT_EMPTY,
            maxLength = R.integer.max_length_text_url,
            stringErrorId = R.string.txt_error_required
        ),
        InputFieldType.POSITION.ordinal to InputField(
            InputFieldType.POSITION.ordinal,
            fieldName = R.string.label_contact_position,
            fieldValue = TEXT_EMPTY,
            inputType = InputType.TYPE_CLASS_TEXT,
            regex = REGEX_NOT_NULL,
            error = TEXT_EMPTY,
            maxLength = R.integer.max_length_text,
            stringErrorId = R.string.txt_error_required
        ),
        InputFieldType.BIRTHDAY.ordinal to InputField(
            InputFieldType.BIRTHDAY.ordinal,
            fieldName = R.string.label_contact_birthday,
            fieldValue = TEXT_EMPTY,
            inputType = InputType.TYPE_CLASS_DATETIME,
            regex = REGEX_NOT_NULL,
            error = TEXT_EMPTY,
            maxLength = R.integer.max_length_text_name,
            stringErrorId = R.string.txt_error_required
        ),
        InputFieldType.NOTE.ordinal to InputField(
            InputFieldType.NOTE.ordinal,
            fieldName = R.string.label_contact_note,
            fieldValue = TEXT_EMPTY,
            inputType = InputType.TYPE_CLASS_TEXT,
            regex = REGEX_NOT_NULL,
            error = TEXT_EMPTY,
            maxLength = R.integer.max_length_text_area,
            stringErrorId = R.string.txt_error_required
        ),

        )

    val lisNewtFiledContact: MutableList<InputField?> =
        mutableListOf(
            inputFieldDefault[InputFieldType.FACEBOOK.ordinal],
            inputFieldDefault[InputFieldType.POSITION.ordinal],
            inputFieldDefault[InputFieldType.BIRTHDAY.ordinal],
            inputFieldDefault[InputFieldType.NOTE.ordinal],
        )

}
