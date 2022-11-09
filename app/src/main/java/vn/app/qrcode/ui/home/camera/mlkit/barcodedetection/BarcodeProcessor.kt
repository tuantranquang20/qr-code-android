package vn.app.qrcode.ui.home.camera.mlkit.barcodedetection

import android.util.Log
import androidx.annotation.MainThread
import vn.app.qrcode.ui.home.camera.mlkit.camera.CameraReticleAnimator
import vn.app.qrcode.ui.home.camera.mlkit.camera.FrameProcessorBase
import vn.app.qrcode.ui.home.camera.mlkit.camera.GraphicOverlay
import vn.app.qrcode.ui.home.camera.CameraViewModel
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import vn.app.qrcode.data.constant.WorkflowState
import vn.app.qrcode.ui.home.camera.mlkit.InputInfo
import java.io.IOException

/** A processor to run the barcode detector.  */
class BarcodeProcessor(graphicOverlay: GraphicOverlay, private val cameraViewModel: CameraViewModel) :
    FrameProcessorBase<List<Barcode>>() {

    private val scanner = BarcodeScanning.getClient()
    private val cameraReticleAnimator: CameraReticleAnimator = CameraReticleAnimator(graphicOverlay)

    override fun detectInImage(image: InputImage): Task<List<Barcode>> =
        scanner.process(image)

    @MainThread
    override fun onSuccess(
        inputInfo: InputInfo,
        results: List<Barcode>,
        graphicOverlay: GraphicOverlay
    ) {

//        if (!workflowModel.isCameraLive) return

        // Picks the barcode, if exists, that covers the center of graphic overlay.

        val barcodeInCenter = results.firstOrNull { barcode ->
            val boundingBox = barcode.boundingBox ?: return@firstOrNull false
            val box = graphicOverlay.translateRect(boundingBox)
            box.contains(graphicOverlay.width / 2f, graphicOverlay.height / 2f)
        }

        graphicOverlay.clear()
        if (barcodeInCenter == null) {
            cameraReticleAnimator.start()
            graphicOverlay.add(BarcodeReticleGraphic(graphicOverlay, cameraReticleAnimator))
            cameraViewModel.setWorkflowState(WorkflowState.DETECTING)
        } else {
            cameraViewModel.inputImage = inputInfo.getBitmap()
            cameraReticleAnimator.cancel()
            cameraViewModel.setWorkflowState(WorkflowState.DETECTED)
            cameraViewModel.detectedBarcode.value = barcodeInCenter
            cameraViewModel.inputImage = inputInfo.getBitmap()
        }
        graphicOverlay.invalidate()
    }

    override fun onFailure(e: Exception) {
        Log.e(TAG, "Barcode detection failed!", e)
    }

    override fun stop() {
        super.stop()
        try {
            scanner.close()
        } catch (e: IOException) {
            Log.e(TAG, "Failed to close barcode detector!", e)
        }
    }

    companion object {
        private const val TAG = "BarcodeProcessor"

    }
}
