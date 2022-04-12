package com.smart.movieslist.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.smart.movieslist.data.model.MovieModel
import com.smart.movieslist.databinding.MovieItemBinding


class MoviesAdapter :
    PagingDataAdapter<MovieModel, MoviesAdapter.MoviesViewHolder>(COMPARATOR) {
    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<MovieModel>() {
            override fun areItemsTheSame(oldItem: MovieModel, newItem: MovieModel): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: MovieModel, newItem: MovieModel): Boolean =
                oldItem == newItem
        }
    }
    var moviesAdapterCallback:MoviesAapterCallback? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoviesViewHolder {
        return MoviesViewHolder( MovieItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: MoviesViewHolder, position: Int) {
        val movieModel = getItem(position)
        holder.binding.movie = movieModel
        holder.binding.position = position
        moviesAdapterCallback?.let { holder.binding.callback = it }
    }


    inner class MoviesViewHolder(val binding: MovieItemBinding) : RecyclerView.ViewHolder(binding.root)

    interface MoviesAapterCallback{
        fun onItemClickListener(model: MovieModel, position: Int)
        fun onFavoriteIconClick(model: MovieModel, position: Int)
    }
}