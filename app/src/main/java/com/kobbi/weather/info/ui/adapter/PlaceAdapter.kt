package com.kobbi.weather.info.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.kobbi.weather.info.BR
import com.kobbi.weather.info.R
import com.kobbi.weather.info.databinding.ItemFavoritePlaceBinding
import com.kobbi.weather.info.presenter.listener.ClickListener
import com.kobbi.weather.info.presenter.listener.LongClickListener

class PlaceAdapter(items: List<String>, selectedPositions:List<Int>) : RecyclerView.Adapter<PlaceAdapter.ViewHolder>() {
    private val mItems = mutableListOf<String>()
    private val mSelectedPositions = mutableListOf<Int>()
    private var mClickListener: ClickListener? = null
    private var mLongClickListener: LongClickListener? = null

    init {
        mItems.addAll(items)
        mSelectedPositions.addAll(selectedPositions)
    }

    override fun getItemCount() = mItems.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemFavoritePlaceBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.item_favorite_place, parent, false
            )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mItems[position], mSelectedPositions.contains(position))
    }

    fun setItems(items: List<String>, selectedPositions:List<Int>) {
        mItems.clear()
        mItems.addAll(items)

        mSelectedPositions.clear()
        mSelectedPositions.addAll(selectedPositions)
        notifyDataSetChanged()
    }

    fun setOnClickListener(clickListener: ClickListener) {
        mClickListener = clickListener
    }

    fun setOnLongClickListener(longClickListener: LongClickListener) {
        mLongClickListener = longClickListener
    }

    inner class ViewHolder(private val binding: ItemFavoritePlaceBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener,
        View.OnLongClickListener {
        init {
            val view = binding.root
            view.setOnClickListener(this)
            view.setOnLongClickListener(this)
        }

        override fun onClick(v: View?) {
            v?.let { mClickListener?.onItemClick(layoutPosition, v) }
        }

        override fun onLongClick(v: View?): Boolean {
            v?.let { mLongClickListener?.onItemLongClick(layoutPosition, v) }
            return true
        }

        fun bind(address: String, isSelected:Boolean) {
            binding.setVariable(BR.address, address)
            binding.setVariable(BR.isSelected, isSelected)
        }
    }
}