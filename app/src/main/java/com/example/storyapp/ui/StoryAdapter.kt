package com.example.storyapp.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.data.model.ListStoryItem
import com.example.storyapp.databinding.ItemStoryBinding

class StoryAdapter(
    private val onClick: (ListStoryItem) -> Unit
) : PagingDataAdapter<ListStoryItem, StoryAdapter.StoryViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val view = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = getItem(position)
        story?.let { holder.bind(it) }
    }

    class StoryViewHolder(
        private val binding: ItemStoryBinding, val onClick: (ListStoryItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(storyItem: ListStoryItem) {
            binding.tvItemName.text = storyItem.name
            Glide.with(itemView.context).load(storyItem.photoUrl).placeholder(R.drawable.ic_loading)
                .error(R.drawable.ic_broken_image).into(binding.ivItemPhoto)

            itemView.setOnClickListener {
                onClick(storyItem)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(
                oldItem: ListStoryItem, newItem: ListStoryItem
            ): Boolean {
                return oldItem == newItem
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(
                oldItem: ListStoryItem, newItem: ListStoryItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}