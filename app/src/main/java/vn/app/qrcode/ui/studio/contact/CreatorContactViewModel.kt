package vn.app.qrcode.ui.studio.contact

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.base.common.base.viewmodel.BaseViewModel
import com.base.common.base.viewmodel.CommonEvent
import vn.app.qrcode.R
import vn.app.qrcode.data.constant.InputFieldData
import vn.app.qrcode.data.constant.InputFieldType
import vn.app.qrcode.data.constant.MAX_FIELD_TO_MESSAGE
import vn.app.qrcode.data.model.InputField
import vn.app.qrcode.data.model.QRCodeCreator
import vn.app.qrcode.ui.home.camera.mlkit.barcodedetection.BarcodeField

class CreatorContactViewModel(private val application: Application) : BaseViewModel<CommonEvent>() {
    var qrCodeCreator: QRCodeCreator? = null
    var rawValue: String = ""
    val barcodeFieldList = ArrayList<BarcodeField>()

    var isValid = true
    var rawValueOfNewField = ""
    var birthday = ""
    var position = ""
    var facebook = ""
    var note = ""
    var numberPhoneSelected = ""

    // variable select field in bottom dialog
    var listNewEditTextCache: MutableList<InputField> = mutableListOf()
    private var listNewEditTextData = InputFieldData.lisNewtFiledContact

    private var _listNewEditText = MutableLiveData<MutableList<InputField?>>()
    var listNewEditText: LiveData<MutableList<InputField?>> = _listNewEditText

    // variable add field to screen
    private var inputDefault = InputFieldData.inputFieldDefault
    private val _listInputEditText = MutableLiveData<MutableList<InputField>>()
    var listInput: LiveData<MutableList<InputField>> = _listInputEditText

    fun selectFieldToAddScreen(field: InputField) {
        if (listNewEditTextCache.contains(field)) {
            listNewEditTextCache.remove(field)
        } else {
            listNewEditTextCache.add(field)
        }
    }

    init {
        _listInputEditText.value = mutableListOf()
        _listNewEditText.value = listNewEditTextData.toMutableList()

    }

    fun handleAddFieldToScreen() {
        addInputField(listNewEditTextCache)
        listNewEditTextCache = mutableListOf()
    }

    private fun removeNewFieldFromBottom(field: InputField) {
        _listNewEditText.value?.remove(field)
        _listInputEditText.value = _listInputEditText.value
    }

    private fun addNewFieldFromBottom(field: InputField) {
        _listNewEditText.value?.add(field)
        _listInputEditText.value = _listInputEditText.value
    }

    // Handle add field to screen

    private fun getListInputFieldSize(): Int {
        return _listInputEditText.value?.size ?: 0
    }

    fun canAddInputField(): Boolean {
        return getListInputFieldSize() < MAX_FIELD_TO_MESSAGE
    }

    private fun addInputField(listNewFieldCache: MutableList<InputField>) {
        for (i in 0 until listNewFieldCache.size) {
            _listInputEditText.value?.add(newInputFieldDefault(listNewFieldCache[i].fieldType))
            _listInputEditText.value = _listInputEditText.value
            removeNewFieldFromBottom(listNewFieldCache[i])
        }
    }

    fun removeInputField(field: InputField) {
        _listInputEditText.value?.remove(field)
        _listInputEditText.value = _listInputEditText.value
        addNewFieldFromBottom(newInputFieldDefault(field.fieldType))
    }

    fun checkListMoreInput(): Boolean {
        var isValid = true
        rawValueOfNewField = ""
        resetNewFieldData()
        for (field in _listInputEditText.value!!) {
            field.fieldValue = field.fieldValue.trim()
            if (field.fieldValue.isNotEmpty()) {
                field.error = null
            } else {
                field.error = application.getString(field.stringErrorId)
                isValid = false
            }
            when (field.fieldType) {
                InputFieldType.BIRTHDAY.ordinal -> {
                    birthday = field.fieldValue
                    barcodeFieldList.add(
                        BarcodeField(
                            application.getString(R.string.label_contact_birthday), birthday
                        )
                    )
                }
                InputFieldType.FACEBOOK.ordinal -> {
                    facebook = field.fieldValue
                    barcodeFieldList.add(
                        BarcodeField(
                            application.getString(R.string.label_contact_facebook), facebook
                        )
                    )
                }
                InputFieldType.POSITION.ordinal -> {
                    position = field.fieldValue
                    barcodeFieldList.add(
                        BarcodeField(
                            application.getString(R.string.label_contact_position), position
                        )
                    )
                }
                InputFieldType.NOTE.ordinal -> {
                    note = field.fieldValue
                    barcodeFieldList.add(
                        BarcodeField(
                            application.getString(R.string.label_contact_note), note
                        )
                    )
                }
            }
        }
        _listInputEditText.value = _listInputEditText.value

        return isValid
    }

    private fun newInputFieldDefault(fieldType: Int): InputField {
        return inputDefault[fieldType]!!.copy()
    }

    private fun resetNewFieldData() {
        facebook = ""
        birthday = ""
        position = ""
        note = ""
        barcodeFieldList.clear()
    }

    fun resetRecyclerView() {
        listNewEditTextCache = mutableListOf()

        _listNewEditText = MutableLiveData<MutableList<InputField?>>()
        _listNewEditText.value = listNewEditTextData.toMutableList()
        listNewEditText = _listNewEditText

        _listInputEditText.value = mutableListOf()
        listInput = _listInputEditText
    }
}
