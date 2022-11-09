package vn.app.qrcode.ui.detail

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.base.common.base.fragment.BaseMVVMFragment
import com.base.common.base.viewmodel.CommonEvent
import com.base.common.utils.ext.setDebounceClickListener
import com.google.gson.Gson
import kotlinx.android.synthetic.main.layout_header_app_bar.btnAction
import kotlinx.android.synthetic.main.layout_header_app_bar.btnBack
import kotlinx.android.synthetic.main.layout_header_app_bar.tvTitle
import org.koin.androidx.viewmodel.ext.android.viewModel
import vn.app.qrcode.R
import vn.app.qrcode.data.constant.PopUpMessage
import vn.app.qrcode.data.constant.QrCodeType
import vn.app.qrcode.data.model.QRCodeCreator
import vn.app.qrcode.data.model.qrcodeobject.ContactQRCode
import vn.app.qrcode.data.model.qrcodeobject.EmailQRCode
import vn.app.qrcode.data.model.qrcodeobject.EventQRCode
import vn.app.qrcode.data.model.qrcodeobject.LocationQRCode
import vn.app.qrcode.data.model.qrcodeobject.MessageQRCode
import vn.app.qrcode.data.model.qrcodeobject.TextQRCode
import vn.app.qrcode.data.model.qrcodeobject.UrlQRCode
import vn.app.qrcode.data.model.qrcodeobject.WifiQRCode
import vn.app.qrcode.databinding.FragmentQrCodeDetailBinding
import vn.app.qrcode.ui.home.camera.mlkit.barcodedetection.BarcodeField
import vn.app.qrcode.ui.home.camera.mlkit.barcodedetection.BarcodeFieldAdapter
import vn.app.qrcode.utils.AppUtils
import vn.app.qrcode.utils.AppUtils.showNotification

