package com.flickr.imagesearchapp.api

import com.flickr.imagesearchapp.data.FlickrPhoto
import com.flickr.imagesearchapp.data.FlickrPhotos

data class FlickrResponse(
    val stat: String? =null,
    val photos: FlickrPhotos?=null,
)
