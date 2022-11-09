package vn.app.qrcode.ui.home.camera

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.content.Intent
import android.hardware.Camera
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.base.common.base.fragment.BaseMVVMFragment
import com.base.common.base.viewmodel.CommonEvent
import com.base.common.utils.ext.setDebounceClickListener
import com.google.android.material.chip.Chip
import com.google.common.base.Objects
import com.google.mlkit.vision.barcode.common.Barcode
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Calendar
import net.glxn.qrgen.core.scheme.SchemeUtil
import net.glxn.qrgen.core.scheme.VCard
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber
import vn.app.qrcode.R
import vn.app.qrcode.data.constant.DATE_TIME_ZONE_FORMAT
import vn.app.qrcode.data.constant.FORMAT_DATE_DISPLAY
import vn.app.qrcode.data.constant.FORMAT_DATE_QRCODE
import vn.app.qrcode.data.constant.MAX_SLIDER_ZOOM
import vn.app.qrcode.data.constant.MIN_SLIDER_ZOOM
import vn.app.qrcode.data.constant.QrCodeType
import vn.app.qrcode.data.constant.REGEX_BCC_EMAIL
import vn.app.qrcode.data.constant.REGEX_CONTACT_BDAY
import vn.app.qrcode.data.constant.REGEX_CONTACT_NOTE
import vn.app.qrcode.data.constant.REGEX_CONTACT_TYPE
import vn.app.qrcode.data.constant.REGEX_FACEBOOK_URL
import vn.app.qrcode.data.constant.REGEX_GEO_LOCATION
import vn.app.qrcode.data.constant.REGEX_INSTAGRAM_URL
import vn.app.qrcode.data.constant.REGEX_QUERY_LOCATION
import vn.app.qrcode.data.constant.REGEX_TWITTER_URL
import vn.app.qrcode.data.constant.REQUEST_CODE_COMMON
import vn.app.qrcode.data.constant.REQUEST_CODE_CONNECT_WIFI
import vn.app.qrcode.data.constant.REQUEST_CODE_COPY_TEXT
import vn.app.qrcode.data.constant.STEP_BUTTON_ZOOM
import vn.app.qrcode.data.constant.VCARD_PREFIX_TITLE
import vn.app.qrcode.data.constant.WorkflowState
import vn.app.qrcode.data.model.qrcodeobject.ContactQRCode
import vn.app.qrcode.data.model.qrcodeobject.EmailQRCode
import vn.app.qrcode.data.model.qrcodeobject.EventQRCode
import vn.app.qrcode.data.model.qrcodeobject.LocationQRCode
import vn.app.qrcode.data.model.qrcodeobject.MessageQRCode
import vn.app.qrcode.data.model.qrcodeobject.NameCardQRCode
import vn.app.qrcode.data.model.qrcodeobject.TextQRCode
import vn.app.qrcode.data.model.qrcodeobject.UrlQRCode
import vn.app.qrcode.data.model.qrcodeobject.WifiQRCode
import vn.app.qrcode.databinding.FragmentCameraBinding
import vn.app.qrcode.ui.home.camera.mlkit.Utils
import vn.app.qrcode.ui.home.camera.mlkit.barcodedetection.BarcodeField
import vn.app.qrcode.ui.home.camera.mlkit.barcodedetection.BarcodeProcessor
import vn.app.qrcode.ui.home.camera.mlkit.camera.CameraSource
import vn.app.qrcode.ui.home.camera.mlkit.camera.CameraSourcePreview
import vn.app.qrcode.ui.home.camera.mlkit.camera.GraphicOverlay
import vn.app.qrcode.utils.AppUtils
import vn.app.qrcode.utils.AppUtils.createItemFromScanner
import vn.app.qrcode.utils.AppUtils.vibratePhone
import vn.app.qrcode.utils.sharepref.SharedPreUtils


