package com.sample.wewatch.search

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sample.wewatch.R
import com.sample.wewatch.model.RemoteDataSource
import com.sample.wewatch.model.TmdbResponse
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers

import io.reactivex.annotations.NonNull
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers

//const val SEARCH_QUERY = "searchQuery"

class SearchActivity : AppCompatActivity(), SearchContract.ViewInterface {
  private val TAG = "SearchActivity"
  private lateinit var searchResultsRecyclerView: RecyclerView
  private lateinit var adapter: SearchAdapter
  private lateinit var noMoviesTextView: TextView
  private lateinit var progressBar: ProgressBar
  private var query = ""

  //private var dataSource = RemoteDataSource()

  //15
  private lateinit var searchPresenter: SearchPresenter

  private fun setupPresenter() {
    val dataSource = RemoteDataSource()
    searchPresenter = SearchPresenter(this, dataSource)
  }
  //

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    window.statusBarColor = ContextCompat.getColor(this, R.color.background_dark)
    window.navigationBarColor = ContextCompat.getColor(this, R.color.background_dark)
    setContentView(R.layout.activity_search_movie)
    searchResultsRecyclerView = findViewById(R.id.search_results_recyclerview)
    noMoviesTextView = findViewById(R.id.no_movies_textview)
    progressBar = findViewById(R.id.progress_bar)

    val i = intent
    query = i.getStringExtra(SEARCH_QUERY) ?: ""
    setupViews()
    setupPresenter()
  }

  override fun onStart() {
    super.onStart()

    progressBar.visibility = VISIBLE
    searchPresenter.getSearchResults(query)
  }

  override fun onStop() {
    super.onStop()
    searchPresenter.stop()
  }

  private fun setupViews() {
    searchResultsRecyclerView.layoutManager = LinearLayoutManager(this)
  }

  override fun displayMessage(message: String ) {
    Toast.makeText(this@SearchActivity , message, Toast. LENGTH_LONG ).show()
  }



  override fun displayResult(tmdbResponse: TmdbResponse) {
    progressBar.visibility = INVISIBLE

    if (tmdbResponse.totalResults == null || tmdbResponse.totalResults == 0) {
      searchResultsRecyclerView.visibility = INVISIBLE
      noMoviesTextView.visibility = VISIBLE
    } else {
      adapter = SearchAdapter(tmdbResponse.results
              ?: arrayListOf(), this@SearchActivity, itemListener)
      searchResultsRecyclerView.adapter = adapter

      searchResultsRecyclerView.visibility = VISIBLE
      noMoviesTextView.visibility = INVISIBLE
    }
  }

  fun showToast(string: String) {
    Toast.makeText(this@SearchActivity, string, Toast.LENGTH_LONG).show()
  }

  override fun displayError(string: String) {
    showToast(string)
  }

  companion object {

    val SEARCH_QUERY = "searchQuery"
    val EXTRA_TITLE = "SearchActivity.TITLE_REPLY"
    val EXTRA_RELEASE_DATE = "SearchActivity.RELEASE_DATE_REPLY"
    val EXTRA_POSTER_PATH = "SearchActivity.POSTER_PATH_REPLY"
  }

  /**
   * Listener for clicks on tasks in the ListView.
   */
  internal var itemListener: RecyclerItemListener = object : RecyclerItemListener {
    override fun onItemClick(view: View, position: Int) {
      val movie = adapter.getItemAtPosition(position)

      val replyIntent = Intent()
      replyIntent.putExtra(EXTRA_TITLE, movie.title)
      replyIntent.putExtra(EXTRA_RELEASE_DATE, movie.getReleaseYearFromDate())
      replyIntent.putExtra(EXTRA_POSTER_PATH, movie.posterPath)
      setResult(Activity.RESULT_OK, replyIntent)

      finish()
    }
  }

  interface RecyclerItemListener {
    fun onItemClick(v: View, position: Int)
  }

}

