package vn.app.qrcode.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import vn.app.qrcode.data.model.QRCodeType
import vn.app.qrcode.databinding.ItemCheckBoxBinding

class ItemFilterAdapter(
    private val qrCodeTypeListener: QrCodeTypeListener,
    private val listSelected : List<Int>,
) : ListAdapter<QRCodeType, ItemFilterAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(
        val binding: ItemCheckBoxBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: QRCodeType,
            qrCodeTypeListener: QrCodeTypeListener,
            listSelected: List<Int>
        ) {
            binding.qrCodeType = item
            binding.checkType.isChecked = listSelected.contains(item.type)
            binding.qrCodeTypeListener = qrCodeTypeListener
            binding.executePendingBindings()
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<QRCodeType>() {
            override fun areItemsTheSame(oldItem: QRCodeType, newItem: QRCodeType): Boolean {
                return oldItem.type == newItem.type
            }

            override fun areContentsTheSame(oldItem: QRCodeType, newItem: QRCodeType): Boolean {
                return oldItem.nameId == newItem.nameId
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemCheckBoxBinding.inflate(
            LayoutInflater
                .from(parent.context), parent, false
        ))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), qrCodeTypeListener, listSelected)
    }

}

class QrCodeTypeListener(val clickListener: (type: QRCodeType) -> Unit) {
    fun onSelectedType(type: QRCodeType) = clickListener(type)
}
