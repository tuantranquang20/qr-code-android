package vn.app.qrcode.ui.studio.creator

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import vn.app.qrcode.data.model.QRCodeType
import vn.app.qrcode.databinding.ItemCreatorBinding

class ItemCreatorAdapter(
    private val itemCreatorListener: ItemCreatorListener,
) : ListAdapter<QRCodeType, ItemCreatorAdapter.ItemViewHolder>(CreatorDiffCallback) {
    class ItemViewHolder(val binding: ItemCreatorBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(qrCodeType: QRCodeType, itemCreatorListener: ItemCreatorListener) {
            binding.qrCodeType = qrCodeType
            binding.itemCreatorListener = itemCreatorListener
            binding.executePendingBindings()
        }
    }

    object CreatorDiffCallback : DiffUtil.ItemCallback<QRCodeType>() {
        override fun areItemsTheSame(oldItem: QRCodeType, newItem: QRCodeType): Boolean {
            return oldItem.type == newItem.type
        }

        override fun areContentsTheSame(oldItem: QRCodeType, newItem: QRCodeType): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ItemCreatorBinding.inflate(LayoutInflater.from(parent.context),
                parent,
                false)
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position), itemCreatorListener)
    }
}

class ItemCreatorListener(val clickListener: (type: QRCodeType) -> Unit) {
    fun onSelectedItem(type: QRCodeType) = clickListener(type)
}
