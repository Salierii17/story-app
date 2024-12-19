package com.example.storyapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class StoryResponse(

    @field:SerializedName("error") val error: Boolean,

    @field:SerializedName("message") val message: String,

    @field:SerializedName("listStory") val listStory: List<ListStoryItem>,
)


data class StoryDetailResponse(

    @field:SerializedName("error") val error: Boolean,

    @field:SerializedName("message") val message: String,

    @field:SerializedName("story") val story: ListStoryItem
)

@Entity(tableName = "story")
data class ListStoryItem(

    @PrimaryKey
    @field:SerializedName("id") val id: String,

    @field:SerializedName("name") val name: String,

    @field:SerializedName("description") val description: String,

    @field:SerializedName("photoUrl") val photoUrl: String,

    @field:SerializedName("createdAt") val createdAt: String,

    @field:SerializedName("lat") val lat: Double,

    @field:SerializedName("lon") val lon: Double
)
