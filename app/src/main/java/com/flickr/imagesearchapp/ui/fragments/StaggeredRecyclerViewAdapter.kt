package com.flickr.imagesearchapp.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.flickr.imagesearchapp.R
import com.flickr.imagesearchapp.data.FlickrPhoto
//import com.flickr.imagesearchapp.util.getProgressDrawable
//import com.flickr.imagesearchapp.util.loadImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.item_flickr_photo.view.*

class StaggeredRecyclerViewAdapter(var listFlickrPhoto: ArrayList<FlickrPhoto>) :
    PagingDataAdapter<FlickrPhoto,StaggeredRecyclerViewAdapter.ViewHolder>(COMPARE) {

//    fun updatePhoto(newPhoto: List<FlickrPhoto>, pageNo: Int) {
//        if (pageNo == 1)
//            listFlickrPhoto.clear()
//        listFlickrPhoto.addAll(newPhoto)
//        notifyDataSetChanged()
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : ViewHolder{
        val binding =LayoutInflater.from(parent.context).inflate(R.layout.item_flickr_photo, parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listFlickrPhoto[position])
    }

    override fun getItemCount() = listFlickrPhoto.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val imageView = view.image_view
        private val textView= view.text_view_title

        fun bind(flickrPhoto: FlickrPhoto) {
                    Glide.with(itemView)
                    .load(flickrPhoto.url)
                    .error(R.mipmap.ic_launcher)
                    .into(imageView)

            textView.setText(flickrPhoto.title)

        }
    }

    companion object {
        private val COMPARE = object : DiffUtil.ItemCallback<FlickrPhoto>() {
            override fun areItemsTheSame(oldItem: FlickrPhoto, newItem: FlickrPhoto) =
                oldItem.id == newItem.id

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: FlickrPhoto, newItem: FlickrPhoto) =
                oldItem == newItem
        }
    }
}