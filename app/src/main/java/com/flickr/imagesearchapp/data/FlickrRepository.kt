package com.flickr.imagesearchapp.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.flickr.imagesearchapp.api.FlickrApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlickrRepository  @Inject constructor(private val flickrApi: FlickrApi) {
    fun getRecentPages()= Pager(config = PagingConfig(pageSize = 20, enablePlaceholders = false),pagingSourceFactory={FlickrPagingSource()}).liveData
    fun getSearchPages(query:String)= Pager(config = PagingConfig(pageSize = 20,enablePlaceholders = false),pagingSourceFactory = {FlickrSearchSource(query)}).liveData

}