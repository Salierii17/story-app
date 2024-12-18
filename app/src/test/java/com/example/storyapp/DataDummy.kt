package com.example.storyapp

import com.example.storyapp.data.model.ListStoryItem

object DataDummy {
//    fun generateDummyStoryResponse(): StoryResponse {
//        return StoryResponse(
//            error = false,
//            message = "Success",
//            listStory = listOf(generateDummyListStory())
//        )
//    }

    private fun generateDummyListStory(): ListStoryItem {
        return ListStoryItem(
            id = "story-1",
            name = "John Doe",
            description = "Test Story",
            photoUrl = "https://story.com/photo.jpg",
            lat = 30,
            lon = 30,
            createdAt = "2024-12-18",
        )
    }

    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                i.toString(),
                "name + $i",
                "story $i",
                "https://story.com/photo.jpg",
                "2024-12-18",
                30,
                30,
            )
            items.add(story)
        }
        return items
    }
}