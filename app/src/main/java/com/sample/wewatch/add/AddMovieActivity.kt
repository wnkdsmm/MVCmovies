package com.sample.wewatch.add

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.sample.wewatch.R
import com.sample.wewatch.search.SearchActivity
import com.sample.wewatch.model.LocalDataSource
import com.sample.wewatch.model.Movie
import com.sample.wewatch.network.RetrofitClient.TMDB_IMAGEURL

import com.squareup.picasso.Picasso

class AddMovieActivity : AppCompatActivity(), AddMovieContract.ViewInterface {
  private lateinit var titleEditText: EditText
  private lateinit var releaseDateEditText: EditText
  private lateinit var movieImageView: ImageView
  //private lateinit var dataSource: LocalDataSource
  //12
  private lateinit var addMoviePresenter: AddMoviePresenter

  fun setupPresenter() {
    val dataSource = LocalDataSource(application)
    addMoviePresenter =  AddMoviePresenter(this, dataSource)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    window.statusBarColor = ContextCompat.getColor(this, R.color.background_dark)
    window.navigationBarColor = ContextCompat.getColor(this, R.color.background_dark)
    setContentView(R.layout.activity_add_movie)
    setupViews()
    //dataSource = LocalDataSource(application)
    setupPresenter()
  }
  // 12
  override  fun  displayMessage (message: String ) {
    Toast.makeText( this@AddMovieActivity , message,
      Toast.LENGTH_LONG).show()
  }

  override  fun  displayError (message: String ) {
    displayMessage (message)
  }

  override fun returnToMain() {
    setResult(Activity.RESULT_OK)
    finish()
  }
  //
  fun setupViews() {
    titleEditText = findViewById(R.id.movie_title)
    releaseDateEditText = findViewById(R.id.movie_release_date)
    movieImageView = findViewById(R.id.movie_imageview)
  }

  //search onClick
  fun goToSearchMovieActivity(v: View) {
    val title = titleEditText.text.toString()
    val intent = Intent(this@AddMovieActivity, SearchActivity::class.java)
    intent.putExtra(SearchActivity.SEARCH_QUERY, title)
    startActivityForResult(intent, SEARCH_MOVIE_ACTIVITY_REQUEST_CODE)
  }

  //addMovie onClick
  fun onClickAddMovie(v: View) {
    val title = titleEditText.text.toString()
    val releaseDate = releaseDateEditText.text.toString()
    val posterPath = if (movieImageView.tag != null )
      movieImageView.tag.toString() else  ""
    addMoviePresenter.addMovie (title, releaseDate, posterPath)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)

    this@AddMovieActivity.runOnUiThread {
      titleEditText.setText(data?.getStringExtra(SearchActivity.EXTRA_TITLE))
      releaseDateEditText.setText(data?.getStringExtra(SearchActivity.EXTRA_RELEASE_DATE))
      movieImageView.tag = data?.getStringExtra(SearchActivity.EXTRA_POSTER_PATH)
      Picasso.get().load(TMDB_IMAGEURL + data?.getStringExtra(SearchActivity.EXTRA_POSTER_PATH)).into(movieImageView)
    }
  }

  fun showToast(string: String) {
    Toast.makeText(this@AddMovieActivity, string, Toast.LENGTH_LONG).show()
  }

  companion object {
    const val SEARCH_MOVIE_ACTIVITY_REQUEST_CODE = 2
  }
}
