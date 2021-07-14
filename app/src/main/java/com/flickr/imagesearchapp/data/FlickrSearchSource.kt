package com.flickr.imagesearchapp.data
import androidx.paging.PagingSource
import com.flickr.imagesearchapp.di.AppModule
import retrofit2.HttpException
import java.io.IOException

class FlickrSearchSource (private val query: String): PagingSource<Int, FlickrPhoto>(){

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FlickrPhoto> {

        val position = params.key ?:1

        val flickrPhotoService = AppModule.provideFlickrApi(AppModule.provideRetrofit())

        val response = flickrPhotoService.getSearchResult(query,position)
        return try {
            val photos = response.photos.photo

            LoadResult.Page(
                data = photos,
                prevKey = if (position == 1) null else position - 1,
                nextKey = if (photos.isEmpty()) null else position + 1
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }

}