class QrCodeDetailFragment :
    BaseMVVMFragment<CommonEvent, FragmentQrCodeDetailBinding, QrCodeDetailViewModel>() {

    private val args: QrCodeDetailFragmentArgs by navArgs()
    lateinit var qrCodeItem: QRCodeCreator

    override val layoutId: Int
        get() = R.layout.fragment_qr_code_detail
    override val viewModel: QrCodeDetailViewModel by viewModel()
    var listBarcodeField = mutableListOf<BarcodeField>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        qrCodeItem = args.qrCodeItem
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
        setupViewEvent()
    }

    private fun setupViewEvent() {
        viewDataBinding.apply {
            btnBack.setOnClickListener {
                findNavController().popBackStack()
            }

            favoriteBtn.setOnClickListener {
                it.isSelected = !it.isSelected
                qrCodeItem.isFavorite = it.isSelected
                viewModel.updateQRCode(qrCodeItem)
            }

            shareBtn.setDebounceClickListener {
                AppUtils.shareQrImage(
                    requireActivity(),
                    AppUtils.generateQRCode(qrCodeItem.rawValue)
                )
            }
            saveBtn.setDebounceClickListener {
                if (AppUtils.saveImage(
                        AppUtils.generateQRCode(qrCodeItem.rawValue),
                        requireActivity()
                    )
                ) {
                    Toast(activity).showNotification(
                        requireActivity(),
                        PopUpMessage.SUCCESS.ordinal,
                        getString(R.string.txt_success_save_image)
                    )
                } else {
                    Toast(activity).showNotification(
                        requireActivity(),
                        PopUpMessage.FAIL.ordinal,
                        getString(R.string.txt_fail_save_image)
                    )
                }
            }
        }
    }

    private fun setupUi() {
        viewDataBinding.apply {
            tvTitle.text = getTitle()
            imgQrcode.setImageBitmap(AppUtils.generateQRCode(qrCodeItem.rawValue))
            btnAction.isInvisible = true
            favoriteBtn.isSelected = qrCodeItem.isFavorite
            rvDetail.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(activity)
                adapter = BarcodeFieldAdapter(listBarcodeField)
            }

        }
    }

    private fun getTitle(): String {
        var title = ""
        when (qrCodeItem.type) {
            QrCodeType.EMAIL.ordinal -> {
                title = getString(R.string.result_creator_email)
                convertEmailJsonToList()
            }
            QrCodeType.MESSAGE.ordinal -> {
                title = getString(R.string.result_creator_message)
                convertMessageJsonToList()
            }
            QrCodeType.CONTACT.ordinal -> {
                title = getString(R.string.result_creator_contact)
                convertContactJsonToList()
            }
            QrCodeType.URL.ordinal -> {
                title = getString(R.string.result_creator_url)
                convertUrlJsonToList()
            }
            QrCodeType.WIFI.ordinal -> {
                title = getString(R.string.result_creator_wifi)
                convertWifiJsonToList()
            }
            QrCodeType.LOCATION.ordinal -> {
                title = getString(R.string.result_creator_location)
                convertLocationJsonToList()
            }
            QrCodeType.EVENT.ordinal -> {
                title = getString(R.string.result_creator_event)
                convertEventJsonToList()
            }
            QrCodeType.TEXT.ordinal -> {
                title = getString(R.string.result_creator_text)
                convertTextJsonToList()
            }
            QrCodeType.NAME_CARD.ordinal -> {
                title = getString(R.string.result_creator_name_card)
                convertContactJsonToList()
            }
            QrCodeType.FACEBOOK.ordinal -> {
                title = getString(R.string.result_creator_facebook)
                convertUrlJsonToList()
            }
            QrCodeType.TWITTER.ordinal -> {
                title = getString(R.string.result_creator_twitter)
                convertUrlJsonToList()
            }
            QrCodeType.INSTAGRAM.ordinal -> {
                title = getString(R.string.result_creator_instagram)
                convertUrlJsonToList()
            }
        }
        return title
    }

    private fun convertEmailJsonToList() {
        val emailModel = Gson().fromJson(qrCodeItem.content, EmailQRCode::class.java)
        listBarcodeField.add(
            BarcodeField(
                getString(R.string.hint_email_address),
                emailModel.getEmailDisplay()
            )
        )
        listBarcodeField.add(
            BarcodeField(
                getString(R.string.hint_email_subject), emailModel.subject
            )
        )
        listBarcodeField.add(
            BarcodeField(
                getString(R.string.label_email_message), emailModel.body
            )
        )
    }

    private fun convertMessageJsonToList() {
        val messageModel = Gson().fromJson(qrCodeItem.content, MessageQRCode::class.java)
        listBarcodeField.add(
            BarcodeField(
                getString(R.string.label_sms_to),
                messageModel.phones
            )
        )
        listBarcodeField.add(
            BarcodeField(
                getString(R.string.label_sms_message), messageModel.message
            )
        )
    }

    private fun convertTextJsonToList() {
        val model = Gson().fromJson(qrCodeItem.content, TextQRCode::class.java)
        listBarcodeField.add(
            BarcodeField(
                getString(R.string.text_display),
                model.content
            )
        )
    }

    private fun convertUrlJsonToList() {
        val model = Gson().fromJson(qrCodeItem.content, UrlQRCode::class.java)
        listBarcodeField.add(
            BarcodeField(
                getString(R.string.label_url_link),
                model.link
            )
        )
    }

    private fun convertWifiJsonToList() {
        val model = Gson().fromJson(qrCodeItem.content, WifiQRCode::class.java)
        listBarcodeField.add(
            BarcodeField(
                getString(R.string.hint_wifi_ssid),
                model.ssid
            )
        )
        listBarcodeField.add(
            BarcodeField(
                getString(R.string.hint_wifi_password),
                model.password
            )
        )
        listBarcodeField.add(
            BarcodeField(
                getString(R.string.label_wifi_encryption),
                model.type
            )
        )
    }

    private fun convertLocationJsonToList() {
        val model = Gson().fromJson(qrCodeItem.content, LocationQRCode::class.java)
        listBarcodeField.add(
            BarcodeField(
                getString(R.string.label_location_latitude),
                model.latitude
            )
        )
        listBarcodeField.add(
            BarcodeField(
                getString(R.string.label_location_longitude),
                model.longitude
            )
        )
        if (model.address.isNotEmpty()) {
            listBarcodeField.add(
                BarcodeField(
                    getString(R.string.label_location_address),
                    model.address
                )
            )
        }
    }

    private fun convertEventJsonToList() {
        val model = Gson().fromJson(qrCodeItem.content, EventQRCode::class.java)
        listBarcodeField.add(
            BarcodeField(
                getString(R.string.label_event_name),
                model.name
            )
        )
        listBarcodeField.add(
            BarcodeField(
                getString(R.string.label_event_start_datetime),
                AppUtils.getTimeFromDateTimeZone(model.startTime)
            )
        )
        listBarcodeField.add(
            BarcodeField(
                getString(R.string.label_event_end_datetime),
                AppUtils.getTimeFromDateTimeZone(model.endTime)
            )
        )
        if (model.location.isNotEmpty()) {
            listBarcodeField.add(
                BarcodeField(
                    getString(R.string.label_event_location),
                    model.location
                )
            )
        }

        if (model.description.isNotEmpty()) {
            listBarcodeField.add(
                BarcodeField(
                    getString(R.string.label_event_description),
                    model.description
                )
            )
        }

    }

    private fun convertContactJsonToList() {
        val model = Gson().fromJson(qrCodeItem.content, ContactQRCode::class.java)
        //Name
        if (!model.name.isNullOrEmpty())
            listBarcodeField.add(
                BarcodeField(
                    getString(R.string.label_contact_name), model.name ?: ""
                )
            )
        //Phone
        if (!model.phoneNumber.isNullOrEmpty()) {
            listBarcodeField.add(
                BarcodeField(
                    getString(R.string.label_contact_number), model.phoneNumber ?: ""
                )
            )
        }
        //Email
        if (!model.email.isNullOrEmpty()) {
            listBarcodeField.add(
                BarcodeField(
                    getString(R.string.label_contact_email), model.email ?: ""
                )
            )
        }
        // Location
        if (!model.address.isNullOrEmpty()) {
            listBarcodeField.add(
                BarcodeField(
                    getString(R.string.label_contact_location), model.address ?: ""
                )
            )
        }
        // url
        if (!model.url.isNullOrEmpty()) {
            listBarcodeField.add(
                BarcodeField(
                    getString(R.string.label_contact_facebook), model.url ?: ""
                )
            )
        }

        //Position
        if (!model.jobTitle.isNullOrEmpty()) {
            listBarcodeField.add(
                BarcodeField(
                    getString(R.string.label_contact_position),
                    model.jobTitle ?: ""
                )
            )
        }

        //Birthday
        if (!model.birthday.isNullOrEmpty()) {
            listBarcodeField.add(
                BarcodeField(
                    getString(R.string.label_contact_birthday),
                    AppUtils.getStringDateFromQRCode(model.birthday)
                )
            )
        }

        // Note
        if (!model.note.isNullOrEmpty()) {
            listBarcodeField.add(
                BarcodeField(
                    getString(R.string.label_contact_note),
                    model.note ?: ""
                )
            )
        }

    }

    override fun onStop() {
        super.onStop()
        listBarcodeField.clear()
    }

    companion object {
        fun newInstance() = QrCodeDetailFragment()
    }

}