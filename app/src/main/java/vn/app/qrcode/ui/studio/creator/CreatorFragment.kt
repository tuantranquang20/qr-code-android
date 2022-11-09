package vn.app.qrcode.ui.studio.creator

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.base.common.base.fragment.BaseMVVMFragment
import com.base.common.base.viewmodel.CommonEvent
import org.jetbrains.anko.support.v4.act
import org.koin.androidx.viewmodel.ext.android.viewModel
import vn.app.qrcode.R
import vn.app.qrcode.data.constant.QrCodeType
import vn.app.qrcode.data.model.QRCodeType
import vn.app.qrcode.databinding.FragmentCreatorBinding

class CreatorFragment : BaseMVVMFragment<CommonEvent, FragmentCreatorBinding, CreatorViewModel>() {

    companion object {
        fun newInstance() = CreatorFragment()
    }

    override val layoutId: Int
        get() = R.layout.fragment_creator
    override val viewModel: CreatorViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding.creatorFragment = this@CreatorFragment
        setRecyclerview()
    }

    private fun setRecyclerview() {
        val itemCreatorAdapter = ItemCreatorAdapter(ItemCreatorListener { type ->
            navigateToCreate(type)
        })

        viewDataBinding.rvCreator.adapter = itemCreatorAdapter
        itemCreatorAdapter.submitList(viewModel.listQrType)
    }

    fun onBack() {
        findNavController().popBackStack()
    }

    private fun navigateToCreate(type: QRCodeType) {
        when (type.type) {
            QrCodeType.URL.ordinal,
            QrCodeType.FACEBOOK.ordinal,
            QrCodeType.TWITTER.ordinal,
            QrCodeType.INSTAGRAM.ordinal,
            -> {
                findNavController().navigate(
                    CreatorFragmentDirections.actionCreatorFragmentToUrlFragment(type)
                )
            }
            QrCodeType.TEXT.ordinal -> {
                findNavController().navigate(
                    CreatorFragmentDirections.actionCreatorFragmentToTextFragment(type)
                )
            }
            QrCodeType.MESSAGE.ordinal -> {
                findNavController().navigate(
                    CreatorFragmentDirections.actionCreatorFragmentToMessageFragment(type)
                )
            }
            QrCodeType.EVENT.ordinal -> {
                findNavController().navigate(
                    CreatorFragmentDirections.actionCreatorFragmentToEventFragment(type)
                )
            }
            QrCodeType.EMAIL.ordinal -> {
                findNavController().navigate(
                    CreatorFragmentDirections.actionCreatorFragmentToCreatorEmailFragment(type)
                )
            }
            QrCodeType.CONTACT.ordinal -> {
                findNavController().navigate(
                    CreatorFragmentDirections.actionCreatorFragmentToCreatorContactFragment(type)
                )
            }
            QrCodeType.WIFI.ordinal -> {
                findNavController().navigate(
                    CreatorFragmentDirections.actionCreatorFragmentToCreatorWifiFragment(type)
                )
            }
            QrCodeType.NAME_CARD.ordinal -> {
                findNavController().navigate(
                    CreatorFragmentDirections.actionCreatorFragmentToCreatorNamecardFragment(type)
                )
            }
            else -> Toast.makeText(activity, "${type.nameId}", Toast.LENGTH_SHORT).show()
        }
    }

}
