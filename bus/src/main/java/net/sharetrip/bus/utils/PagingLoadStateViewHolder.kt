package net.sharetrip.bus.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.bus.R
import net.sharetrip.bus.databinding.ItemBusHistoryNetworkBinding

class PagingLoadStateViewHolder(
    private val binding: ItemBusHistoryNetworkBinding,
    retry: () -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.retryLoadingButton.setOnClickListener { retry.invoke() }
    }

    fun bind(loadState: LoadState) {
        if (loadState is LoadState.Error) {
            binding.errorMessageTextView.text = loadState.error.localizedMessage
        }

        binding.loadingProgressBar.isVisible = loadState is LoadState.Loading
        binding.retryLoadingButton.isVisible = loadState is LoadState.Error
        binding.errorMessageTextView.isVisible = loadState is LoadState.Error
    }

    companion object {
        fun create(parent: ViewGroup, retry: () -> Unit): PagingLoadStateViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_bus_history_network, parent, false)
            val binding = ItemBusHistoryNetworkBinding.bind(view)
            return PagingLoadStateViewHolder(binding, retry)
        }
    }
}
