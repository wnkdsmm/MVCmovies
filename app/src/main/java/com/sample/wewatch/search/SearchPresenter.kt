package com.sample.wewatch.search

import android.util.Log
import com.sample.wewatch.model.RemoteDataSource
import com.sample.wewatch.model.TmdbResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import androidx.annotation.NonNull
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable

class SearchPresenter (
    private var viewInterface: SearchContract.ViewInterface,
    private var dataSource: RemoteDataSource
) : SearchContract.PresenterInterface {
    private val TAG = "SearchPresenter"

    private val compositeDisposable = CompositeDisposable()

    //1
    val searchResultsObservable: (String) -> Observable<TmdbResponse> = { query -> dataSource.searchResultsObservable(query) }

    //2
    val observer: DisposableObserver<TmdbResponse>
        get() = object : DisposableObserver<TmdbResponse>() {

            override fun onNext(@NonNull tmdbResponse: TmdbResponse) {
                Log.d(TAG, "OnNext" + tmdbResponse.totalResults)
                viewInterface.displayResult(tmdbResponse)
            }

            override fun onError(@NonNull e: Throwable) {
                Log.d(TAG, "Error fetching movie data.", e)
                viewInterface.displayError("Error fetching movie data.")
            }

            override fun onComplete() {
                Log.d(TAG, "Completed")
            }
        }

    //3
    override fun getSearchResults(query: String) {
        val searchResultsDisposable = searchResultsObservable(query)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(observer)
        compositeDisposable.add(searchResultsDisposable)
    }
    override fun stop() {
        compositeDisposable.clear()
    }
}