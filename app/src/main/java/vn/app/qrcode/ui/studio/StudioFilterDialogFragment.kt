package vn.app.qrcode.ui.studio

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import vn.app.qrcode.R
import vn.app.qrcode.data.constant.QrCodeType
import vn.app.qrcode.data.constant.QrCodeTypeData
import vn.app.qrcode.data.model.QRCodeType
import vn.app.qrcode.databinding.FragmentStudioFilterBinding


class StudioFilterDialogFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentStudioFilterBinding? = null

    val binding get() = _binding!!
    private val viewModel: StudioViewModel by sharedViewModel()

    private val listSelectType: MutableList<Int> = mutableListOf()
    private val listAllQrType: MutableList<Int> = mutableListOf()
    lateinit var adapter: ItemFilterStudioAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudioFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.studioFilterDialogFragment = this@StudioFilterDialogFragment

        setListAllQrType()
        setListSelectedType()

        adapter = ItemFilterStudioAdapter(QrCodeTypeListener { type ->
            selectQrCodeType(type)
        }, listSelectType)
        binding.rvListQrCodeType.adapter = adapter

        viewModel.listQrCodeType.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun selectQrCodeType(type: QRCodeType) {
        if (type.type == QrCodeType.ALL.ordinal) {
            // select ALL option
            if (listSelectType.contains(type.type)) {
                // select list exist ALL option -> remove all options
                listSelectType.clear()
            } else {
                // select list not exist ALL option -> add all options
                listSelectType.clear()
                listSelectType.addAll(listAllQrType)
            }
        } else {
            // select other options
            if (listSelectType.size == QrCodeTypeData.listQrCodeType.size) {
                // all options selected -> remove it and ALL option
                listSelectType.remove(type.type)
                listSelectType.remove(QrCodeType.ALL.ordinal)
            } else {
                // a few options selected
                if (listSelectType.contains(type.type)) {
                    // this option selected -> remove it
                    listSelectType.remove(type.type)
                } else {
                    // this option not selected yet -> add it
                    listSelectType.add(type.type)
                    if (listSelectType.size == QrCodeTypeData.listQrCodeType.size - 1) {
                        // all options selected -> add ALL option
                        listSelectType.add(QrCodeType.ALL.ordinal)
                    }
                }
            }
        }

        adapter.notifyDataSetChanged()
    }

    private fun setListAllQrType() {
        QrCodeTypeData.listQrCodeType.forEach { qrCodeType ->
            listAllQrType.add(qrCodeType.type)
        }
    }

    private fun setListSelectedType() {
        if (viewModel.listFilterType.value == null || viewModel.listFilterType.value?.size!! <= 0) {
            listSelectType.addAll(listAllQrType)
        } else {
            viewModel.listFilterType.value?.let { listSelectType.addAll(it) }
        }
    }

    fun filterQrCode() {
        viewModel.updateListTypeToFilter(listSelectType)
        this.dismiss()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        listSelectType.clear()
        listAllQrType.clear()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        (dialog as BottomSheetDialog).behavior
            .addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                        //In the EXPANDED STATE apply a new MaterialShapeDrawable with rounded cornes
                        val newMaterialShapeDrawable: MaterialShapeDrawable =
                            createMaterialShapeDrawable(bottomSheet)
                        ViewCompat.setBackground(bottomSheet, newMaterialShapeDrawable)
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            })

        return dialog
    }

    private fun createMaterialShapeDrawable(bottomSheet: View): MaterialShapeDrawable {
        //Create a ShapeAppearanceModel with the same shapeAppearanceOverlay used in the style
        val shapeAppearanceModel =
            ShapeAppearanceModel.builder(context, 0, R.style.CustomShapeAppearanceBottomSheetDialog)
                .build()

        val states = arrayOf(
            intArrayOf(android.R.attr.state_enabled),
            intArrayOf(-android.R.attr.state_enabled),
            intArrayOf(-android.R.attr.state_checked),
            intArrayOf(android.R.attr.state_pressed)
        )

        val colors = intArrayOf(
            Color.WHITE,
            Color.WHITE,
            Color.WHITE,
            Color.WHITE
        )

        val colorStateList = ColorStateList(states, colors)
        val newMaterialShapeDrawable = MaterialShapeDrawable((shapeAppearanceModel))
        //Copy the attributes in the new MaterialShapeDrawable
        newMaterialShapeDrawable.initializeElevationOverlay(context)
        newMaterialShapeDrawable.fillColor = colorStateList
        newMaterialShapeDrawable.tintList = colorStateList
        return newMaterialShapeDrawable
    }
}
