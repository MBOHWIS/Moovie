package com.example.moovie

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {

    private val TAG = javaClass.simpleName
    private var adapterTheMovieDb by Delegates.notNull<AdapterTheMovieDb>()
    private var isLoading by Delegates.notNull<Boolean>()
    private var page by Delegates.notNull<Int>()
    private var totalPage by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        page = 1
        totalPage = 0
        doLoadData()
        initListener()

    }

    @SuppressLint("CheckResult")
    private fun doLoadData() {
        Log.d(TAG, "page: $page")
        true.showLoading()
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
        val apiTheMovieDb = retrofit.create(ApiTheMovieDb::class.java)
        apiTheMovieDb.getNowPlaying(page = page)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { theMovieDb: TheMovieDb ->
                    val resultTheMovieDb = theMovieDb.results as ArrayList
                    if (page == 1) {
                        adapterTheMovieDb = AdapterTheMovieDb(
                            this@MainActivity,
                            resultTheMovieDb
                        )
                        main_recycler_view.layoutManager = GridLayoutManager(this@MainActivity,2)
                        main_recycler_view.adapter = adapterTheMovieDb
                    } else {
                        adapterTheMovieDb.refreshAdapter(resultTheMovieDb)
                    }
                    totalPage = theMovieDb.totalPages
                },
                { t: Throwable ->
                    t.printStackTrace()
                },
                {
                    hideLoading()
                }
            )
    }

    private fun initListener() {
        main_recycler_view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val linearLayoutManager = recyclerView.layoutManager as GridLayoutManager
                val countItem = linearLayoutManager.itemCount
                val lastVisiblePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition()
                val isLastPosition = countItem.minus(1) == lastVisiblePosition
                Log.d(TAG, "countItem: $countItem")
                Log.d(TAG, "lastVisiblePosition: $lastVisiblePosition")
                Log.d(TAG, "isLastPosition: $isLastPosition")
                if (!isLoading && isLastPosition && page < totalPage) {
                    true.showLoading()
                    page = page.plus(1)
                    doLoadData()
                }
            }
        })
    }

    private fun Boolean.showLoading() {
        isLoading = true
        main_progress_bar.visibility = View.VISIBLE
        main_recycler_view.visibility.let {
            if (this) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    private fun hideLoading() {
        isLoading = false
        main_progress_bar.visibility = View.GONE
        main_recycler_view.visibility = View.VISIBLE
    }

}
