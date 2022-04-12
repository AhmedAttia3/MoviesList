
package com.smart.movieslist.utils

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import com.smart.movieslist.data.storage.remote.ApiCall
import com.smart.movieslist.data.storage.local.db.AppLocalDB
import com.smart.movieslist.data.storage.remote.ApiService
import com.smart.movieslist.repository.MoviesRepository

/**
 * Class that handles object creation.
 * Like this, objects can be passed as parameters in the constructors and then replaced for
 * testing, where needed.
 */
object Injector {

    /**
     * Creates an instance of [MoviesRepository] based on the [ApiService] and a
     * [AppLocalDB]
     */
    private fun provideMoviesRepository(context: Context): MoviesRepository {
        return MoviesRepository(ApiCall(), AppLocalDB.invoke(context).appDB())
    }

    /**
     * Provides the [ViewModelProvider.Factory] that is then used to get a reference to
     * [ViewModel] objects.
     */
    fun provideViewModelFactory(context: Context,owner: SavedStateRegistryOwner): ViewModelProvider.Factory {
        return ViewModelFactory(owner, provideMoviesRepository(context))
    }
}
