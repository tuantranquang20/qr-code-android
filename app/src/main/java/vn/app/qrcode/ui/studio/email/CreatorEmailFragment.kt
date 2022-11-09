package vn.app.qrcode.ui.studio.email

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.view.isInvisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.base.common.base.fragment.BaseMVVMFragment
import com.base.common.base.viewmodel.CommonEvent
import com.base.common.utils.ext.onTextChanged
import kotlinx.android.synthetic.main.layout_header_app_bar.btnAction
import kotlinx.android.synthetic.main.layout_header_app_bar.btnBack
import kotlinx.android.synthetic.main.layout_header_app_bar.tvTitle
import org.koin.androidx.viewmodel.ext.android.viewModel
import vn.app.qrcode.R
import vn.app.qrcode.data.constant.QrCodeType
import vn.app.qrcode.data.constant.REGEX_EMAIL
import vn.app.qrcode.data.model.QRCodeType
import vn.app.qrcode.data.model.ResultCreator
import vn.app.qrcode.data.model.qrcodeobject.EmailQRCode
import vn.app.qrcode.databinding.FragmentCreatorEmailBinding
import vn.app.qrcode.ui.home.camera.mlkit.barcodedetection.BarcodeField
import vn.app.qrcode.ui.studio.ItemInputFieldAdapter
import vn.app.qrcode.ui.studio.ItemInputFieldListener
import vn.app.qrcode.utils.AppUtils
import vn.app.qrcode.utils.AppUtils.hideKeyboard

class CreatorEmailFragment :
    BaseMVVMFragment<CommonEvent, FragmentCreatorEmailBinding, CreatorEmailViewModel>() {

    companion object {
        fun newInstance() = CreatorEmailFragment()
    }

    lateinit var qrCodeTypeArg: QRCodeType
    private val args: CreatorEmailFragmentArgs by navArgs()

    override val layoutId: Int
        get() = R.layout.fragment_creator_email
    override val viewModel: CreatorEmailViewModel by viewModel()
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
        setupRecyclerView()
    }

    private fun setupViewEvent() {
        viewDataBinding.apply {
            tvTitle.text = getString(R.string.create_email_title)
            btnBack.setOnClickListener {
                findNavController().popBackStack()
            }

            btnAction.isInvisible = true

            val maxLengthTextArea = resources.getInteger(R.integer.max_length_text_area)
            textSize.text = maxLengthTextArea.toString()
            editTextMessage.onTextChanged {
                textSize.text =
                    (maxLengthTextArea - editTextMessage.text.toString().length).toString()
            }

            imgAddAddressField.setOnClickListener {
                viewModel.addInputField()
            }

            btnCreate.setOnClickListener {
                handleClickCreate()
            }
        }
    }

    private fun setupRecyclerView() {
        val adapter = ItemInputFieldAdapter(requireActivity(), ItemInputFieldListener { field ->
            viewModel.removeInputField(field)
        })

        viewDataBinding.rvAddress.adapter = adapter

        viewModel.listInputField.observe(this.viewLifecycleOwner) {
            adapter.submitList(it)
            viewDataBinding.imgAddAddressField.visibility =
                if (viewModel.canAddInputField()) View.VISIBLE else View.GONE
        }
    }

    private fun validateWhenCreate(): Boolean {
        viewDataBinding.apply {
            val isListMoreEmailValidate = viewModel.isListMoreEmailValidate()
            val isEmailValidate = isEmailValidate(editTextAddress.text.trim().toString())
            val isSubjectNotEmpty =
                isValueNotEmpty(editTextSubject.text.trim().toString(), tvErrorSubject)
            val isMessageNotEmpty =
                isValueNotEmpty(editTextMessage.text.trim().toString(), tvErrorMessage)

            return isEmailValidate && isListMoreEmailValidate && isSubjectNotEmpty && isMessageNotEmpty
        }
    }

    private fun handleClickCreate() {
        if (validateWhenCreate()) {
            viewDataBinding.apply {
                val email = editTextAddress.text.trim().toString()
                val subject = editTextSubject.text.trim().toString()
                val body = editTextMessage.text.trim().toString()

                val addressList = mutableListOf<String>()
                for (field in viewModel.listInputField.value!!) {
                    addressList.add(field.fieldValue)
                }

                val emailQrCode = EmailQRCode(email, addressList.joinToString(), subject, body)
                addressList.add(0, email)

                barcodeFieldList.add(
                    BarcodeField(
                        getString(R.string.hint_email_address), addressList.joinToString()
                    )
                )

                if (subject != "") {
                    barcodeFieldList.add(
                        BarcodeField(
                            getString(R.string.hint_email_subject), subject

                        )
                    )
                }

                if (body != "") {
                    barcodeFieldList.add(
                        BarcodeField(
                            getString(R.string.label_email_message), body

                        )
                    )
                }

                val resultCreator = ResultCreator(
                    barcodeFieldList = barcodeFieldList,
                    nameItemResultCreator = addressList.joinToString(),
                    typeItemResultCreator = QrCodeType.EMAIL.ordinal,
                    rawValue = emailQrCode.getRawValue(),
                    image = AppUtils.generateQRCode(emailQrCode.getRawValue()),
                    title = getString(R.string.result_creator_email),
                    content = emailQrCode.toJson()
                )

                findNavController().navigate(
                    CreatorEmailFragmentDirections.actionCreatorEmailFragmentToResultCreatorFragment(
                        resultCreator
                    )
                )
            }
        } else {
            viewModel.isValid = true
        }
    }

    private fun isEmailValidate(email: String): Boolean {
        return isValueNotEmpty(
            email,
            viewDataBinding.tvErrorAddress
        ) && isValidateField(
            viewDataBinding.tvErrorAddress,
            email,
            REGEX_EMAIL,
            R.string.txt_error_invalid
        )
    }

    private fun isValidateField(
        error: TextView,
        fieldValue: String,
        regex: Regex,
        idErrorText: Int
    ): Boolean {
        if (!fieldValue.matches(regex)) {
            error.text = getString(idErrorText)
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
