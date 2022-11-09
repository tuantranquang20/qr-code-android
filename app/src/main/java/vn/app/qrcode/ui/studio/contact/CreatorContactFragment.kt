package vn.app.qrcode.ui.studio.contact

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.telephony.PhoneNumberUtils
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isInvisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.base.common.base.fragment.BaseMVVMFragment
import com.base.common.base.viewmodel.CommonEvent
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Calendar
import kotlinx.android.synthetic.main.layout_header_app_bar.btnAction
import kotlinx.android.synthetic.main.layout_header_app_bar.btnBack
import kotlinx.android.synthetic.main.layout_header_app_bar.tvTitle
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber
import vn.app.qrcode.R
import vn.app.qrcode.data.constant.FIRST_POSITION
import vn.app.qrcode.data.constant.FORMAT_DATE_DISPLAY
import vn.app.qrcode.data.constant.FORMAT_DATE_QRCODE
import vn.app.qrcode.data.constant.QrCodeType
import vn.app.qrcode.data.constant.REGEX_EMAIL
import vn.app.qrcode.data.constant.REGEX_PHONE
import vn.app.qrcode.data.constant.REQUEST_CODE_OPEN_CONTACT
import vn.app.qrcode.data.constant.TAG_HISTORY_FILTER
import vn.app.qrcode.data.constant.TEXT_EMPTY
import vn.app.qrcode.data.constant.TEXT_SPACE
import vn.app.qrcode.data.model.QRCodeType
import vn.app.qrcode.data.model.ResultCreator
import vn.app.qrcode.data.model.qrcodeobject.ContactQRCode
import vn.app.qrcode.databinding.FragmentCreatorContactBinding
import vn.app.qrcode.ui.home.camera.mlkit.barcodedetection.BarcodeField
import vn.app.qrcode.ui.studio.ItemInputFieldAdapter
import vn.app.qrcode.ui.studio.ItemInputFieldListener
import vn.app.qrcode.utils.AppUtils
import vn.app.qrcode.utils.AppUtils.hideKeyboard


