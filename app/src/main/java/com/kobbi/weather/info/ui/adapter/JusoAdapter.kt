package com.kobbi.weather.info.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.kobbi.weather.info.BR
import com.kobbi.weather.info.R
import com.kobbi.weather.info.databinding.ItemDialogSelectPlaceBinding
import com.kobbi.weather.info.presenter.listener.ClickListener

class JusoAdapter(items: List<String>) : RecyclerView.Adapter<JusoAdapter.ViewHolder>() {
    private val mItems = mutableListOf<String>()
    private var mClickListener: ClickListener? = null

    init {
        mItems.addAll(items)
    }

    override fun getItemCount(): Int = mItems.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemDialogSelectPlaceBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_dialog_select_place,
                parent,
                false
            )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mItems[position])
    }

    fun setItems(items: List<String>) {
        mItems.clear()
        mItems.addAll(items)
        notifyDataSetChanged()
    }

    fun setOnClickListener(clickListener: ClickListener) {
        mClickListener = clickListener
    }

    inner class ViewHolder(private val binding: ItemDialogSelectPlaceBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            val view = binding.root
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            v?.let { mClickListener?.onItemClick(layoutPosition, v) }
        }

        fun bind(place: String) {
            binding.setVariable(BR.place, place)
        }
    }
}