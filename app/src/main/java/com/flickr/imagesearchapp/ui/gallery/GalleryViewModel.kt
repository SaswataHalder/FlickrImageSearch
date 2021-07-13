package com.flickr.imagesearchapp.ui.gallery

import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.flickr.imagesearchapp.api.FlickrResponse
import com.flickr.imagesearchapp.data.FlickrRepository
import com.flickr.imagesearchapp.di.AppModule
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class GalleryViewModel @ViewModelInject constructor(
    private val repository: FlickrRepository,
    @Assisted state: SavedStateHandle,
) : ViewModel() {
    private lateinit var flickrResponse: MutableLiveData<FlickrResponse>
    lateinit var isLoading: MutableLiveData<Boolean>
    lateinit var error: MutableLiveData<Boolean>
    lateinit var PAGE_NO: MutableLiveData<Int>
    private var PAGE_SIZE = 20

    companion object {
        private val TAG = GalleryViewModel::class.java.simpleName
    }

    fun getFlickrResponse(): MutableLiveData<FlickrResponse> {
        if (!::flickrResponse.isInitialized) {
            flickrResponse = MutableLiveData()
            isLoading = MutableLiveData()
            isLoading.value = false
            error = MutableLiveData()
            PAGE_NO = MutableLiveData()
            PAGE_NO.value = 1
            fetchFlickrResponse(PAGE_NO.value!!)
        }

        return flickrResponse
    }

    fun refresh() {
        PAGE_NO.value = 1
        fetchFlickrResponse(PAGE_NO.value!!)
    }

    fun loadNextPage() {
        if (!(isLoading.value!!)) {
            PAGE_NO.value = PAGE_NO.value!! + 1
            fetchFlickrResponse(PAGE_NO.value!!)
        }
    }

    private fun fetchFlickrResponse(page: Int) {
        Log.i(TAG, "Page No: $page")
        isLoading.value = true
        error.value = false

        val flickrPhotoService = AppModule.provideFlickrApi(AppModule.provideRetrofit())

        val response = flickrPhotoService.recentPhotos(page, PAGE_SIZE, "6f102c62f41998d151e5a1b48713cf13")

        response.enqueue(object : Callback<FlickrResponse> {
            override fun onFailure(call: Call<FlickrResponse>, t: Throwable) {
                isLoading.value = false
                error.value = true
            }

            override fun onResponse(call: Call<FlickrResponse>, response: Response<FlickrResponse>) {
                isLoading.value = false
                if (response.isSuccessful) {
                    flickrResponse.value = response.body()
                }else{
                    error.value = true
                }
            }
        })
    }
}