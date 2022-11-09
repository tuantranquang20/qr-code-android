package vn.app.qrcode.ui.studio.text

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.base.common.base.fragment.BaseMVVMFragment
import com.base.common.base.viewmodel.CommonEvent
import com.base.common.utils.ext.onTextChanged
import org.koin.androidx.viewmodel.ext.android.viewModel
import vn.app.qrcode.R
import vn.app.qrcode.data.constant.TEXT_EMPTY
import vn.app.qrcode.data.model.QRCodeType
import vn.app.qrcode.data.model.qrcodeobject.TextQRCode
import vn.app.qrcode.databinding.FragmentCreatorTextBinding
import vn.app.qrcode.ui.home.camera.mlkit.barcodedetection.BarcodeField
import vn.app.qrcode.utils.AppUtils
import vn.app.qrcode.utils.AppUtils.hideKeyboard

class TextFragment : BaseMVVMFragment<CommonEvent, FragmentCreatorTextBinding, TextViewModel>() {

    companion object {
        fun newInstance() = TextFragment()
    }

    lateinit var qrCodeTypeArg: QRCodeType
    private val args: TextFragmentArgs by navArgs()

    override val layoutId: Int
        get() = R.layout.fragment_creator_text
    override val viewModel: TextViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        qrCodeTypeArg = args.qrCodeType
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewDataBinding.apply {
            qrCodeType = qrCodeTypeArg
        }
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding.apply {
            textFragment = this@TextFragment
        }

        setUpViewEvent()
    }

    private fun setUpViewEvent() {
        viewDataBinding.apply {
            val maxLengthTextArea = resources.getInteger(R.integer.max_length_text_area)
            textSize.text = maxLengthTextArea.toString()
            editTextQrCode.onTextChanged {
                textSize.text = (maxLengthTextArea - editTextQrCode.text.trim().length).toString()
            }
        }
    }

    fun onBack() {
        findNavController().popBackStack()
    }

    fun createTextQrCode() {
        val text = viewDataBinding.editTextQrCode.text.trim().toString()
        if (text.isNotBlank() || text.isNotEmpty()) {
            viewDataBinding.tvErrorText.text = TEXT_EMPTY
            val messageBarcode = ArrayList<BarcodeField>()
            messageBarcode.add(
                BarcodeField(
                    getString(R.string.text_display),
                    text
                )
            )

            val textQrCode = TextQRCode(text)

            val textResultCreator = AppUtils.getNewResultCreator(
                barcodeFieldList = messageBarcode,
                type = qrCodeTypeArg,
                displayName = resources.getString(R.string.text_display),
                rawValue = textQrCode.getRawValue(),
                title = resources.getString(R.string.result_creator_text),
                content = textQrCode.toJson()
            )
            findNavController().navigate(
                TextFragmentDirections.actionTextFragmentToResultCreatorFragment(textResultCreator)
            )
        } else {
            viewDataBinding.tvErrorText.text = getString(R.string.txt_error_required)
        }
    }

    override fun onStop() {
        super.onStop()
        hideKeyboard()
    }
}