class CreatorContactFragment :
    BaseMVVMFragment<CommonEvent, FragmentCreatorContactBinding, CreatorContactViewModel>() {

    companion object {
        fun newInstance() = CreatorContactFragment()
    }

    lateinit var qrCodeTypeArg: QRCodeType
    private val args: CreatorContactFragmentArgs by navArgs()

    override val layoutId: Int
        get() = R.layout.fragment_creator_contact
    override val viewModel: CreatorContactViewModel by sharedViewModel()
    lateinit var contactAddNewFieldDialogFragment: ContactAddNewFieldDialogFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        qrCodeTypeArg = args.qrCodeType
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.resetRecyclerView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewDataBinding.apply {
            qrCodeType = qrCodeTypeArg
        }
        super.onViewCreated(view, savedInstanceState)
        contactAddNewFieldDialogFragment = ContactAddNewFieldDialogFragment()
        setupViewEvent()
        setupRecyclerViewListField()
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.numberPhoneSelected != TEXT_EMPTY) {
            val number = viewModel.numberPhoneSelected.replace(TEXT_SPACE, TEXT_EMPTY)
            viewDataBinding.editTextNumber.setText(number, TextView.BufferType.EDITABLE);
            viewModel.numberPhoneSelected = TEXT_EMPTY
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK &&
            data != null
        ) {
            try {
                when (requestCode) {
                    REQUEST_CODE_OPEN_CONTACT -> {
                        val uri = data.data
                        val projection = arrayOf<String>(
                            ContactsContract.CommonDataKinds.Phone.NUMBER,
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                        )

                        var cursor: Cursor? = uri?.let {
                            activity?.contentResolver?.query(
                                it, projection,
                                null, null, null
                            )
                        }
                        cursor!!.moveToFirst()
                        val numberColumnIndex: Int =
                            cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                        val number: String = cursor.getString(numberColumnIndex)
                        val displayNameColumnIndex: Int =
                            cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                        val name = cursor.getString(displayNameColumnIndex)

                        viewDataBinding.editTextName.setText(name)
                        viewModel.numberPhoneSelected = number

                        // Set up the projection
                        val projectionData = arrayOf(
                            ContactsContract.Data.DISPLAY_NAME,
                            ContactsContract.Contacts.Data.DATA1,
                            ContactsContract.Contacts.Data.MIMETYPE
                        )
                        // Query ContactsContract.Data
                        cursor = activity?.contentResolver?.query(
                            ContactsContract.Data.CONTENT_URI, projectionData,
                            ContactsContract.Data.DISPLAY_NAME + " = ?",
                            arrayOf(name),
                            null)

                        if (cursor != null) {
                            if (cursor.moveToFirst()) {
                                // Get the indexes of the MIME type and data
                                val mimeIdx = cursor.getColumnIndex(
                                    ContactsContract.Contacts.Data.MIMETYPE
                                )
                                val dataIdx = cursor.getColumnIndex(
                                    ContactsContract.Contacts.Data.DATA1
                                )
                                val emails = mutableListOf<String>()
                                // Match the data to the MIME type, store in variables
                                do {
                                    val mime = cursor.getString(mimeIdx);
                                    if (ContactsContract.CommonDataKinds.Email
                                            .CONTENT_ITEM_TYPE.equals(mime, ignoreCase = true)
                                    ) {
                                        val email = cursor.getString(dataIdx);
                                        emails.add(email)
                                    }
                                } while (cursor.moveToNext())

                                if (emails.isNotEmpty()) {
                                    viewDataBinding.editTextEmail.setText(emails[0])
                                }
                            }
                        }
                    }
                    else -> {
                        super.onActivityResult(requestCode, resultCode, data)
                    }
                }
            } catch (e: Exception) {
                Timber.d(e)
            }

        }
    }


    private fun setupViewEvent() {
        viewDataBinding.apply {
            tvTitle.text = getString(R.string.create_contact_title)
            btnBack.setOnClickListener {
                findNavController().popBackStack()
            }

            btnAction.isInvisible = true

            addNewFieldBtn.setOnClickListener {
                showListFilter()
            }

            imgOpenContact.setOnClickListener {
                val uri: Uri = Uri.parse("content://contacts")
                val intent = Intent(Intent.ACTION_PICK, uri)
                intent.type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
                startActivityForResult(intent, REQUEST_CODE_OPEN_CONTACT)
            }

            btnCreate.setOnClickListener {
                handleClickCreate()
            }
        }
    }

    private fun validateWhenCreate(): Boolean {
        viewDataBinding.apply {

            val name = editTextName.text.trim().toString()
            val numberPhone = editTextNumber.text.trim().toString()
            val email = editTextEmail.text.trim().toString()
            val location = editTextLocation.text.trim().toString()

            val isNameNotEmpty = isValueNotEmpty(name, tvErrorName)
            val isNumberPhoneValidate = isValueNotEmpty(numberPhone, tvErrorNumber) &&
                        isValidateField(tvErrorNumber, numberPhone, REGEX_PHONE, R.string.txt_error_phone)

            val isEmailValidate = isValueNotEmpty(email, tvErrorEmail) &&
                        isValidateField(tvErrorEmail, email, REGEX_EMAIL, R.string.txt_error_invalid)
            val isLocationNotEmpty = isValueNotEmpty(location, tvErrorLocation)

            val isMoreInfoValidate = viewModel.checkListMoreInput()
            return isNameNotEmpty && isNumberPhoneValidate &&
                    isEmailValidate && isLocationNotEmpty &&
                    isMoreInfoValidate
        }
    }

    private fun handleClickCreate() {
        if (validateWhenCreate()) {
            viewDataBinding.apply {
                val name = editTextName.text.trim().toString()
                val number = editTextNumber.text.trim().toString()
                val email = editTextEmail.text.trim().toString()
                val location = editTextLocation.text.trim().toString()
                val contactQRCode = ContactQRCode(name, number, email, location)

                viewModel.barcodeFieldList.add(
                    FIRST_POSITION,
                    BarcodeField(
                        getString(R.string.label_namecard_location), location
                    )
                )

                viewModel.barcodeFieldList.add(
                    FIRST_POSITION,
                    BarcodeField(
                        getString(R.string.hint_namecard_email), email
                    )
                )

                viewModel.barcodeFieldList.add(
                    FIRST_POSITION,
                    BarcodeField(
                        getString(R.string.hint_namecard_number), number
                    )
                )

                viewModel.barcodeFieldList.add(
                    FIRST_POSITION,
                    BarcodeField(
                        getString(R.string.hint_namecard_name), name
                    )
                )

                if (viewModel.birthday != TEXT_EMPTY) {
                    contactQRCode.birthday = AppUtils.convertDateString(viewModel.birthday, FORMAT_DATE_DISPLAY, FORMAT_DATE_QRCODE)
                }
                if (viewModel.position != TEXT_EMPTY) {
                    contactQRCode.jobTitle = viewModel.position
                }
                if (viewModel.facebook != TEXT_EMPTY) {
                    contactQRCode.url = viewModel.facebook
                }
                if (viewModel.note != TEXT_EMPTY) {
                    contactQRCode.note = viewModel.note
                }

                val resultCreator = ResultCreator(
                    barcodeFieldList = viewModel.barcodeFieldList,
                    nameItemResultCreator = name,
                    typeItemResultCreator = QrCodeType.CONTACT.ordinal,
                    rawValue = contactQRCode.getRawValue(),
                    image = AppUtils.generateQRCode(contactQRCode.getRawValue()),
                    title = getString(R.string.result_creator_contact),
                    content = contactQRCode.toJson()
                )

                findNavController().navigate(
                    CreatorContactFragmentDirections.actionCreatorContactFragmentToResultCreatorFragment(
                        resultCreator
                    )
                )
            }
        } else {
            viewModel.isValid = true
        }
    }

    private fun showListFilter() {
        activity?.supportFragmentManager?.let {
            contactAddNewFieldDialogFragment.show(it, TAG_HISTORY_FILTER)
        }
    }

    private fun setupRecyclerViewListField() {
        val adapter = ItemInputFieldAdapter(requireActivity(), ItemInputFieldListener { field ->
            viewModel.removeInputField(field)
        })

        viewDataBinding.rvAnotherField.adapter = adapter

        viewModel.listInput.observe(this.viewLifecycleOwner) {
            adapter.submitList(it)
            viewDataBinding.addNewFieldBtn.visibility =
                if (viewModel.canAddInputField()) View.VISIBLE else View.GONE
        }
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
