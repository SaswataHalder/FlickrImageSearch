package com.flickr.imagesearchapp.ui.gallery

import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.flickr.imagesearchapp.api.FlickrResponse
import com.flickr.imagesearchapp.data.FlickrRepository
import com.flickr.imagesearchapp.di.AppModule
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class GalleryViewModel @ViewModelInject constructor(
    private val repository: FlickrRepository,
) : ViewModel() {
    //private lateinit var flickrResponse: MutableLiveData<FlickrResponse>
//    lateinit var isLoading: MutableLiveData<Boolean>
//    lateinit var error: MutableLiveData<Boolean>
//    lateinit var PAGE_NO: MutableLiveData<Int>
//    private var PAGE_SIZE = 20
    var response=repository.getRecentPages().cachedIn(viewModelScope)

    companion object {
        private val TAG = GalleryViewModel::class.java.simpleName
    }

//    fun getFlickrResponse(): MutableLiveData<FlickrResponse> {
//        if (!::flickrResponse.isInitialized) {
//            flickrResponse = MutableLiveData()
//            isLoading = MutableLiveData()
//            isLoading.value = false
//            error = MutableLiveData()
//            PAGE_NO = MutableLiveData()
//            PAGE_NO.value = 1
//            fetchFlickrResponse(PAGE_NO.value!!)
//        }
//
//        return flickrResponse
//    }

//    fun refresh() {
//        PAGE_NO.value = 1
//        fetchFlickrResponse(PAGE_NO.value!!)
//    }

//    fun loadNextPage() {
//        if (!(isLoading.value!!)) {
//            PAGE_NO.value = PAGE_NO.value!! + 1
//            fetchFlickrResponse(PAGE_NO.value!!)
//        }
//    }

//    private fun fetchFlickrResponse(page: Int) {
//        Log.i(TAG, "Page No: $page")
//        isLoading.value = true
//        error.value = false
//    }
}