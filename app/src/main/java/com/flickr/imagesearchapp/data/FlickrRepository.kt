package com.flickr.imagesearchapp.data

import com.flickr.imagesearchapp.api.FlickrApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlickrRepository  @Inject constructor(private val flickrApi: FlickrApi) {
}