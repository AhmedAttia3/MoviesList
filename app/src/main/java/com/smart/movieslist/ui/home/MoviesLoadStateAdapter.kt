
package com.smart.movieslist.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.smart.movieslist.databinding.MoviesLoadStateFooterViewItemBinding
import com.smart.movieslist.utils.getErrorType
import com.smart.movieslist.utils.getMessage


class MoviesLoadStateAdapter(
    private val retry: () -> Unit
) : LoadStateAdapter<MoviesLoadStateAdapter.MoviesLoadStateViewHolder>() {
    override fun onBindViewHolder(holder: MoviesLoadStateAdapter.MoviesLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): MoviesLoadStateAdapter.MoviesLoadStateViewHolder {
        return MoviesLoadStateViewHolder(
            com.smart.movieslist.databinding.MoviesLoadStateFooterViewItemBinding.inflate(LayoutInflater.from(parent.context),parent,false),
        retry)
    }


    inner class MoviesLoadStateViewHolder(
        private val binding: MoviesLoadStateFooterViewItemBinding,
        retry: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.retryButton.setOnClickListener { retry.invoke() }
        }

        fun bind(loadState: LoadState) {
            if (loadState is LoadState.Error) {
                binding.errorMsg.text = loadState.error
                    .getErrorType()
                    .getMessage(binding.root.context)
            }
            binding.retryButton.isVisible = loadState is LoadState.Error
            binding.progressBar.isVisible = loadState is LoadState.Loading
            binding.errorMsg.isVisible = loadState is LoadState.Error
        }
    }

}
