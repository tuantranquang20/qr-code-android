package vn.app.qrcode.ui.studio.email

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.base.common.base.viewmodel.BaseViewModel
import com.base.common.base.viewmodel.CommonEvent
import vn.app.qrcode.R
import vn.app.qrcode.data.constant.InputFieldData
import vn.app.qrcode.data.constant.InputFieldType
import vn.app.qrcode.data.constant.MAX_FIELD_TO_MESSAGE
import vn.app.qrcode.data.constant.REGEX_EMAIL
import vn.app.qrcode.data.model.InputField
import vn.app.qrcode.data.model.QRCodeCreator
import vn.app.qrcode.data.repository.QrCodeRepository

class CreatorEmailViewModel(private val qrCodeRepository: QrCodeRepository,
                            private val application: Application,) : BaseViewModel<CommonEvent>() {
    var qrCodeCreator: QRCodeCreator?= null
    var rawValue: String =""
    var isValid = true

    private val inputFieldDefault = InputFieldData.inputFieldDefault

    private val _listInputField = MutableLiveData<MutableList<InputField>>()
    val listInputField: LiveData<MutableList<InputField>> = _listInputField

    init {
        _listInputField.value = mutableListOf()
    }

    fun addInputField() {
        if (canAddInputField()) {
            _listInputField.value?.add(newInputFieldDefault())
            _listInputField.value = _listInputField.value
        }
    }

    fun removeInputField(field: InputField) {
        _listInputField.value?.remove(field)
        _listInputField.value = _listInputField.value
    }

    private fun getListInputFieldSize(): Int {
        return _listInputField.value?.size ?: 0
    }

    fun canAddInputField(): Boolean {
        return getListInputFieldSize() < MAX_FIELD_TO_MESSAGE
    }

    fun isListMoreEmailValidate(): Boolean {
        var isValid = true
        for (field in _listInputField.value!!) {
            field.fieldValue = field.fieldValue.trim()
            if (isEmailEmpty(field)) {
                isValid = false
            } else if (isEmailInvalidate(field)) {
                isValid = false
            }
        }
        _listInputField.value = _listInputField.value
        return isValid
    }

    private fun isEmailEmpty(email: InputField): Boolean {
        if(email.fieldValue.isEmpty()) {
            email.error = application.getString(R.string.txt_error_required)
            return true
        }
        email.error = null
        return false
    }

    private fun isEmailInvalidate(email: InputField): Boolean {
        if(email.fieldValue.matches(REGEX_EMAIL)) {
            email.error = null
            return false
        }
        email.error = application.getString(R.string.txt_error_invalid)
        return true
    }

    private fun newInputFieldDefault(): InputField {
        return inputFieldDefault[InputFieldType.EMAIL.ordinal]?.copy(fieldName = R.string.hint_email_address)!!
    }
}
