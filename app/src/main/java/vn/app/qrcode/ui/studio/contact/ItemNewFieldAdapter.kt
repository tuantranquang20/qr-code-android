package vn.app.qrcode.ui.studio.contact

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import vn.app.qrcode.data.model.ContactNewFieldModel
import vn.app.qrcode.data.model.InputField
import vn.app.qrcode.databinding.ItemNewFieldCheckBoxBinding

class ItemNewFieldAdapter(
    private val newFieldListener: NewFieldListener,
    private val listSelected : List<InputField>,
) : ListAdapter<InputField, ItemNewFieldAdapter.ViewHolder>(DiffCallback) {
    class ViewHolder(
        val binding: ItemNewFieldCheckBoxBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: InputField,
            newFieldListener: NewFieldListener,
            listSelected: List<InputField>
        ) {
            binding.inputField = item
            binding.checkType.isChecked = listSelected.contains(item)
            binding.newFieldListener = newFieldListener
            binding.executePendingBindings()
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<InputField>() {
            override fun areItemsTheSame(oldItem: InputField, newItem: InputField): Boolean {
                return oldItem.fieldType == newItem.fieldType
            }

            override fun areContentsTheSame(oldItem: InputField, newItem: InputField): Boolean {
                return oldItem.fieldValue == newItem.fieldValue
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemNewFieldCheckBoxBinding.inflate(
            LayoutInflater
                .from(parent.context), parent, false
        ))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), newFieldListener, listSelected)
    }

}

class NewFieldListener(val clickListener: (fieldModel: InputField) -> Unit) {
    fun onSelectedType(fieldModel: InputField) = clickListener(fieldModel)
}
