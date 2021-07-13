package com.flickr.imagesearchapp.data

import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


class FlickrPhoto(
    val id: String,
){
    var title: String? = null

    @field:SerializedName("url_s")
    var url: String? = null

    @field:SerializedName("width_s")
    var width: String? = null

    @field:SerializedName("height_s")
    var height: String? = null
}

data class FlickrPhotos (
    val page: Int = 0,
    val perpage: Int = 0,
    val photo: List<FlickrPhoto>? = null,
)