class CameraFragment :
    BaseMVVMFragment<CommonEvent, FragmentCameraBinding, CameraViewModel>(), View.OnClickListener {

    private var cameraSource: CameraSource? = null
    private var preview: CameraSourcePreview? = null
    private var graphicOverlay: GraphicOverlay? = null
    private var flashButton: View? = null
    private var bellButton: View? = null
    private var staticButton: View? = null
    private var switchButton: View? = null
    private var promptChip: Chip? = null
    private var promptChipAnimator: AnimatorSet? = null
    private var currentWorkflowState: WorkflowState? = null

    val barcodeFieldList = ArrayList<BarcodeField>()
    var nameButtonAction: Int? = null
    var intent: Intent? = null
    var requestCode: Int = REQUEST_CODE_COMMON

    override val layoutId: Int
        get() = R.layout.fragment_camera
    override val viewModel: CameraViewModel by sharedViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setWorkflowState(WorkflowState.DETECTING)
        setupViewEvent()
    }

    @SuppressLint("ResourceType")
    private fun setupViewEvent() {
        viewDataBinding.apply {
            preview = requireActivity().findViewById(R.id.camera_preview)
            graphicOverlay =
                requireActivity().findViewById<GraphicOverlay>(R.id.camera_preview_graphic_overlay)
                    .apply {
                        setOnClickListener(this@CameraFragment)
                        cameraSource = CameraSource(this)
                    }
            promptChip = requireActivity().findViewById(R.id.bottom_prompt_chip)
            promptChipAnimator =
                (AnimatorInflater.loadAnimator(requireContext(), R.anim.bottom_prompt_chip_enter) as AnimatorSet).apply {
                    setTarget(promptChip)
                }

            flashButton = requireActivity().findViewById<View>(R.id.flash_button).apply {
                setDebounceClickListener(this@CameraFragment)
            }
            bellButton = requireActivity().findViewById<View>(R.id.notification_bell_button).apply {
                setDebounceClickListener(this@CameraFragment)
                this.isSelected = SharedPreUtils.isVibrate(getString(R.string.pref_key_setting_vibrate))
            }
            staticButton = requireActivity().findViewById<View>(R.id.static_button).apply {
                setDebounceClickListener(this@CameraFragment)
            }
            switchButton = requireActivity().findViewById<View>(R.id.switch_camera_button).apply {
                setDebounceClickListener(this@CameraFragment)
            }
            sliderZoom.valueFrom = MIN_SLIDER_ZOOM
            sliderZoom.valueTo = MAX_SLIDER_ZOOM
            sliderZoom.addOnChangeListener { _, value, _ ->
                // Responds to when slider's value is changed
                cameraSource?.changeZoom(value / MAX_SLIDER_ZOOM)
            }

            zoomInBtn.setOnClickListener {
                val tempValue = sliderZoom.value + STEP_BUTTON_ZOOM
                sliderZoom.value = if (tempValue > MAX_SLIDER_ZOOM) MAX_SLIDER_ZOOM else tempValue
            }

            zoomOutBtn.setOnClickListener {
                val tempValue = sliderZoom.value - STEP_BUTTON_ZOOM
                sliderZoom.value = if (tempValue < MIN_SLIDER_ZOOM) MIN_SLIDER_ZOOM else tempValue
            }
        }
        setUpWorkflowModel()

    }

    override fun onResume() {
        super.onResume()
        viewModel.isCameraFragmentActive = true
        if (!Utils.allPermissionsGranted(requireActivity())) {
            Utils.requestRuntimePermissions(requireActivity())
        }

        viewModel.markCameraFrozen()
        currentWorkflowState = WorkflowState.NOT_STARTED
        cameraSource?.setFrameProcessor(BarcodeProcessor(graphicOverlay!!, viewModel))
        viewModel.setWorkflowState(WorkflowState.DETECTING)
    }

    override fun onStop() {
        super.onStop()
        viewModel.isCameraFragmentActive = false
        currentWorkflowState = WorkflowState.NOT_STARTED
        stopCameraPreview()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraSource?.release()
        cameraSource = null
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.flash_button -> {
                try {
                    flashButton?.let {
                        if (it.isSelected) {
                            it.isSelected = false
                            cameraSource?.updateFlashMode(Camera.Parameters.FLASH_MODE_OFF)
                        } else {
                            it.isSelected = true
                            cameraSource!!.updateFlashMode(Camera.Parameters.FLASH_MODE_TORCH)
                        }
                    }
                } catch (e: Exception) {
                    Timber.d(e)
                }

            }
            R.id.notification_bell_button -> {
                try {
                    bellButton?.let {
                        it.isSelected = !it.isSelected
                        SharedPreUtils.setVibrate(getString(R.string.pref_key_setting_vibrate), it.isSelected)
                    }
                } catch (e: Exception) {
                    Timber.d(e)
                }
            }
            R.id.static_button -> {
                try{
                    stopCameraPreview()
                    staticButton?.let {
                        Utils.openImagePicker(requireActivity())
                    }
                } catch (e: Exception) {
                    Timber.d(e)
                }
            }
            R.id.switch_camera_button -> {
                try {
                    cameraSource?.switchCamera()
                    stopCameraPreview()
                    startCameraPreview()
                } catch (e: Exception) {
                    Timber.d(e)
                }
            }
        }
    }

    private fun startCameraPreview() {
        val cameraSource = this.cameraSource ?: return
        if (!viewModel.isCameraLive) {
            try {
                viewModel.markCameraLive()
                preview?.start(cameraSource)
                viewDataBinding.sliderZoom.value = 0.0F
            } catch (e: IOException) {
                Log.e(TAG, "Failed to start camera preview!", e)
                cameraSource.release()
                this.cameraSource = null
            }
        }
    }

    private fun stopCameraPreview() {
        if (viewModel.isCameraLive) {
            viewModel.markCameraFrozen()
            flashButton?.isSelected = false
            preview?.stop()
        }
    }

    private fun setUpWorkflowModel() {
        /*
        * Observes the workflow state changes, if happens, update the overlay view indicators and
        * camera preview state.
        */
        viewModel.workflowState.observe(viewLifecycleOwner, Observer { workflowState ->
            if (workflowState == null || Objects.equal(currentWorkflowState, workflowState)) {
                return@Observer
            }

            currentWorkflowState = workflowState
            val wasPromptChipGone = promptChip?.visibility == View.GONE

            when (workflowState) {
                WorkflowState.DETECTING -> {
                    promptChip?.visibility = View.VISIBLE
                    promptChip?.setText(R.string.prompt_point_at_a_barcode)
                    startCameraPreview()
                }
                WorkflowState.DETECTED -> {
                    promptChip?.visibility = View.GONE
                    stopCameraPreview()
                }
                else -> promptChip?.visibility = View.GONE
            }

            val shouldPlayPromptChipEnteringAnimation =
                wasPromptChipGone && promptChip?.visibility == View.VISIBLE
            promptChipAnimator?.let {
                if (shouldPlayPromptChipEnteringAnimation && !it.isRunning) it.start()
            }
        })

        viewModel.detectedBarcode.observe(viewLifecycleOwner) { barcode ->
            try {
                if (barcode != null && viewModel.workflowState.value == WorkflowState.DETECTED) {
                    barcodeFieldList.clear()
                    when (barcode.valueType) {
                        // QR Code type Email
                        Barcode.TYPE_EMAIL -> {
                            handleTypeEmail(barcode.email!!, barcode.rawValue!!)
                        }

                        // QR Code type Message
                        Barcode.TYPE_SMS -> {
                            handleTypeSms(barcode.sms!!, barcode.rawValue!!)
                        }

                        // QR Code type Contact
                        Barcode.TYPE_CONTACT_INFO -> {
                            handleTypeContact(
                                barcode.contactInfo!!,
                                barcode.rawValue!!
                            )
                        }

                        // QR Code type URL, Facebook, Twitter, Instagram
                        Barcode.TYPE_URL -> {
                            handleTypeUrl(barcode.url!!, barcode.rawValue!!)
                        }

                        // QR Code type Wifi
                        Barcode.TYPE_WIFI -> {
                            handleTypeWifi(barcode.wifi!!, barcode.rawValue!!)
                        }

                        // QR Code type Location
                        Barcode.TYPE_GEO -> {
                            handleTypeGeo(barcode.geoPoint!!, barcode.rawValue!!)
                        }

                        // QR Code type Event
                        Barcode.TYPE_CALENDAR_EVENT -> {
                            handleTypeCalendar(barcode.calendarEvent!!, barcode.rawValue!!)
                        }

                        // QR Code type Text
                        else -> {
                            handleTypeText(barcode.rawValue!!)
                        }
                    }

                    viewModel.textActionBtn = nameButtonAction
                    viewModel.intentOtherApp = intent
                    viewModel.barcodeFieldList = barcodeFieldList
                    viewModel.requestCode = requestCode
                    if (SharedPreUtils.isVibrate(getString(R.string.pref_key_setting_vibrate))) {
                        vibratePhone()
                    }
                    findNavController().navigate(R.id.action_camera_fragment_to_result_scan_fragment)
                }
            } catch (e: Exception) {
                Timber.d(e)
                Toast.makeText(activity, R.string.can_not_scan, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun handleTypeEmail(dataObject: Barcode.Email, rawValue: String) {
        nameButtonAction = R.string.open_email
        val email = dataObject.address ?: ""
        val bcc = REGEX_BCC_EMAIL.findAll(rawValue).map { it.groupValues[3] }.joinToString()
        val subject = dataObject.subject ?: ""
        val body = dataObject.body ?: ""

        val emailQrCode = EmailQRCode(email.trim(), bcc.trim(), subject.trim(), body.trim())

        barcodeFieldList.add(
            BarcodeField(
                getString(R.string.hint_email_address),
                emailQrCode.getEmailDisplay()
            )
        )
        barcodeFieldList.add(
            BarcodeField(
                getString(R.string.hint_email_subject), emailQrCode.subject
            )
        )
        barcodeFieldList.add(
            BarcodeField(
                getString(R.string.label_email_message), emailQrCode.body
            )
        )

        //Open Email app
        intent = Intent(Intent.ACTION_SEND)

        intent?.type = "plain/text"
        intent?.putExtra(Intent.EXTRA_EMAIL, arrayOf(emailQrCode.email))
        intent?.putExtra(Intent.EXTRA_CC, arrayOf(emailQrCode.bcc))
        intent?.putExtra(Intent.EXTRA_SUBJECT, emailQrCode.subject)
        intent?.putExtra(Intent.EXTRA_TEXT, emailQrCode.body)

        val qrCodeCreator = createItemFromScanner(
            rawValue = emailQrCode.getRawValue(),
            displayName = emailQrCode.getEmailDisplay(),
            typeQR = QrCodeType.EMAIL.ordinal,
            content = emailQrCode.toJson()
        )
        viewModel.setQRCodeCreator(qrCodeCreator)
    }

    private fun handleTypeSms(dataObject: Barcode.Sms, rawValue: String) {
        nameButtonAction = R.string.open_message
        val phone = dataObject.phoneNumber ?: ""
        val message = dataObject.message ?: ""
        val messageQrCode = MessageQRCode(
            phones = phone.trim(),
            message = message.trim()
        )

        barcodeFieldList.add(
            BarcodeField(
                getString(R.string.label_sms_to),
                messageQrCode.phones
            )
        )
        barcodeFieldList.add(
            BarcodeField(
                getString(R.string.label_sms_message), messageQrCode.message
            )
        )

        val qrCodeCreator = createItemFromScanner(
            rawValue = messageQrCode.getRawValue(),
            displayName = messageQrCode.phones,
            typeQR = QrCodeType.MESSAGE.ordinal,
            content = messageQrCode.toJson()
        )
        viewModel.setQRCodeCreator(qrCodeCreator)
    }

    private fun handleTypeContact(dataObject: Barcode.ContactInfo, rawValue: String) {
        val vCard = VCard()
        vCard.parseSchema(rawValue)
        nameButtonAction = R.string.open_contact
        var name = dataObject.name?.formattedName ?: ""
        var phone = ""
        var email = ""
        var location = ""
        var url = ""
        var position = ""
        var birthday = ""
        var note = ""

        //Name
        if (name.isNotEmpty()){
            name = name.trim()
            addBarcodeToList( getString(R.string.label_contact_name), name)
        }

        //Phone
        if (dataObject.phones.size > 0) {
            phone = dataObject.phones[0].number ?: ""
            addBarcodeToList(getString(R.string.label_contact_number), phone)
        }
        //Email
        if (dataObject.emails.size > 0) {
            email = dataObject.emails[0].address ?: ""
            addBarcodeToList(getString(R.string.label_contact_email), email)
        }
        // Location
        if (dataObject.addresses.size > 0) {
            location = dataObject.addresses[0].addressLines[0] ?: ""
            addBarcodeToList(getString(R.string.label_contact_location), location)
        }
        // url
        if (dataObject.urls.size > 0) {
            url = dataObject.urls[0] ?: ""
            addBarcodeToList(getString(R.string.label_contact_facebook), url)
        }

        //Position
        position =
            AppUtils.findAttributeFromQRCode(rawValue, VCARD_PREFIX_TITLE, SchemeUtil.LINE_FEED)
        if (position != "") {
            addBarcodeToList(getString(R.string.label_contact_position), position)
        }

        //Birthday
        birthday = REGEX_CONTACT_BDAY.find(rawValue)?.groupValues?.get(2) ?: ""
        if (birthday.isNotEmpty()) {
            addBarcodeToList(
                getString(R.string.label_contact_birthday),
                AppUtils.convertDateString(birthday, FORMAT_DATE_QRCODE, FORMAT_DATE_DISPLAY)
            )
        }

        //Note
        note = REGEX_CONTACT_NOTE.find(rawValue)?.groupValues?.get(2) ?: ""
        if (note.isNotEmpty()) {
            addBarcodeToList(getString(R.string.label_contact_note), note)
        }

        var typeQr = QrCodeType.CONTACT.ordinal
        try {
            typeQr = REGEX_CONTACT_TYPE.find(rawValue)?.groupValues?.get(2)?.toInt()!!
        } catch (e: Exception) {
            Timber.d(e)
        }

        if (typeQr == QrCodeType.NAME_CARD.ordinal) {
            val nameCardQRCode = NameCardQRCode(
                name = name,
                phoneNumber = phone,
                email = email,
                address = location,
                jobTitle = position,
                url= url,
                note = note
            )
            val qrCodeCreator = createItemFromScanner(
                rawValue = nameCardQRCode.getRawValue(),
                displayName = name,
                typeQR = typeQr,
                content = nameCardQRCode.toJson()
            )
            viewModel.setQRCodeCreator(qrCodeCreator)
        } else {
            val contactQRCode = ContactQRCode(
                name = name,
                phoneNumber = phone,
                email = email,
                address = location,
                jobTitle = position,
                url= url,
                birthday = birthday,
                note = note
            )
            val qrCodeCreator = createItemFromScanner(
                rawValue = contactQRCode.getRawValue(),
                displayName = name,
                typeQR = typeQr,
                content = contactQRCode.toJson()
            )
            viewModel.setQRCodeCreator(qrCodeCreator)
        }

        intent = CameraHelper.setIntentToContact(
            name = name,
            phoneNumber = phone,
            email = email,
            address = location,
            jobTitle = position,
            url= url,
            birthday = birthday,
            note = note
        )
    }

    private fun handleTypeUrl(dataObject: Barcode.UrlBookmark, rawValue: String) {
        val typeQr: QrCodeType
        val username: String
        val url = dataObject.url?.trim()
        when {
            url!!.matches(REGEX_FACEBOOK_URL) -> {
                nameButtonAction = R.string.open_facebook
                username = getUserNameFromUrl(url, REGEX_FACEBOOK_URL)
                typeQr = QrCodeType.FACEBOOK
            }
            url.matches(REGEX_TWITTER_URL) -> {
                nameButtonAction = R.string.open_twitter
                username = getUserNameFromUrl(url, REGEX_TWITTER_URL)
                typeQr = QrCodeType.TWITTER
            }
            url.matches(REGEX_INSTAGRAM_URL) -> {
                nameButtonAction = R.string.open_instagram
                username = getUserNameFromUrl(url, REGEX_INSTAGRAM_URL)
                typeQr = QrCodeType.INSTAGRAM
            }
            else -> {
                nameButtonAction = R.string.open_browser
                username = url
                typeQr = QrCodeType.URL
            }
        }
        barcodeFieldList.add(
            BarcodeField(
                getString(R.string.label_url_link),
                url
            )
        )

        intent = CameraHelper.setIntentToBrowser(dataObject.url!!)
        val urlQrCode = UrlQRCode(url)

        val qrCodeCreator =
            createItemFromScanner(
                rawValue = urlQrCode.getRawValue(),
                displayName = username,
                typeQR = typeQr.ordinal,
                content = urlQrCode.toJson()
            )
        viewModel.setQRCodeCreator(qrCodeCreator)
    }

    private fun getUserNameFromUrl(url: String, regex: Regex): String {
        val matches = regex.findAll(url)
        var username: String = matches.map { it.groupValues[1] }.joinToString()
        if (username.isEmpty()) {
            username = url
        }
        return username
    }

    @SuppressLint("NewApi")
    private fun handleTypeWifi(dataObject: Barcode.WiFi, rawValue: String) {
        nameButtonAction = R.string.connect_wifi
        val ssid = dataObject.ssid ?: ""
        val password = dataObject.password ?: ""
        val encryption = AppUtils.getNameEncryption(dataObject.encryptionType)
        val wifiQRCode  =  WifiQRCode(ssid.trim(), password.trim(), encryption.trim())

        if (ssid != "") {
            barcodeFieldList.add(
                BarcodeField(
                    getString(R.string.hint_wifi_ssid), wifiQRCode.ssid
                )
            )
        }
        if (password != "") {
            barcodeFieldList.add(
                BarcodeField(
                    getString(R.string.hint_wifi_password), wifiQRCode.password

                )
            )
        }
        if (encryption != "") {
            barcodeFieldList.add(
                BarcodeField(
                    getString(R.string.label_wifi_encryption), wifiQRCode.type
                )
            )
        }

        requestCode = REQUEST_CODE_CONNECT_WIFI

        val qrCodeCreator = createItemFromScanner(
            rawValue = wifiQRCode.getRawValue(),
            displayName = wifiQRCode.ssid,
            typeQR = QrCodeType.WIFI.ordinal,
            content = wifiQRCode.toJson()
        )
        viewModel.setQRCodeCreator(qrCodeCreator)
    }

    private fun handleTypeGeo(dataObject: Barcode.GeoPoint, rawValue: String) {
        val lat = dataObject.lat.toString() ?: ""
        val lng = dataObject.lng.toString() ?: ""
        val locationQrCode = LocationQRCode(lat.trim(), lng.trim())
        handleLocationQrCode(locationQrCode)
    }

    private fun handleTypeCalendar(dataObject: Barcode.CalendarEvent, rawValue: String) {
        val summary = dataObject.summary ?: ""
        val description = dataObject.description ?: ""
        val location = dataObject.location ?: ""
        val start = dataObject.start!!
        val end = dataObject.end!!
        val beginTime: Calendar = Calendar.getInstance().apply {
            set(start.year,start.month - 1, start.day, start.hours, start.minutes, start.seconds)
        }
        val endTime = Calendar.getInstance().apply {
            set(end.year, end.month - 1, end.day, end.hours, end.minutes, end.seconds)
        }

        val sdf = SimpleDateFormat(DATE_TIME_ZONE_FORMAT)
        val startTimeString = sdf.format(Date(beginTime.timeInMillis))
        val endTimeString = sdf.format(Date(endTime.timeInMillis))
        val eventQrCode = EventQRCode(summary.trim(), startTimeString, endTimeString, location.trim(), description.trim())

        nameButtonAction = R.string.open_calendar
        barcodeFieldList.add(
            BarcodeField(
                getString(R.string.label_event_name),
                eventQrCode.name
            )
        )
        barcodeFieldList.add(
            BarcodeField(
                getString(R.string.label_event_start_datetime),
                AppUtils.getDateTimeFromTimeStamp(beginTime.timeInMillis) ?: ""
            )
        )
        barcodeFieldList.add(
            BarcodeField(
                getString(R.string.label_event_end_datetime),
                AppUtils.getDateTimeFromTimeStamp(endTime.timeInMillis) ?: ""
            )
        )
        barcodeFieldList.add(
            BarcodeField(
                getString(R.string.label_event_location),
                eventQrCode.location
            )
        )
        barcodeFieldList.add(
            BarcodeField(
                getString(R.string.label_event_description),
                eventQrCode.description
            )
        )

        // Open Calendar app

        intent = Intent(
            Intent.ACTION_INSERT,
            CalendarContract.Events.CONTENT_URI
        ).apply {
            putExtra(
                CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                beginTime.timeInMillis
            )
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.timeInMillis)
            putExtra(CalendarContract.Events.TITLE, dataObject.summary)
            putExtra(CalendarContract.Events.EVENT_LOCATION, dataObject.location)
        }

        val qrCodeCreator = createItemFromScanner(
            rawValue = eventQrCode.getRawValue(),
            displayName = eventQrCode.name,
            typeQR = QrCodeType.EVENT.ordinal,
            content = eventQrCode.toJson()
        )
        viewModel.setQRCodeCreator(qrCodeCreator)
    }

    private fun handleTypeText(dataObject: String) {
        val content = dataObject.trim()
        if (REGEX_GEO_LOCATION.containsMatchIn(content)) {
            handleTypeGeoWithQuery(dataObject)
        } else {
            handleTextQrCode(dataObject)
        }
    }

    private fun handleTypeGeoWithQuery(dataObject: String) {
        val location = dataObject.trim()
        val matches = REGEX_GEO_LOCATION.findAll(location)
        val latitude = matches.map { it.groupValues[2] }.joinToString()
        val longitude = matches.map { it.groupValues[3] }.joinToString()

        var address = ""
        if (REGEX_QUERY_LOCATION.containsMatchIn(location)) {
            address = REGEX_QUERY_LOCATION.findAll(location).map { it.groupValues[2] }.joinToString()
        }

        val locationQrCode = LocationQRCode(latitude, longitude, address)
        handleLocationQrCode(locationQrCode)
    }

    private fun handleTextQrCode(dataObject: String) {
        val content = dataObject.trim()
        nameButtonAction = R.string.copy_text
        requestCode = REQUEST_CODE_COPY_TEXT

        val textQrCode = TextQRCode(content)
        barcodeFieldList.add(
            BarcodeField(
                getString(R.string.text_display),
                textQrCode.content
            )
        )
        val qrCodeCreator = createItemFromScanner(
            rawValue = textQrCode.getRawValue(),
            displayName = getString(R.string.text_display),
            typeQR = QrCodeType.TEXT.ordinal,
            content = textQrCode.toJson()
        )
        viewModel.setQRCodeCreator(qrCodeCreator)
    }

    private fun handleLocationQrCode (locationQrCode: LocationQRCode) {
        nameButtonAction = R.string.open_map
        barcodeFieldList.add(
            BarcodeField(
                getString(R.string.label_location_latitude),
                locationQrCode.latitude
            )
        )
        barcodeFieldList.add(
            BarcodeField(
                getString(R.string.label_location_longitude),
                locationQrCode.longitude
            )
        )
        var displayName = getString(R.string.label_location)
        if (locationQrCode.address.isNotEmpty()) {
            barcodeFieldList.add(
                BarcodeField(
                    getString(R.string.label_location_address),
                    locationQrCode.address
                )
            )
            displayName = locationQrCode.address
        }
        val qrCodeCreator =
            createItemFromScanner(
                rawValue = locationQrCode.getRawValue(),
                displayName = displayName,
                typeQR = QrCodeType.LOCATION.ordinal,
            content = locationQrCode.toJson()
            )
        viewModel.setQRCodeCreator(qrCodeCreator)
        // Creates an Intent that will load a map of San Francisco
        val gmmIntentUri = Uri.parse(locationQrCode.getLocationUri())
        intent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        intent!!.setPackage("com.google.android.apps.maps")
    }

    companion object {
        private const val TAG = "LiveBarcodeActivity"
    }

    private fun addBarcodeToList(label: String, content: String) {
        barcodeFieldList.add(BarcodeField(label, content))
    }
}
