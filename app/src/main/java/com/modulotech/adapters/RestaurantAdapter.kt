package com.modulotech.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.modulotech.databinding.ListItemRestaurantBinding
import com.modulotech.models.CallInfo

class RestaurantAdapter : ListAdapter<CallInfo, RestaurantAdapter.ViewHolder>(RestaurantDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                ListItemRestaurantBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ListItemRestaurantBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CallInfo) {
            binding.apply {
                callInfo = item
                executePendingBindings()
            }
        }
    }
}

private class RestaurantDiffCallback : DiffUtil.ItemCallback<CallInfo>() {

    override fun areItemsTheSame(oldItem: CallInfo, newItem: CallInfo): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: CallInfo, newItem: CallInfo): Boolean {
        return oldItem.name == newItem.name
    }
}