package com.flickr.imagesearchapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.flickr.imagesearchapp.R
import com.flickr.imagesearchapp.api.FlickrResponse
import com.flickr.imagesearchapp.data.FlickrPhoto
import com.flickr.imagesearchapp.data.FlickrPhotos
import com.flickr.imagesearchapp.ui.gallery.GalleryViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_gallery.*

@AndroidEntryPoint
class GalleryFragment : Fragment(R.layout.fragment_gallery) {
    private lateinit var viewModel: GalleryViewModel
    private lateinit var flickrResponse: MutableLiveData<FlickrResponse>
    private lateinit var error: MutableLiveData<Boolean>
    private lateinit var PAGE_NO: MutableLiveData<Int>
    private val staggeredRecyclerViewAdapter = StaggeredRecyclerViewAdapter(arrayListOf())
    private val NUM_COLUMNS = 2

    private lateinit var manager: StaggeredGridLayoutManager
    private var isScrolling = false

    companion object {
        private val TAG = GalleryFragment::class.java.simpleName
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(GalleryViewModel::class.java)
        flickrResponse = viewModel.getFlickrResponse()
        error = viewModel.error
        PAGE_NO = viewModel.PAGE_NO

        observeViewModel()

        manager = StaggeredGridLayoutManager(NUM_COLUMNS, LinearLayout.VERTICAL)
        recycler_view.apply {
            layoutManager = manager
            adapter = staggeredRecyclerViewAdapter
        }

        recycler_view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val totalItems = manager.itemCount
                val currentItems = manager.childCount
                val scrollOutItems = manager.findFirstVisibleItemPositions(null)

                if (isScrolling && (scrollOutItems[0] + currentItems >= totalItems)) {
                    isScrolling = false
                    Log.i(TAG, "$totalItems $currentItems (${scrollOutItems[0]}, ${scrollOutItems[1]})")
                    viewModel.loadNextPage()
                }
            }
        })

        swipeRefreshLayout.setOnRefreshListener {
            viewModel.refresh()
        }
    }
    private fun observeViewModel() {
        flickrResponse.observe(viewLifecycleOwner, Observer { flickrResponse ->
            flickrResponse?.let {
                val flickrPhotos: FlickrPhotos = it.photos!!
                val listFlickrPhotos: List<FlickrPhoto> = flickrPhotos.photo!!
                staggeredRecyclerViewAdapter.updatePhoto(listFlickrPhotos, PAGE_NO.value!!)
                swipeRefreshLayout.isRefreshing = false
                for (i in listFlickrPhotos) {
                    Log.i(TAG, "${i.url}")
                }
            }
        })

        error.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it)
                    swipeRefreshLayout.isRefreshing = false
            }
        })

        viewModel.isLoading.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it) {
                    progress_bar.visibility = View.VISIBLE
                } else {
                    progress_bar.visibility = View.GONE
                }
            }
        })
    }
}

