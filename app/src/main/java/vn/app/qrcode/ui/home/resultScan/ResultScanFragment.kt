package vn.app.qrcode.ui.home.resultScan

import android.content.ClipData
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.ClipboardManager
import android.view.View
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.base.common.base.fragment.BaseMVVMFragment
import com.base.common.base.viewmodel.CommonEvent
import com.base.common.utils.ext.setDebounceClickListener
import com.google.gson.Gson
import java.util.Date
import kotlinx.android.synthetic.main.layout_header_app_bar.btnAction
import kotlinx.android.synthetic.main.layout_header_app_bar.btnBack
import kotlinx.android.synthetic.main.layout_header_app_bar.tvTitle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber
import vn.app.qrcode.R
import vn.app.qrcode.data.constant.QrCodeType
import vn.app.qrcode.data.constant.SMS_BODY
import vn.app.qrcode.data.constant.SMS_TO
import vn.app.qrcode.data.model.qrcodeobject.MessageQRCode
import vn.app.qrcode.databinding.FragmentHomeResaultscansBinding
import vn.app.qrcode.ui.home.camera.CameraViewModel
import vn.app.qrcode.ui.home.camera.mlkit.barcodedetection.BarcodeFieldAdapter
import vn.app.qrcode.utils.AppUtils


class ResultScanFragment :
    BaseMVVMFragment<CommonEvent, FragmentHomeResaultscansBinding, CameraViewModel>() {
    override val layoutId: Int
        get() = R.layout.fragment_home_resaultscans
    override val viewModel: CameraViewModel by sharedViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewEvent()
    }

    private fun setupViewEvent() {
        viewDataBinding.apply {
            tvTitle.text = getString(R.string.result_screen_title)

            btnBack.setOnClickListener {
                findNavController().popBackStack()
            }

            barcodeFieldRecyclerView.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(activity)
                adapter = viewModel.barcodeFieldList?.let { BarcodeFieldAdapter(it) }
            }

            favoriteBtn.setOnClickListener {
                it.isSelected = !it.isSelected
                viewModel.qrCodeCreator?.isFavorite = it.isSelected
                viewModel.updateQRCode()
            }

            barcodeActionBtn.text = getString(viewModel.textActionBtn!!)
            barcodeActionBtn.setDebounceClickListener {
                when(viewModel.qrCodeCreator?.type) {
                    QrCodeType.TEXT.ordinal -> {
                        copyToClipboard()
                    }
                    QrCodeType.WIFI.ordinal -> {
                       connectWifi()
                    }
                    QrCodeType.MESSAGE.ordinal -> {
                        sendSMS()
                    }
                    else -> {
                        activity?.startActivityForResult(
                            Intent.createChooser(
                                viewModel.intentOtherApp,
                                getString(R.string.result_screen_open_with)
                            ),
                            viewModel.requestCode
                        )
                    }
                }
            }

            btnAction.setDebounceClickListener {
                val imgQrCode = viewModel.detectedBarcode.value?.rawValue?.let { rawValue ->
                    AppUtils.generateQRCode(rawValue)
                }
                if (imgQrCode != null) {
                    AppUtils.shareQrImage(requireActivity(), imgQrCode)
                }
            }
        }
        updateOrCreateQrCode()
    }

    private fun connectWifi() {
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val cn = ComponentName("com.android.settings", "com.android.settings.wifi.WifiSettings")
        intent.component = cn
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun updateOrCreateQrCode() {
        if (viewModel.detectedBarcode.value != null) {
            handleScanResult(true)
            lifecycleScope.launch {
                val qrCode =
                    withContext(Dispatchers.IO) {
                        viewModel.checkQRCodeExist()
                    }
                if (qrCode != null) {
                    viewModel.qrCodeCreator = qrCode
                    viewDataBinding.favoriteBtn.isSelected = qrCode.isFavorite
                    viewModel.qrCodeCreator?.lastScan = Date().time
                    viewModel.updateQRCode()
                } else {
                    viewModel.createNewQRCode()
                }
            }
        } else {
            handleScanResult(false)
        }
    }

    private fun handleScanResult(isSuccess: Boolean) {
        viewDataBinding.apply {
            scrollResultScan.isInvisible = !isSuccess
            textNotFound.isInvisible = isSuccess
            imgNotFound.isInvisible = isSuccess
        }
    }

    private fun sendSMS() {
        var phoneNumber = ""
        var message = ""
        try {
            val messageQRCode = Gson().fromJson(viewModel.qrCodeCreator?.content, MessageQRCode::class.java)
            phoneNumber = messageQRCode.phones
            message = messageQRCode.message
        } catch (e: Exception) {
            Timber.d(e)
        }

        val uri = Uri.parse("$SMS_TO${phoneNumber}")
        val intent = Intent(Intent.ACTION_SENDTO, uri)
        intent.putExtra(SMS_BODY, message)
        requireActivity().startActivityForResult(intent, 0)
    }

    private fun copyToClipboard () {
        try {
            val sdk = Build.VERSION.SDK_INT
            if (sdk < Build.VERSION_CODES.HONEYCOMB) {
                val clipboard =
                    context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.text = viewModel.barcodeFieldList?.get(0)?.value ?: ""
            } else {
                val clipboard =
                    context?.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                val clip = ClipData
                    .newPlainText(
                        requireContext().resources.getString(
                            R.string.copy_text
                        ), viewModel.barcodeFieldList?.get(0)?.value ?: ""
                    )
                clipboard.setPrimaryClip(clip)
            }
            Toast.makeText(
                activity,
                getString(R.string.copy_text_success),
                Toast.LENGTH_LONG
            ).show()
            true
        } catch (e: java.lang.Exception) {
            Toast.makeText(
                activity,
                getString(R.string.copy_text_fail),
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
