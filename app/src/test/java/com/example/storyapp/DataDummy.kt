package com.example.storyapp

import com.example.storyapp.data.model.ListStoryItem

object DataDummy {
    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                i.toString(),
                "name + $i",
                "story $i",
                "https://story.com/photo.jpg",
                "2024-12-18",
                30.0,
                30.0,
            )
            items.add(story)
        }
        return items
    }
}