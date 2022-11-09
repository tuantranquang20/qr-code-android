package vn.app.qrcode.ui.history

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.base.common.base.fragment.BaseMVVMFragment
import com.base.common.base.viewmodel.CommonEvent
import com.base.common.utils.ext.setDebounceClickListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import vn.app.qrcode.R
import vn.app.qrcode.data.constant.QrCodeType
import vn.app.qrcode.data.constant.QrItemType
import vn.app.qrcode.data.constant.TAG_HISTORY_FILTER
import vn.app.qrcode.data.model.QRCodeCreator
import vn.app.qrcode.databinding.FragmentHistoryBinding
import vn.app.qrcode.ui.studio.QrCodeFilterStudioAdapter
import vn.app.qrcode.ui.studio.QrItemListener
import vn.app.qrcode.utils.AppUtils

class HistoryFragment : BaseMVVMFragment<CommonEvent, FragmentHistoryBinding, HistoryViewModel>() {
    override val layoutId: Int
        get() = R.layout.fragment_history
    override val viewModel: HistoryViewModel by sharedViewModel()
    lateinit var filterBottomSheetDialog: HistoryFilterDialogFragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewDataBinding.historyFragment = this@HistoryFragment
        viewDataBinding.imgFilter.setDebounceClickListener { showListFilter() }

        filterBottomSheetDialog = HistoryFilterDialogFragment()
        setRecycleView()
        setIconFilter()
    }

    private fun setIconFilter() {
        viewModel.listFilterType.observe(this.viewLifecycleOwner) {
            if (it.isNullOrEmpty() || it.contains(QrCodeType.ALL.ordinal)) {
                viewDataBinding.imgFilter.setImageResource(R.drawable.ic_filter)
            } else {
                viewDataBinding.imgFilter.setImageResource(R.drawable.ic_filter_dot)
            }
        }
    }

    private fun showListFilter() {
        activity?.supportFragmentManager?.let {
            filterBottomSheetDialog.show(it, TAG_HISTORY_FILTER)
        }
    }

    private fun setRecycleView() {
        val qrItemListener = QrItemListener(
            { item -> navigateToDetail(item) },
            { item -> deleteQrItem(item) },
            { item -> setFavoriteItem(item) },
            { item -> shareQrCodeImage(item) },
            { items -> updateListSelectItem(items) }
        )

        val adapter = QrCodeFilterStudioAdapter(qrItemListener)
        adapter.setQrItemType(QrItemType.HISTORY_ITEM)
        viewDataBinding.rvQrCodeHistoryItem.adapter = adapter

        viewModel.listQrCodeHistory.observe(this.viewLifecycleOwner) {
            adapter.submitList(it)
            viewDataBinding.layoutNoDataHistory.visibility =
                if (it.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.listSelectQrItem.observe(this.viewLifecycleOwner) {
            adapter.setListSelectedItem(it)

            if (it.isNullOrEmpty()) {
                viewDataBinding.imgFilter.visibility = View.VISIBLE
                viewDataBinding.imgRecycleHistory.visibility = View.GONE
            } else {
                viewDataBinding.imgFilter.visibility = View.GONE
                viewDataBinding.imgRecycleHistory.visibility = View.VISIBLE
            }
        }
    }

    private fun navigateToDetail(item: QRCodeCreator) {
        findNavController().navigate(HistoryFragmentDirections.actionHistoryFragmentToQrCodeDetailFragment(item))
    }

    private fun shareQrCodeImage(item: QRCodeCreator) {
        val imageQrCode = AppUtils.generateQRCode(item.rawValue)
        AppUtils.shareQrImage(requireActivity(), imageQrCode)
    }

    private fun setFavoriteItem(item: QRCodeCreator) {
        viewModel.favorite(item)
    }

    private fun updateListSelectItem(items: MutableList<Long>) {
        viewModel.updateListQrItemToDelete(items)
    }

    private fun deleteQrItem(item: QRCodeCreator) {
        MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_App_DeleteMaterialAlertDialog)
            .setTitle(getString(R.string.alert_delete_title))
            .setMessage(getString(R.string.alert_delete_item_message))
            .setNegativeButton(getString(R.string.alert_delete_btn_negative)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.alert_delete_btn_positive)) { dialog, _ ->
                viewModel.deleteQRCode(item)
                viewModel.removeSelectItem(item)
                dialog.dismiss()
            }
            .show()
    }

    fun deleteSelectedQrItems() {
        MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_App_DeleteMaterialAlertDialog)
            .setTitle(getString(R.string.alert_delete_title))
            .setMessage(getString(R.string.alert_delete_items_message))
            .setNegativeButton(getString(R.string.alert_delete_btn_negative)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.alert_delete_btn_positive)) { dialog, _ ->
                viewModel.deleteItems()
                viewModel.clearListQrItemToDelete()
                dialog.dismiss()
            }
            .show()
    }

    override fun onStop() {
        viewModel.clearListQrItemToDelete()
        super.onStop()
    }
}
