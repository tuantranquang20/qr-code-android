package vn.app.qrcode.ui.studio

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.base.common.utils.ext.setDebounceClickListener
import vn.app.qrcode.R
import vn.app.qrcode.data.constant.QrItemType
import vn.app.qrcode.data.model.QRCodeCreator
import vn.app.qrcode.databinding.ItemQrCreatorStudioBinding

class QrCodeFilterStudioAdapter(
    private val qrItemListener: QrItemListener
): ListAdapter<QRCodeCreator, QrCodeFilterStudioAdapter.ViewHolder>(DiffCallback) {

    var selectedList = mutableListOf<Long>()
    var itemType = QrItemType.YOUR_CODE_ITEM

    inner class ViewHolder(
        val binding: ItemQrCreatorStudioBinding,
        private val itemListener: QrItemListener
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: QRCodeCreator) {
            binding.apply {
                qrCodeCreator = item
                qrItemListener = itemListener
                qrItemType = itemType
                favoriteBtn.isSelected = item.isFavorite

                if (selectedList.contains(item.id)) {
                    imgMarkItem.visibility = View.VISIBLE
                    cvItemContent.setCardBackgroundColor(
                        ContextCompat.getColor(
                            cvItemContent.context,
                            R.color.blue5
                        )
                    )
                } else {
                    imgMarkItem.visibility = View.GONE
                    cvItemContent.setCardBackgroundColor(
                        ContextCompat.getColor(
                            cvItemContent.context,
                            R.color.white
                        )
                    )
                }

                cvItemContent.setOnLongClickListener { markSelectedItem(item) }
                cvItemContent.setOnClickListener { selectItem(item) }
                if (qrItemType == QrItemType.FAVORITE_ITEM) {
                    flDeleteBtn.visibility = View.GONE
                } else {
                    flDeleteBtn.visibility = View.VISIBLE
                    deleteBtn.setDebounceClickListener { deleteItem(item) }
                }

                shareBtn.setDebounceClickListener { shareItem(item) }
                executePendingBindings()
            }
        }
    }

    fun shareItem(item: QRCodeCreator) {
        qrItemListener.clickShareListener(item)
    }

    private fun deleteItem(item: QRCodeCreator) {
        qrItemListener.clickDeleteListener(item)
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

    fun setQrItemType(type: QrItemType) {
        itemType = type
        notifyDataSetChanged()
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<QRCodeCreator>() {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemQrCreatorStudioBinding.inflate(
                LayoutInflater
                    .from(parent.context), parent, false
            ),
            qrItemListener
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class QrItemListener(
    val clickListener: (item: QRCodeCreator) -> Unit,
    val clickDeleteListener: (item: QRCodeCreator) -> Unit,
    val clickFavoriteListener: (item: QRCodeCreator) -> Unit,
    val clickShareListener: (item: QRCodeCreator) -> Unit,
    val updateListSelectItem: (listSelectItem: MutableList<Long>) -> Unit,
) {
    fun onFavoriteClick(item: QRCodeCreator) = clickFavoriteListener(item)
}
