package vn.app.qrcode.ui.home.camera

import android.content.Intent
import android.graphics.Bitmap
import androidx.annotation.MainThread
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.base.common.base.viewmodel.BaseViewModel
import com.base.common.base.viewmodel.CommonEvent
import com.google.mlkit.vision.barcode.common.Barcode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import vn.app.qrcode.data.constant.REQUEST_CODE_COMMON
import vn.app.qrcode.data.constant.WorkflowState
import vn.app.qrcode.data.model.QRCodeCreator
import vn.app.qrcode.data.repository.QrCodeRepository
import vn.app.qrcode.ui.home.camera.mlkit.barcodedetection.BarcodeField
import vn.app.qrcode.utils.AppUtils.md5

/** View model for handling application workflow based on camera preview.  */
class CameraViewModel(
    private val qrCodeRepository: QrCodeRepository
) : BaseViewModel<CommonEvent>() {

    val workflowState = MutableLiveData<WorkflowState>()
    val detectedBarcode = MutableLiveData<Barcode>()

    var textActionBtn: Int? = null
    var intentOtherApp: Intent? = null
    var inputImage: Bitmap? = null
    var barcodeFieldList: ArrayList<BarcodeField>? = null
    var requestCode: Int = REQUEST_CODE_COMMON
    var isCameraFragmentActive = false
    var qrCodeCreator: QRCodeCreator? = null

    private val objectIdsToSearch = HashSet<Int>()
    var isCameraLive = false
        private set

    /**
     * State set of the application workflow.
     */

    @MainThread
    fun setWorkflowState(workflowState: WorkflowState) {
        this.workflowState.value = workflowState
    }

    fun markCameraLive() {
        isCameraLive = true
        objectIdsToSearch.clear()
    }

    fun markCameraFrozen() {
        isCameraLive = false
    }

    fun setQRCodeCreator(item: QRCodeCreator) {
        qrCodeCreator = item
    }

    fun createNewQRCode() {
        viewModelScope.launch {
            qrCodeCreator?.let { qrCodeRepository.insert(it) }
            withContext(Dispatchers.IO) {
                qrCodeCreator =
                    qrCodeCreator?.let { qrCodeRepository.getQrCodeByHashCode(it.hashCode) }
            }
        }
    }

    fun updateQRCode() {
        viewModelScope.launch {
            qrCodeCreator?.let { qrCodeRepository.update(it) }
        }
    }

    fun checkQRCodeExist(): QRCodeCreator {
        return qrCodeRepository.getQrCodeByHashCode(
            md5(qrCodeCreator?.rawValue ?: "") ?: ""
        )
    }

}
