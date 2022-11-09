package vn.app.qrcode.ui.studio.message

import android.app.Application
import android.text.InputType
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.base.common.base.viewmodel.BaseViewModel
import com.base.common.base.viewmodel.CommonEvent
import vn.app.qrcode.R
import vn.app.qrcode.data.constant.InputFieldType
import vn.app.qrcode.data.constant.MAX_FIELD_TO_MESSAGE
import vn.app.qrcode.data.constant.REGEX_PHONE
import vn.app.qrcode.data.constant.TEXT_EMPTY
import vn.app.qrcode.data.model.InputField
import vn.app.qrcode.data.model.QRCodeType
import vn.app.qrcode.data.model.ResultCreator
import vn.app.qrcode.data.model.qrcodeobject.MessageQRCode
import vn.app.qrcode.ui.home.camera.mlkit.barcodedetection.BarcodeField
import vn.app.qrcode.utils.AppUtils

class MessageViewModel(
    private val application: Application,
) : BaseViewModel<CommonEvent>() {
    lateinit var messageResultCreator: ResultCreator

    private val _firstPhoneNumber = MutableLiveData<InputField>()
    val firstPhoneNumber: LiveData<InputField> = _firstPhoneNumber

    private val _listPhoneNumber = MutableLiveData<MutableList<InputField>>()
    val listPhoneNumber: LiveData<MutableList<InputField>> = _listPhoneNumber

    init {
        _listPhoneNumber.value = mutableListOf()
        _firstPhoneNumber.value = newPhoneNumber()
    }

    fun addPhoneNumber() {
        if (canAddPhoneNumber()) {
            _listPhoneNumber.value?.add(newPhoneNumber())
            _listPhoneNumber.value = _listPhoneNumber.value
        }
    }

    fun removePhoneNumber(field: InputField) {
        _listPhoneNumber.value?.remove(field)
        _listPhoneNumber.value = _listPhoneNumber.value
    }

    private fun getListPhoneNumberSize(): Int {
        return _listPhoneNumber.value?.size ?: 0
    }

    fun canAddPhoneNumber(): Boolean {
        return getListPhoneNumberSize() < MAX_FIELD_TO_MESSAGE
    }

    fun setFirstPhoneNumber(phone: String) {
        _firstPhoneNumber.value =
            InputField(
                fieldType = InputFieldType.PHONE.ordinal,
                fieldName = R.string.txt_hint_to,
                fieldValue = phone,
                inputType = InputType.TYPE_CLASS_PHONE,
                regex = REGEX_PHONE,
                error = null,
                maxLength = R.integer.max_length_text_name,
                stringErrorId = R.string.error_invalid
            )
    }

    private fun getNewResultCreator(
        barcodeFieldList: ArrayList<BarcodeField>,
        type: QRCodeType,
        displayName: String,
        rawValue: String,
        content: String
    ): ResultCreator {
        return ResultCreator(
            barcodeFieldList = barcodeFieldList,
            nameItemResultCreator = displayName,
            typeItemResultCreator = type.type,
            rawValue = rawValue,
            image = AppUtils.generateQRCode(rawValue),
            title = application.getString(R.string.result_creator_message),
            content = content
        )
    }

    fun addNewQrCode(
        type: QRCodeType,
        message: String,
    ) {
        val phones = mutableListOf<String>()
        val messageBarcode = ArrayList<BarcodeField>()

        phones.add(_firstPhoneNumber.value?.fieldValue ?: TEXT_EMPTY)

        for (field in _listPhoneNumber.value!!) {
            phones.add(field.fieldValue)
        }

        messageBarcode.add(
            BarcodeField(
                application.getString(R.string.label_sms_to),
                phones.joinToString()
            )
        )
        messageBarcode.add(
            BarcodeField(
                application.getString(R.string.label_sms_message),
                message
            )
        )

        val messageQrCode = MessageQRCode(
            phones = phones.joinToString(),
            message = message
        )

        messageResultCreator = getNewResultCreator(
            barcodeFieldList = messageBarcode,
            type = type,
            displayName = phones.joinToString(),
            rawValue = messageQrCode.getRawValue(),
            content = messageQrCode.toJson()
        )
    }

    fun isPhonesValidate(): Boolean {
        val isFirstPhoneValidate = isFirstPhoneValidate()
        val isListMorePhonesValidate = isListMorePhonesValidate()
        return isFirstPhoneValidate && isListMorePhonesValidate
    }

    private fun isFirstPhoneValidate(): Boolean {
        if (_firstPhoneNumber.value?.fieldValue.isNullOrEmpty()) {
            _firstPhoneNumber.value!!.error = application.getString(R.string.txt_error_required)
            return false
        }
        if (_firstPhoneNumber.value?.fieldValue?.matches(REGEX_PHONE) == false) {
            _firstPhoneNumber.value!!.error = application.getString(R.string.txt_error_phone)
            return false
        }
        _firstPhoneNumber.value!!.error = null
        return true
    }

    private fun isListMorePhonesValidate(): Boolean {
        var isValid = true
        for (field in _listPhoneNumber.value!!) {
            if (field.fieldValue.isEmpty()) {
                field.error = application.getString(R.string.txt_error_required)
                isValid = false
            } else if (field.fieldValue.matches(REGEX_PHONE)) {
                field.error = null
            } else {
                field.error = application.getString(R.string.txt_error_phone)
                isValid = false
            }
        }
        _listPhoneNumber.value = _listPhoneNumber.value
        return isValid
    }

    private fun newPhoneNumber(): InputField {
        return InputField(
            fieldType = InputFieldType.PHONE.ordinal,
            fieldName = R.string.txt_hint_to,
            fieldValue = TEXT_EMPTY,
            inputType = InputType.TYPE_CLASS_PHONE,
            regex = REGEX_PHONE,
            error = null,
            maxLength = R.integer.max_length_text_name,
            stringErrorId = R.string.error_invalid
        )
    }
}
