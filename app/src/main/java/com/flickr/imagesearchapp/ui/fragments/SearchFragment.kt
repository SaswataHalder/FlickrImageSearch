package com.flickr.imagesearchapp.ui.fragments

import dagger.hilt.android.AndroidEntryPoint
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.paging.LoadState
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.flickr.imagesearchapp.R
import com.flickr.imagesearchapp.api.FlickrResponse
import com.flickr.imagesearchapp.data.FlickrPhoto
import com.flickr.imagesearchapp.data.FlickrPhotos
import com.flickr.imagesearchapp.ui.gallery.SearchViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_gallery.*
import java.util.*
import io.reactivex.Observable
import io.reactivex.Observer
import java.util.concurrent.TimeUnit
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class SearchFragment : Fragment(R.layout.fragment_gallery) {
    private lateinit var searchView: SearchView

    private val disposables = CompositeDisposable()
    private var timeSinceLastRequest: Long = 0

    private lateinit var viewModel: SearchViewModel
    private lateinit var flickrResponse: MutableLiveData<FlickrResponse>
    private lateinit var queryText: MutableLiveData<String>
    private lateinit var error: MutableLiveData<Boolean>

    private val searchRecyclerViewAdapter = SearchRecyclerViewAdapter(arrayListOf())
    private val NUM_COLUMNS = 2

    companion object {
        private val TAG = SearchFragment::class.java.simpleName
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
        queryText = viewModel.queryText
        error = viewModel.error

        observeViewModel()

        recycler_view.apply {
            layoutManager = StaggeredGridLayoutManager(NUM_COLUMNS, LinearLayout.VERTICAL)
            adapter = searchRecyclerViewAdapter
        }

        searchRecyclerViewAdapter.addLoadStateListener { loadState ->

            progress_bar.isVisible = loadState.source.refresh is LoadState.Loading
            recycler_view.isVisible = loadState.source.refresh is LoadState.NotLoading
            button_retry.isVisible = loadState.source.refresh is LoadState.Error
            text_view_error.isVisible = loadState.source.refresh is LoadState.Error

            // empty view
            if (loadState.source.refresh is LoadState.NotLoading &&
                loadState.append.endOfPaginationReached &&
                searchRecyclerViewAdapter.itemCount < 1
            ) {
                recycler_view.isVisible = false
                text_view_empty.isVisible = true
            } else {
                text_view_empty.isVisible = false
            }

        }
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        menu.clear()
        inflater.inflate(R.menu.menu_gallery, menu)

        val item = menu.findItem(R.id.action_search)
        searchView = item.actionView as SearchView

        timeSinceLastRequest = System.currentTimeMillis()

        createDebounceOperator()
    }

    private fun createDebounceOperator() {
        // Create the Observable
        val observableQueryText = Observable
            .create<String> { emitter ->
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String): Boolean {
                        return false
                    }

                    override fun onQueryTextChange(newText: String): Boolean {
                        if (!emitter.isDisposed) {
                            emitter.onNext(newText)
                        }
                        return false
                    }
                })
            }
            .debounce(800, TimeUnit.MILLISECONDS)  // Apply Debounce() operator to limit requests
            .subscribeOn(Schedulers.io())

        // Subscribe an Observer
        observableQueryText.subscribe(object : Observer<String> {
            override fun onSubscribe(d: Disposable) {
                disposables.add(d)
            }

            override fun onNext(s: String) {
                val str = "onNext: time since last request: " + (System.currentTimeMillis() - timeSinceLastRequest)
                Log.d(TAG, str)
                Log.d(TAG, "onNext: search query: $s")
                timeSinceLastRequest = System.currentTimeMillis()

                sendRequestToServer(s)
            }

            override fun onError(e: Throwable) {
                Log.d(TAG, "onError: ${e.message}")
            }

            override fun onComplete() {
                Log.d(TAG, "onComplete:")
            }
        })
    }

    private fun sendRequestToServer(query: String) {
        queryText.postValue(query.trim())
    }

    private fun observeViewModel() {
        viewModel._searchimages.observe(viewLifecycleOwner, androidx.lifecycle.Observer { flickrResponse ->
            flickrResponse?.let {
                searchRecyclerViewAdapter.submitData(viewLifecycleOwner.lifecycle,it)
            }
        })

        viewModel.queryText.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let {
                viewModel.makeQuery(queryText.value!!)
            }
        })

        error.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let {
                if (it) {
                    Snackbar.make(swipeRefreshLayout, "Network failed", Snackbar.LENGTH_INDEFINITE)
                        .setAction("RETRY") { viewModel.makeQuery(queryText.value!!) }
                        .show()
                }
            }
        })

        viewModel.isLoading.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let {
                if (it)
                    progress_bar.visibility = View.VISIBLE
                else
                    progress_bar.visibility = View.GONE
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }
}