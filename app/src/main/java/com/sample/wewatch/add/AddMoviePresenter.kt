package com.sample.wewatch.add

import com.sample.wewatch.model.LocalDataSource
import com.sample.wewatch.model.Movie

class AddMoviePresenter( private var viewInterface: AddMovieContract.ViewInterface,
                         private var dataSource: LocalDataSource) :
    AddMovieContract.PresenterInterface {

    override fun addMovie(title: String, releaseDate: String, posterPath: String) {
        //1
        if (title.isEmpty()) {
            viewInterface.displayError ("Название фильма не может быть пустым")
        } else {
            //2
            //val movie = Movie(title, posterPath, releaseDate)
            val movie = Movie(
                title = title,
                posterPath = posterPath,
                releaseDate = releaseDate
            )
            dataSource.insert(movie)
            viewInterface.returnToMain()
        }
    }
}