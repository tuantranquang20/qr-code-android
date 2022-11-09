package vn.app.qrcode.ui.studio

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.base.common.base.viewmodel.BaseViewModel
import com.base.common.base.viewmodel.CommonEvent
import kotlinx.coroutines.launch
import vn.app.qrcode.data.constant.CreatedBy
import vn.app.qrcode.data.constant.QrCodeType
import vn.app.qrcode.data.constant.QrCodeTypeData
import vn.app.qrcode.data.constant.TYPE_LIST_CODE
import vn.app.qrcode.data.model.QRCodeCreator
import vn.app.qrcode.data.model.QRCodeType
import vn.app.qrcode.data.model.qrcodeobject.TextQRCodeSchema
import vn.app.qrcode.data.repository.QrCodeRepository
import vn.app.qrcode.utils.AppUtils

class StudioViewModel(
    private val qrCodeRepository: QrCodeRepository
): BaseViewModel<CommonEvent>() {

    var radioQrCodeType = TYPE_LIST_CODE.YOUR_CODE

    private val _listSelectQrItem = MutableLiveData<MutableList<Long>>()
    val listSelectQrItem: LiveData<MutableList<Long>> = _listSelectQrItem

    private val listQrType = QrCodeTypeData.listQrCodeType

    private val _listQrCodeType = MutableLiveData<List<QRCodeType>>()
    val listQrCodeType: LiveData<List<QRCodeType>> = _listQrCodeType

    private val _listFilterType = MutableLiveData<MutableList<Int>>()
    val listFilterType: LiveData<MutableList<Int>> = _listFilterType
    var yourCodeFilterTypes = mutableListOf<Int>()
    var favoriteFilterTypes = mutableListOf<Int>()

    var listQrCode: LiveData<List<QRCodeCreator>> = _listFilterType.switchMap { typeList ->
        liveData {
            when (radioQrCodeType) {
                TYPE_LIST_CODE.YOUR_CODE -> {
                    if (typeList.isNullOrEmpty() || typeList.contains(QrCodeType.ALL.ordinal)) {
                        val data = qrCodeRepository.getAllQrCodeYourCode(CreatedBy.MANUAL_INPUT.ordinal)
                        emitSource(data)
                    } else {
                        val data = qrCodeRepository.getQrCodesYourCodeByType(typeList, CreatedBy.MANUAL_INPUT.ordinal)
                        emitSource(data)
                    }
                }
                TYPE_LIST_CODE.FAVORITE -> {
                    if (typeList.isNullOrEmpty() || typeList.contains(QrCodeType.ALL.ordinal)) {
                        val data = qrCodeRepository.getAllQrCodeFavorite()
                        emitSource(data)
                    } else {
                        val data = qrCodeRepository.getQrCodesFavoriteByType(typeList)
                        emitSource(data)
                    }
                }
            }
        }
    }

    init {
        _listFilterType.value = mutableListOf()
        _listQrCodeType.value = listQrType
        _listSelectQrItem.value = mutableListOf()
    }

    fun updateListTypeToFilter(listType: MutableList<Int>) {
        val listTypeId = mutableListOf<Int>()
        listTypeId.addAll(listType)
        if (radioQrCodeType == TYPE_LIST_CODE.FAVORITE) {
            favoriteFilterTypes = listTypeId.toMutableList()
        } else {
            yourCodeFilterTypes = listTypeId.toMutableList()
        }
        _listFilterType.value = listTypeId
    }

    fun switchListQrCode(type: TYPE_LIST_CODE) {
        radioQrCodeType = type
        clearListQrItemToDelete()
        if (radioQrCodeType == TYPE_LIST_CODE.FAVORITE) {
            _listFilterType.value = favoriteFilterTypes.toMutableList()
        } else {
            _listFilterType.value = yourCodeFilterTypes.toMutableList()
        }
    }

    fun clearList() {
        _listFilterType.value = mutableListOf()
    }

    fun shareImage(qrCodeCreator: QRCodeCreator, activity: Activity) {
        val img = AppUtils.createQRCODE(TextQRCodeSchema(qrCodeCreator.rawValue))
        AppUtils.shareQrImage(activity, img)
    }

    fun favorite(qrCodeCreator: QRCodeCreator) {
        qrCodeCreator.isFavorite = !qrCodeCreator.isFavorite
        updateQRCode(qrCodeCreator)
    }

    private fun updateQRCode(qrCodeCreator: QRCodeCreator) {
        viewModelScope.launch {
            qrCodeCreator.let { qrCodeRepository.update(it) }
        }
    }

    fun deleteQRCode(qrCodeCreator: QRCodeCreator) {
        viewModelScope.launch {
            qrCodeCreator.let { qrCodeRepository.delete(it) }
        }
    }

    fun updateListQrItemToDelete(listQrItems: MutableList<Long>) {
        _listSelectQrItem.value = listQrItems
    }

    fun removeSelectItem(item: QRCodeCreator) {
        if (_listSelectQrItem.value?.contains(item.id) == true) {
            _listSelectQrItem.value = _listSelectQrItem.value!!.filter {
                it != item.id
            } as MutableList<Long>
        }
    }

    fun clearListQrItemToDelete() {
        _listSelectQrItem.value = mutableListOf()
    }

    fun deleteItems() {
        viewModelScope.launch {
            listSelectQrItem.value?.let { qrCodeRepository.deleteItems(it) }
        }
    }

    fun updateFavoriteItems() {
        viewModelScope.launch {
            _listSelectQrItem.value?.let { qrCodeRepository.updateFavoriteItems(it, false) }
        }
    }

    fun isYourCode(): Boolean {
        return radioQrCodeType == TYPE_LIST_CODE.YOUR_CODE
    }
}
