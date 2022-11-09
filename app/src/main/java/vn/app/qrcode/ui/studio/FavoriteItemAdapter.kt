package vn.app.qrcode.ui.studio

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.base.common.utils.ext.setDebounceClickListener
import vn.app.qrcode.R
import vn.app.qrcode.data.constant.QrItemType
import vn.app.qrcode.data.model.QRCodeCreator
import vn.app.qrcode.databinding.ItemQrCreatorBinding

class FavoriteItemAdapter(
    private val qrItemListener: QrItemListener
): ListAdapter<QRCodeCreator, FavoriteItemAdapter.FavoriteItemViewHolder>(FavoriteItemDiffCallback) {

    var selectedList = mutableListOf<Long>()
    var itemType = QrItemType.FAVORITE_ITEM

    inner class FavoriteItemViewHolder(
        val binding: ItemQrCreatorBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: QRCodeCreator) {
            binding.apply {
                qrCodeCreator = item
                qrItemType = itemType

                if (selectedList.contains(item.id)) {
                    imgMarkFavoriteItem.visibility = View.VISIBLE
                    cvFavoriteItem.setCardBackgroundColor(
                        ContextCompat.getColor(
                            cvFavoriteItem.context,
                            R.color.blue5
                        )
                    )
                } else {
                    imgMarkFavoriteItem.visibility = View.GONE
                    cvFavoriteItem.setCardBackgroundColor(
                        ContextCompat.getColor(
                            cvFavoriteItem.context,
                            R.color.white
                        )
                    )
                }
                cvFavoriteItem.setOnLongClickListener { markSelectedItem(item) }
                cvFavoriteItem.setOnClickListener { selectItem(item) }

                deleteFavoriteBtn.setDebounceClickListener { deleteFavoriteItem(item) }

                shareFavoriteBtn.setDebounceClickListener { shareItem(item) }
                executePendingBindings()
            }
        }
    }

    fun shareItem(item: QRCodeCreator) {
        qrItemListener.clickShareListener(item)
    }

    private fun deleteFavoriteItem(item: QRCodeCreator) {
        qrItemListener.onFavoriteClick(item)
    }

    private fun selectItem(item: QRCodeCreator): Boolean {
        if (selectedList.isNullOrEmpty()) {
            qrItemListener.clickListener(item)
            return false
        }

        if (selectedList.contains(item.id)) {
            selectedList.remove(item.id)
        } else {
            selectedList.add(item.id)
        }
        qrItemListener.updateListSelectItem(selectedList)
        return true
    }

    private fun markSelectedItem(item: QRCodeCreator): Boolean {
        if (selectedList.isNullOrEmpty()) {
            selectedList.add(item.id)
            qrItemListener.updateListSelectItem(selectedList)
        }
        return false
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setListSelectedItem(listItems: MutableList<Long>) {
        selectedList = listItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteItemViewHolder {
        return FavoriteItemViewHolder(
            ItemQrCreatorBinding.inflate(
                LayoutInflater
                    .from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: FavoriteItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val FavoriteItemDiffCallback = object : DiffUtil.ItemCallback<QRCodeCreator>() {
            override fun areItemsTheSame(oldItem: QRCodeCreator, newItem: QRCodeCreator): Boolean {
                return oldItem.id == newItem.id
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(
                oldItem: QRCodeCreator,
                newItem: QRCodeCreator,
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}