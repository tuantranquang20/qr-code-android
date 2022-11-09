package vn.app.qrcode.ui.studio

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.base.common.base.fragment.BaseMVVMFragment
import com.base.common.base.viewmodel.CommonEvent
import com.base.common.utils.ext.setDebounceClickListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import vn.app.qrcode.R
import vn.app.qrcode.data.constant.QrCodeType
import vn.app.qrcode.data.constant.QrItemType
import vn.app.qrcode.data.constant.TAG_HISTORY_FILTER
import vn.app.qrcode.data.constant.TYPE_LIST_CODE
import vn.app.qrcode.data.model.QRCodeCreator
import vn.app.qrcode.databinding.FragmentStudioBinding

class StudioFragment : BaseMVVMFragment<CommonEvent, FragmentStudioBinding, StudioViewModel>() {

    companion object {
        fun newInstance() = StudioFragment()
    }

    override val layoutId: Int
        get() = R.layout.fragment_studio
    override val viewModel: StudioViewModel by sharedViewModel()
    lateinit var filterBottomSheetDialog: StudioFilterDialogFragment
    lateinit var yourCodeItemAdapter: QrCodeFilterStudioAdapter
    lateinit var favoriteItemAdapter: FavoriteItemAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding.viewModel = viewModel
        viewDataBinding.studioFragment = this@StudioFragment
        filterBottomSheetDialog = StudioFilterDialogFragment()
        setupViewEvent()
        setRecycleView()
        setIconAction()
    }

    private fun setupViewEvent() {
        viewDataBinding.apply {
            btnAction.setDebounceClickListener {
                showListFilter()
            }
            radioGroupStudio.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.radio_your_code -> {
                        yourCodeItemAdapter.setQrItemType(QrItemType.YOUR_CODE_ITEM)
                        viewModel?.switchListQrCode(TYPE_LIST_CODE.YOUR_CODE)
                        rvStudioYourCode.visibility = View.VISIBLE
                        rvStudioFavorite.visibility = View.GONE
                        viewDataBinding.rvStudioYourCode.smoothScrollToPosition(0)
                    }
                    R.id.radio_favorite -> {
                        yourCodeItemAdapter.setQrItemType(QrItemType.FAVORITE_ITEM)
                        viewModel?.switchListQrCode(TYPE_LIST_CODE.FAVORITE)
                        rvStudioYourCode.visibility = View.GONE
                        rvStudioFavorite.visibility = View.VISIBLE
                        viewDataBinding.rvStudioFavorite.smoothScrollToPosition(0)
                    }
                }
            }
        }
    }

    private fun setIconAction() {
        viewModel.listFilterType.observe(this.viewLifecycleOwner) {
            if (it.isNullOrEmpty() || it.contains(QrCodeType.ALL.ordinal)) {
                viewDataBinding.btnAction.setImageResource(R.drawable.ic_filter)
            } else {
                viewDataBinding.btnAction.setImageResource(R.drawable.ic_filter_dot)
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
            { item -> viewModel.favorite(item) },
            { item -> viewModel.shareImage(item, requireActivity()) },
            { items -> updateListSelectItem(items) }
        )

        setYourCodeItemAdapter(qrItemListener)
        setFavoriteItemAdapter(qrItemListener)

        viewModel.listQrCode.observe(this.viewLifecycleOwner) {
            if (viewModel.isYourCode()) {
                yourCodeItemAdapter.submitList(it)
            } else {
                favoriteItemAdapter.submitList(it)
            }
            viewDataBinding.layoutNoDataHistory.visibility =
            if (it.isEmpty()) View.VISIBLE else View.GONE
        }

        setSelectItemDataObserver()
        setSelectRadioButton()
    }

    private fun navigateToDetail(item: QRCodeCreator) {
        findNavController().navigate(StudioFragmentDirections.actionStudioFragmentToQrCodeDetailFragment(item))
    }

    private fun setFavoriteItemAdapter(qrItemListener: QrItemListener) {
        favoriteItemAdapter = FavoriteItemAdapter(qrItemListener)
        favoriteItemAdapter.registerAdapterDataObserver(object: RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                viewDataBinding.rvStudioFavorite.smoothScrollToPosition(0)
            }
        })
        viewDataBinding.rvStudioFavorite.adapter = favoriteItemAdapter
    }

    private fun setYourCodeItemAdapter(qrItemListener: QrItemListener) {
        yourCodeItemAdapter = QrCodeFilterStudioAdapter(qrItemListener)
        yourCodeItemAdapter.registerAdapterDataObserver(object: RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                viewDataBinding.rvStudioYourCode.smoothScrollToPosition(0)
            }
        })
        viewDataBinding.rvStudioYourCode.adapter = yourCodeItemAdapter
    }

    private fun setSelectRadioButton() {
        if (viewModel.isYourCode()) {
            yourCodeItemAdapter.setQrItemType(QrItemType.YOUR_CODE_ITEM)
            viewDataBinding.apply {
                radioGroupStudio.check(R.id.radio_your_code)
                rvStudioYourCode.visibility = View.VISIBLE
                rvStudioFavorite.visibility = View.GONE
            }
        } else {
            yourCodeItemAdapter.setQrItemType(QrItemType.FAVORITE_ITEM)
            viewDataBinding.apply {
                radioGroupStudio.check(R.id.radio_favorite)
                rvStudioYourCode.visibility = View.GONE
                rvStudioFavorite.visibility = View.VISIBLE
            }
        }
    }

    private fun setSelectItemDataObserver() {
        viewModel.listSelectQrItem.observe(this.viewLifecycleOwner) {
            if (viewModel.isYourCode()) {
                yourCodeItemAdapter.setListSelectedItem(it)
            } else {
                favoriteItemAdapter.setListSelectedItem(it)
            }

            if (it.isNullOrEmpty()) {
                viewDataBinding.btnAction.visibility = View.VISIBLE
                viewDataBinding.imgRecycle.visibility = View.GONE
            } else {
                viewDataBinding.btnAction.visibility = View.GONE
                viewDataBinding.imgRecycle.visibility = View.VISIBLE
            }
        }
    }

    private fun updateListSelectItem(items: MutableList<Long>) {
        viewModel.updateListQrItemToDelete(items)
    }

    fun deleteSelectedQrItems() {
        if (viewModel.isYourCode()) {
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
        } else {
            viewModel.updateFavoriteItems()
        }
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

    fun onBack() {
        findNavController().popBackStack()
    }

    fun toCreatorScreen() {
        findNavController().navigate(R.id.action_studio_fragment_to_creator_fragment)
    }

    override fun onStop() {
        viewModel.clearListQrItemToDelete()
        super.onStop()
    }
}
