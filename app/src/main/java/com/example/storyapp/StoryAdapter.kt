package com.example.storyapp

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyapp.data.model.ListStoryItem
import com.example.storyapp.databinding.ItemStoryBinding

class StoryAdapter(
    private val sizeOption: Int, private val onClick: (ListStoryItem) -> Unit
) : ListAdapter<ListStoryItem, StoryAdapter.StoryViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val view = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = getItem(position)
        holder.bind(story)
//        holder.sizeOption(sizeOption)
    }

    class StoryViewHolder(
        private var binding: ItemStoryBinding, val onClick: (ListStoryItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(storyItem: ListStoryItem) {
            binding.tvItemName.text = storyItem.name
            Glide.with(itemView.context).load(storyItem.photoUrl).placeholder(R.drawable.ic_loading)
                .error(R.drawable.ic_broken_image).into(binding.ivItemPhoto)

            itemView.setOnClickListener {
                onClick(storyItem)
            }
        }

//        fun sizeOption(sizeOption: Int) {
//            when (sizeOption) {
//                SIZE_SMALL -> {
//                    binding.main.layoutParams.width = 500
//                    binding.main.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
//                    binding.tvName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
//                    binding.tvName.maxLines = 1
//                    binding.tvName.ellipsize = TextUtils.TruncateAt.END
//                    binding.imgMediaCover.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
//                }
//
//                SIZE_LARGE -> {
//                    binding.main.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
//                    binding.main.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
//
//                }
//            }
//        }
    }

    companion object {

        const val SIZE_SMALL = 1
        const val SIZE_LARGE = 2

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