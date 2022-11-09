package vn.app.qrcode.ui.studio.resultcreator

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.base.common.base.fragment.BaseMVVMFragment
import com.base.common.base.viewmodel.CommonEvent
import com.base.common.utils.ext.setDebounceClickListener
import java.util.Date
import kotlinx.android.synthetic.main.layout_header_app_bar.btnAction
import kotlinx.android.synthetic.main.layout_header_app_bar.btnBack
import kotlinx.android.synthetic.main.layout_header_app_bar.tvTitle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import vn.app.qrcode.R
import vn.app.qrcode.data.constant.CreatedBy
import vn.app.qrcode.data.constant.PopUpMessage
import vn.app.qrcode.data.model.QRCodeCreator
import vn.app.qrcode.data.model.ResultCreator
import vn.app.qrcode.databinding.FragmentCreatorResaultcreatorBinding
import vn.app.qrcode.ui.home.camera.mlkit.barcodedetection.BarcodeFieldAdapter
import vn.app.qrcode.utils.AppUtils
import vn.app.qrcode.utils.AppUtils.saveImage
import vn.app.qrcode.utils.AppUtils.shareQrImage
import vn.app.qrcode.utils.AppUtils.showNotification


class ResultCreatorFragment :
    BaseMVVMFragment<CommonEvent, FragmentCreatorResaultcreatorBinding, ResultCreatorViewModel>() {
    companion object {
        fun newInstance() = ResultCreatorFragment()
    }

    lateinit var resultCreator: ResultCreator
    private val args: ResultCreatorFragmentArgs by navArgs()
    override val layoutId: Int
        get() = R.layout.fragment_creator_resaultcreator
    override val viewModel: ResultCreatorViewModel by viewModel()

    lateinit var qrCodeCreator: QRCodeCreator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resultCreator = args.resultCreator
        viewModel.qrCodeCreator = AppUtils.createItemFromCreator(
            resultCreator.rawValue,
            resultCreator.nameItemResultCreator,
            resultCreator.typeItemResultCreator,
            resultCreator.content
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewEvent()
    }

    private fun setupViewEvent() {
        viewDataBinding.apply {
            tvTitle.text = resultCreator.title
            imgQrcodeCreator.setImageBitmap(resultCreator.image)
            btnAction.isInvisible = true

            barcodeFieldRecyclerView.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(activity)
                adapter = BarcodeFieldAdapter(resultCreator.barcodeFieldList)
            }

            btnBack.setOnClickListener {
                findNavController().popBackStack()
            }

            favoriteBtn.setOnClickListener {
                it.isSelected = !it.isSelected
                viewModel.qrCodeCreator?.isFavorite = it.isSelected
                viewModel.updateQRCode()
            }

            saveBtn.setDebounceClickListener {
                if (saveImage(resultCreator.image, requireActivity())) {
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

            shareBtn.setDebounceClickListener {
                shareQrImage(requireActivity(), resultCreator.image)
            }

            lifecycleScope.launch {
                val qrCode =
                    withContext(Dispatchers.IO) {
                        viewModel.checkQRCodeExist(resultCreator.rawValue)
                    }
                if (qrCode != null) {
                    viewModel.qrCodeCreator = qrCode
                    favoriteBtn.isSelected = qrCode.isFavorite
                    viewModel.qrCodeCreator?.createdAt = Date().time
                    viewModel.qrCodeCreator?.createdBy = CreatedBy.MANUAL_INPUT.ordinal
                    viewModel.updateQRCode()
                } else {
                    viewModel.createNewQRCode()
                }
            }
        }
    }
}
