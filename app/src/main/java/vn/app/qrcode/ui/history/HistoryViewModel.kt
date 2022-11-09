package vn.app.qrcode.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.base.common.base.viewmodel.BaseViewModel
import com.base.common.base.viewmodel.CommonEvent
import kotlinx.coroutines.launch
import vn.app.qrcode.data.constant.QrCodeType
import vn.app.qrcode.data.model.QRCodeCreator
import vn.app.qrcode.data.repository.QrCodeRepository

class HistoryViewModel(private val qrCodeRepository: QrCodeRepository) :
    BaseViewModel<CommonEvent>() {

    private val _listFilterType = MutableLiveData<MutableList<Int>>()
    val listFilterType: LiveData<MutableList<Int>> = _listFilterType

    private val _listSelectQrItem = MutableLiveData<MutableList<Long>>()
    val listSelectQrItem: LiveData<MutableList<Long>> = _listSelectQrItem

    val listQrCodeHistory: LiveData<List<QRCodeCreator>> = _listFilterType.switchMap { typeList ->
        liveData {
            if (typeList.isNullOrEmpty() || typeList.contains(QrCodeType.ALL.ordinal)) {
                val data = qrCodeRepository.getAllQrCodeHistory()
                emitSource(data)
            } else {
                val data = qrCodeRepository.getQrCodesHistoryByType(typeList)
                emitSource(data)
            }
        }
    }

    init {

        _listFilterType.value = mutableListOf()
        _listSelectQrItem.value = mutableListOf()
    }

    private fun updateQRCode(qrCodeCreator: QRCodeCreator) {
        viewModelScope.launch {
            qrCodeCreator.let { qrCodeRepository.update(it) }
        }
    }

    fun deleteQRCode(qrCodeCreator: QRCodeCreator) {
        viewModelScope.launch {
            if (qrCodeCreator.createdBy == null) {
                qrCodeRepository.delete(qrCodeCreator)
            } else {
                qrCodeCreator.lastScan = null
                qrCodeRepository.update(qrCodeCreator)
            }
        }
    }

    fun favorite(qrCodeCreator: QRCodeCreator) {
        qrCodeCreator.isFavorite = !qrCodeCreator.isFavorite
        updateQRCode(qrCodeCreator)
    }

    fun updateListTypeToFilter(listType: MutableList<Int>) {
        val listTypeId = mutableListOf<Int>()
        listTypeId.addAll(listType)
        _listFilterType.value = listTypeId
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
            val listItemToDelete = mutableListOf<Long>()
            val listItemToUpdate = mutableListOf<QRCodeCreator>()
            listQrCodeHistory.value?.forEach {
                if (_listSelectQrItem.value?.contains(it.id) == true) {
                    if (it.createdBy == null) {
                        listItemToDelete.add(it.id)
                    } else {
                        it.lastScan = null
                        listItemToUpdate.add(it)
                    }
                }
            }
            qrCodeRepository.updateItems(listItemToUpdate)
            qrCodeRepository.deleteItems(listItemToDelete)
        }
    }
}
