package vn.app.qrcode.ui.studio.contact

import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import vn.app.qrcode.R
import vn.app.qrcode.databinding.FragmentBottomAddNewFieldBinding

class ContactAddNewFieldDialogFragment: BottomSheetDialogFragment() {
    private var _binding: FragmentBottomAddNewFieldBinding? = null

    val binding get() = _binding!!
    private val viewModel: CreatorContactViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBottomAddNewFieldBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.contactAddFieldFragment = this@ContactAddNewFieldDialogFragment

        val adapter = ItemNewFieldAdapter(NewFieldListener{ field ->
            viewModel.selectFieldToAddScreen(field)
        }, viewModel.listNewEditTextCache)
        binding.rvListNewField.adapter = adapter

        viewModel.listNewEditText.observe(viewLifecycleOwner) {
            it?.let {
                adapter.submitList(it)
            }
        }
    }

    fun handleAddNewFieldToScreen() {
        viewModel.handleAddFieldToScreen()
        this.dismiss()
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
        // Create a ShapeAppearanceModel with the same shapeAppearanceOverlay used in the style
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
    }}
