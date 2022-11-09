package vn.app.qrcode.ui.studio.url

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.base.common.base.fragment.BaseMVVMFragment
import com.base.common.base.viewmodel.CommonEvent
import org.koin.androidx.viewmodel.ext.android.viewModel
import vn.app.qrcode.R
import vn.app.qrcode.data.constant.QrCodeType
import vn.app.qrcode.data.constant.REGEX_FACEBOOK_URL
import vn.app.qrcode.data.constant.REGEX_INSTAGRAM_URL
import vn.app.qrcode.data.constant.REGEX_TWITTER_URL
import vn.app.qrcode.data.constant.REGEX_URL
import vn.app.qrcode.data.constant.TEXT_EMPTY
import vn.app.qrcode.data.model.QRCodeType
import vn.app.qrcode.data.model.qrcodeobject.UrlQRCode
import vn.app.qrcode.databinding.FragmentCreatorUrlBinding
import vn.app.qrcode.ui.home.camera.mlkit.barcodedetection.BarcodeField
import vn.app.qrcode.utils.AppUtils
import vn.app.qrcode.utils.AppUtils.hideKeyboard

class UrlFragment : BaseMVVMFragment<CommonEvent, FragmentCreatorUrlBinding, UrlViewModel>() {

    lateinit var qrCodeTypeArg: QRCodeType
    private val args: UrlFragmentArgs by navArgs()

    companion object {
        fun newInstance() = UrlFragment()
    }

    override val layoutId: Int
        get() = R.layout.fragment_creator_url
    override val viewModel: UrlViewModel by viewModel()

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
            urlFragment = this@UrlFragment
            layoutFooter.layoutFooterConstraint.visibility =
                if (qrCodeTypeArg.type == QrCodeType.URL.ordinal) View.GONE
                else View.VISIBLE
        }
        setupViewEvent()
    }

    private fun setupViewEvent() {
        val urlDefault = when (qrCodeTypeArg.type) {
            QrCodeType.FACEBOOK.ordinal -> getString(R.string.txt_default_fb_url)
            QrCodeType.TWITTER.ordinal -> getString(R.string.txt_default_tw_url)
            QrCodeType.INSTAGRAM.ordinal -> getString(R.string.txt_default_ig_url)
            else -> getString(R.string.txt_default_url)
        }
        viewDataBinding.editTextUrl.setText(urlDefault)
        viewDataBinding.editTextUrl.requestFocus()
    }

    fun onBack() {
        findNavController().popBackStack()
    }

    fun createUrlQrCode() {
        val textUrl = viewDataBinding.editTextUrl.text.trim().toString()
        if (isUrlNotEmpty(textUrl)) {
            when (qrCodeTypeArg.type) {
                QrCodeType.URL.ordinal -> createUrl(textUrl)
                QrCodeType.FACEBOOK.ordinal -> createFacebookUrl(textUrl)
                QrCodeType.TWITTER.ordinal -> createTwitterUrl(textUrl)
                QrCodeType.INSTAGRAM.ordinal -> createInstagramUrl(textUrl)
            }
        }
    }

    private fun isUrlNotEmpty(url: String): Boolean {
        if (url.isEmpty()) {
            setTextError(getString(R.string.txt_error_required))
            return false
        }
        setTextError(TEXT_EMPTY)
        return true
    }

    private fun createUrl(url: String) {
        if (url.matches(REGEX_URL)) {
            navigateToSaveAndShare(url, url, getString(R.string.result_creator_url))
            setTextError(TEXT_EMPTY)
        } else {
            setTextError(getString(R.string.txt_error_invalid))
        }
    }

    private fun createFacebookUrl(url: String) {
        if (url.matches(REGEX_FACEBOOK_URL)) {
            val matches = REGEX_FACEBOOK_URL.findAll(url)
            var userId: String = matches.map { it.groupValues[1] }.joinToString()
            if (userId.isEmpty()) {
                userId = url
            }
            navigateToSaveAndShare(userId, url, getString(R.string.result_creator_facebook))
            setTextError(TEXT_EMPTY)
        } else {
            setTextError(getString(R.string.txt_error_invalid))
        }
    }

    private fun createTwitterUrl(url: String) {
        if (url.matches(REGEX_TWITTER_URL)) {
            val matches = REGEX_TWITTER_URL.findAll(url)
            var userId: String = matches.map { it.groupValues[1] }.joinToString()
            if (userId.isEmpty()) {
                userId = url
            }
            navigateToSaveAndShare(userId, url, getString(R.string.result_creator_twitter))
            setTextError(TEXT_EMPTY)
        } else {
            setTextError(getString(R.string.txt_error_invalid))
        }
    }

    private fun createInstagramUrl(url: String) {
        if (url.matches(REGEX_INSTAGRAM_URL)) {
            val matches = REGEX_INSTAGRAM_URL.findAll(url)
            var userId = matches.map { it.groupValues[1] }.joinToString()
            if (userId.isEmpty()) {
                userId = url
            }
            navigateToSaveAndShare(userId, url, getString(R.string.result_creator_instagram))
            setTextError(TEXT_EMPTY)
        } else {
            setTextError(
                getString(R.string.txt_error_invalid)
            )
        }
    }

    private fun setTextError(error: String) {
        viewDataBinding.tvError.text = error
    }

    private fun navigateToSaveAndShare(displayName: String, url: String, title: String) {
        val messageBarcode = ArrayList<BarcodeField>()
        messageBarcode.add(
            BarcodeField(
                getString(R.string.label_url_link),
                url
            )
        )

        val urlQrCode = UrlQRCode(url)
        val urlResultCreator = AppUtils.getNewResultCreator(
            barcodeFieldList = messageBarcode,
            type = qrCodeTypeArg,
            displayName = displayName,
            rawValue = urlQrCode.getRawValue(),
            title = title,
            content = urlQrCode.toJson()
        )
        findNavController().navigate(
            UrlFragmentDirections.actionUrlFragmentToResultCreatorFragment(urlResultCreator)
        )
    }

    override fun onStop() {
        super.onStop()
        hideKeyboard()
    }
}
