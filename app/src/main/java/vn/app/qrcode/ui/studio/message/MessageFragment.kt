package vn.app.qrcode.ui.studio.message

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
import vn.app.qrcode.databinding.FragmentCreatorMessageBinding
import vn.app.qrcode.ui.studio.ItemInputFieldAdapter
import vn.app.qrcode.ui.studio.ItemInputFieldListener
import vn.app.qrcode.utils.AppUtils.hideKeyboard

class MessageFragment :
    BaseMVVMFragment<CommonEvent, FragmentCreatorMessageBinding, MessageViewModel>() {

    companion object {
        fun newInstance() = MessageFragment()
    }

    lateinit var qrCodeTypeArg: QRCodeType
    private val args: MessageFragmentArgs by navArgs()

    override val layoutId: Int
        get() = R.layout.fragment_creator_message
    override val viewModel: MessageViewModel by viewModel()

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
            messageFragment = this@MessageFragment
            messageViewModel = viewModel
        }
        setupViewEvent()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val adapter = ItemInputFieldAdapter(requireActivity(),ItemInputFieldListener { phone ->
            viewModel.removePhoneNumber(phone)
        })

        viewDataBinding.rvPhones.adapter = adapter

        viewModel.listPhoneNumber.observe(this.viewLifecycleOwner) {
            adapter.submitList(it)
            viewDataBinding.imgAddPhoneField.visibility =
                if (viewModel.canAddPhoneNumber()) View.VISIBLE else View.GONE
        }
    }

    private fun setupViewEvent() {
        viewDataBinding.apply {
            val maxLengthTextArea = resources.getInteger(R.integer.max_length_text_area)
            textSize.text = maxLengthTextArea.toString()
            editTextMessage.onTextChanged {
                textSize.text = (maxLengthTextArea - editTextMessage.text.trim().length).toString()
            }
        }
    }

    fun onBack() {
        findNavController().popBackStack()
    }

    fun createQrCode() {
        val phone = viewDataBinding.editTextPhone.text.trim().toString()
        viewModel.setFirstPhoneNumber(phone)
        val message = viewDataBinding.editTextMessage.text.trim().toString()

        if (isMessageFormValidate(message)) {
            viewModel.addNewQrCode(
                qrCodeTypeArg,
                message = message,
            )
            val action = MessageFragmentDirections.actionMessageFragmentToResultCreatorFragment(
                viewModel.messageResultCreator
            )
            findNavController().navigate(action)
        }
    }

    private fun isMessageFormValidate(message: String): Boolean {
        val isPhoneValidate = viewModel.isPhonesValidate()
        val isMessageNoEmpty = isMessageNotEmpty(message)
        return isPhoneValidate && isMessageNoEmpty
    }

    private fun isMessageNotEmpty(message: String): Boolean {
        if(message.isEmpty()) {
            viewDataBinding.tvErrorMessage.text = resources.getString(R.string.txt_error_required)
            return false
        }
        viewDataBinding.tvErrorMessage.text = TEXT_EMPTY
        return true
    }

    fun addPhone() {
        viewModel.addPhoneNumber()
    }

    override fun onStop() {
        super.onStop()
        hideKeyboard()
    }
}
