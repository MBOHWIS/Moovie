package com.example.moovie

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

import kotlinx.android.synthetic.main.item_movie.view.*
import kotlinx.android.synthetic.main.item_movie.view.item_movie_overview
import kotlinx.android.synthetic.main.item_movie.view.item_movie_poster
import kotlinx.android.synthetic.main.item_movie.view.item_movie_rating
import kotlinx.android.synthetic.main.item_movie.view.item_movie_title

class AdapterTheMovieDb(private val context: Context, private var resultTheMovieDb: ArrayList<Result>) : RecyclerView.Adapter<AdapterTheMovieDb.ViewHolderTheMovieDb>() {

//    private val TAG = javaClass.simpleName

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderTheMovieDb =
        ViewHolderTheMovieDb(LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_movie, parent, false)

        )

    override fun onBindViewHolder(holder: ViewHolderTheMovieDb, position: Int) {
        val resultItem = resultTheMovieDb[position]
        Glide
            .with(context)
            .load(BuildConfig.BASE_URL_IMAGE + resultItem.posterPath)
            .into(holder.itemView.item_movie_poster)
        holder
            .itemView
            .item_movie_title
            .text = resultItem.originalTitle
        holder
            .itemView
            .item_movie_rating
            .text = resultItem.voteAverage.toString()
        holder
            .itemView
            .item_movie_release
            .text = resultItem.releaseDate
        holder
            .itemView
            .item_movie_overview
            .text = resultItem.overview
    }

    override fun getItemCount(): Int = resultTheMovieDb.size

    fun refreshAdapter(resultTheMovieDb: List<Result>) {
        this.resultTheMovieDb.addAll(resultTheMovieDb)
        notifyItemRangeChanged(0, this.resultTheMovieDb.size)
    }

    inner class ViewHolderTheMovieDb(itemView: View) : RecyclerView.ViewHolder(itemView)

}