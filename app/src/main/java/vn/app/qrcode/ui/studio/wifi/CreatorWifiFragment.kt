package vn.app.qrcode.ui.studio.wifi

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.view.isInvisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.base.common.base.fragment.BaseMVVMFragment
import com.base.common.base.viewmodel.CommonEvent
import com.google.mlkit.vision.barcode.common.Barcode
import kotlinx.android.synthetic.main.layout_header_app_bar.btnAction
import kotlinx.android.synthetic.main.layout_header_app_bar.btnBack
import kotlinx.android.synthetic.main.layout_header_app_bar.tvTitle
import org.koin.androidx.viewmodel.ext.android.viewModel
import vn.app.qrcode.R
import vn.app.qrcode.data.constant.QrCodeType
import vn.app.qrcode.data.constant.REGEX_PASSWORD_WIFI
import vn.app.qrcode.data.model.QRCodeType
import vn.app.qrcode.data.model.ResultCreator
import vn.app.qrcode.data.model.qrcodeobject.WifiQRCode
import vn.app.qrcode.databinding.FragmentCreatorWifiBinding
import vn.app.qrcode.ui.home.camera.mlkit.barcodedetection.BarcodeField
import vn.app.qrcode.utils.AppUtils
import vn.app.qrcode.utils.AppUtils.hideKeyboard

class CreatorWifiFragment :
    BaseMVVMFragment<CommonEvent, FragmentCreatorWifiBinding, CreatorWifiViewModel>() {

    companion object {
        fun newInstance() = CreatorWifiFragment()
    }

    lateinit var qrCodeTypeArg: QRCodeType
    private val args: CreatorWifiFragmentArgs by navArgs()

    override val layoutId: Int
        get() = R.layout.fragment_creator_wifi
    override val viewModel: CreatorWifiViewModel by viewModel()
    private val barcodeFieldList = ArrayList<BarcodeField>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        qrCodeTypeArg = args.qrCodeType
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewDataBinding.apply {
            qrCodeType = qrCodeTypeArg
        }
        super.onViewCreated(view, savedInstanceState)
        setupViewEvent()
    }

    private fun setupViewEvent() {
        viewDataBinding.apply {
            tvTitle.text = getString(R.string.create_wifi_title)
            btnBack.setOnClickListener {
                findNavController().popBackStack()
            }

            btnAction.isInvisible = true

            radioGroupEncryptionType.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.radioWPA -> {
                        viewModel.setEncryptionType(Barcode.WiFi.TYPE_WPA)
                    }
                    R.id.radioWEP -> {
                        viewModel.setEncryptionType(Barcode.WiFi.TYPE_WEP)
                    }
                    R.id.radioNone -> {
                        viewModel.setEncryptionType(Barcode.WiFi.TYPE_OPEN)
                    }
                }
            }

            btnCreate.setOnClickListener {
                handleClickCreate()
            }
        }
    }

    private fun validateWhenCreate(): Boolean {
        viewDataBinding.apply {
            val ssid = editTextSsid.text.trim().toString()
            val password = editTextPassword.text.trim().toString()

            val isSsidNotEmpty = isValueNotEmpty(ssid, tvErrorSsid)
            val isPasswordNotEmpty = isValueNotEmpty(password, tvErrorPassword) &&
                    validateField(
                        tvErrorPassword,
                        password,
                        REGEX_PASSWORD_WIFI,
                        R.string.txt_error_password
                    )
            return isSsidNotEmpty && isPasswordNotEmpty
        }
    }

    private fun handleClickCreate() {
        if (validateWhenCreate()) {
            viewDataBinding.apply {
                val ssid = editTextSsid.text.trim().toString()
                val password = editTextPassword.text.trim().toString()
                val encryption = AppUtils.getNameEncryption(viewModel.encryptionType.value!!)

                barcodeFieldList.add(
                    BarcodeField(
                        getString(R.string.hint_wifi_ssid), ssid
                    )
                )

                barcodeFieldList.add(
                    BarcodeField(
                        getString(R.string.hint_wifi_password), password
                    )
                )
                barcodeFieldList.add(
                    BarcodeField(
                        getString(R.string.label_wifi_encryption), encryption
                    )
                )

                val wifiQRCode  =  WifiQRCode(ssid, password, encryption)
                viewModel.rawValue = wifiQRCode.getRawValue()

                val resultCreator = ResultCreator(
                    barcodeFieldList = barcodeFieldList,
                    nameItemResultCreator = ssid,
                    typeItemResultCreator = QrCodeType.WIFI.ordinal,
                    rawValue = wifiQRCode.getRawValue(),
                    image = AppUtils.generateQRCode(wifiQRCode.getRawValue()),
                    title = getString(R.string.result_creator_wifi),
                    content = wifiQRCode.toJson()
                )

                findNavController().navigate(
                    CreatorWifiFragmentDirections.actionCreatorWifiFragmentToResultCreatorFragment(
                        resultCreator
                    )
                )
            }
        } else {
            viewModel.isValid = true
        }
    }

    private fun validateField(
        error: TextView,
        value: String,
        regex: Regex,
        idErrorText: Int
    ): Boolean {
        if (!value.matches(regex)) {
            error.text = getString(idErrorText)
            viewModel.isValid = false
            error.visibility = View.VISIBLE
            return false
        }
        error.text = getString(R.string.txt_error_empty)
        error.visibility = View.GONE
        return true
    }

    private fun setErrorText(textView: TextView, error: String) {
        textView.visibility = if (error.isEmpty()) View.GONE else View.VISIBLE
        textView.text = error
    }

    private fun isValueNotEmpty(value: String, textView: TextView): Boolean {
        if (value.isEmpty()) {
            setErrorText(textView, getString(R.string.txt_error_required))
            return false
        }
        setErrorText(textView, getString(R.string.txt_error_empty))
        return true
    }

    override fun onStop() {
        super.onStop()
        hideKeyboard()
    }
}
