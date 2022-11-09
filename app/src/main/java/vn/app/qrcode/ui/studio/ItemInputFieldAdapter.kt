package vn.app.qrcode.ui.studio

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Calendar
import timber.log.Timber
import vn.app.qrcode.R
import vn.app.qrcode.data.constant.FORMAT_DATE_DISPLAY
import vn.app.qrcode.data.constant.InputFieldType
import vn.app.qrcode.data.model.InputField
import vn.app.qrcode.databinding.ItemInputFieldBinding


class ItemInputFieldAdapter(
    private val activity: FragmentActivity,
    private val itemInputFieldListener: ItemInputFieldListener,
) : RecyclerView.Adapter<ItemInputFieldAdapter.ItemInputFieldViewHolder>() {
    private var listInputField: MutableList<InputField> = mutableListOf()

    class ItemInputFieldViewHolder(val binding: ItemInputFieldBinding, val act: FragmentActivity) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: InputField, itemListener: ItemInputFieldListener) {
            binding.apply {
                inputField = item
                editTextField.inputType = item.inputType
                itemInputFieldListener = itemListener
                setTextWatcher(item)
                executePendingBindings()
            }
        }

        private fun setTextWatcher(item: InputField) {
            val textWatcher: TextWatcher = object : TextWatcher {
                override fun afterTextChanged(s: Editable) {
                    // Todo: logic here after text changed
                }

                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    // Todo: logic here before text changed
                }

                override fun onTextChanged(
                    s: CharSequence,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    item.fieldValue = s.trim().toString()
                }
            }

            binding.apply {
                editTextField.setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) {
                        if (item.fieldType == InputFieldType.BIRTHDAY.ordinal) {
                            showDatePickerDialog(item)
                        } else {
                            editTextField.addTextChangedListener(textWatcher)
                        }
                    } else {
                        editTextField.removeTextChangedListener(textWatcher)
                    }
                }
            }
        }

        @SuppressLint("SimpleDateFormat")
        private fun showDatePickerDialog(item: InputField) {
            val constraintsBuilder =
                CalendarConstraints.Builder()
                    .setValidator(DateValidatorPointBackward.now())

            var dateSelection = Calendar.getInstance().timeInMillis
            try {
                if (item.fieldValue.isNotEmpty()) {
                    dateSelection = SimpleDateFormat(FORMAT_DATE_DISPLAY).parse(item.fieldValue).time
                }
            } catch (e: Exception) {
                Timber.d(e)
            }

            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(item.fieldName)
                .setSelection(dateSelection)
                .setCalendarConstraints(constraintsBuilder.build())
                .build()

            datePicker.addOnPositiveButtonClickListener {
                item.fieldValue = SimpleDateFormat(FORMAT_DATE_DISPLAY).format(it)
                binding.editTextField.setText(
                    item.fieldValue,
                    TextView.BufferType.EDITABLE
                )
            }

            datePicker.addOnNegativeButtonClickListener {
                binding.editTextField.setText(
                    item.fieldValue,
                    TextView.BufferType.EDITABLE
                )
            }

            datePicker.addOnDismissListener {
                binding.editTextField.setText(
                    item.fieldValue,
                    TextView.BufferType.EDITABLE
                )
            }
            datePicker.show(act.supportFragmentManager, act.resources.getString(R.string.tag_birthday_date_picker_dialog))
            binding.editTextField.clearFocus()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemInputFieldViewHolder {
        return ItemInputFieldViewHolder(
            ItemInputFieldBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            activity
        )
    }

    override fun onBindViewHolder(holder: ItemInputFieldViewHolder, position: Int) {
        holder.bind(listInputField[position], itemInputFieldListener)
    }

    override fun getItemCount(): Int {
        return listInputField.size
    }

    fun submitList(listInput: MutableList<InputField>) {
        listInputField = listInput
        notifyDataSetChanged()
    }

}

class ItemInputFieldListener(val clickListener: (field: InputField) -> Unit) {
    fun clearField(field: InputField) = clickListener(field)
}