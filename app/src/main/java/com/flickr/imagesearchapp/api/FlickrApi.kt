package com.flickr.imagesearchapp.api

import com.flickr.imagesearchapp.BuildConfig
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface FlickrApi {

    companion object {
        const val BASE_URL= "https://api.flickr.com/"
        const val CLIENT_ID= BuildConfig.FLICKR_ACCESS_KEY
    }


    @GET("services/rest/?method=flickr.photos.getRecent&format=json&nojsoncallback=1&extras=url_s")
    fun recentPhotos(
        @Query("page") page: Int,
        @Query("per_page") perpage: Int=20,
        @Query("api_key") api_key: String= CLIENT_ID,
    ): Call<FlickrResponse>

    @GET("services/rest/?method=flickr.photos.search&format=json&nojsoncallback=1&extras=url_s")
    fun getSearchResult(
        @Query("text") query: String,
        @Query("api_key") key: String= CLIENT_ID,
    ): Call<FlickrResponse>
}