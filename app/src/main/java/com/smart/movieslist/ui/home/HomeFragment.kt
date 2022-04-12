package com.smart.movieslist.ui.home

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.smart.movieslist.data.model.MovieModel
import com.smart.movieslist.databinding.HomeFragmentBinding
import com.smart.movieslist.utils.Injector
import com.smart.movieslist.utils.getErrorType
import com.smart.movieslist.utils.getMessage
import com.smart.movieslist.utils.showLongToast
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class HomeFragment : Fragment(),MoviesAdapter.MoviesAapterCallback {

    private lateinit var binding: HomeFragmentBinding
    lateinit var viewModel: HomeViewModel


    private val moviesAdapter = MoviesAdapter().also { it.moviesAdapterCallback = this }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = HomeFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, Injector.provideViewModelFactory(context= requireContext(),owner = this))
            .get(HomeViewModel::class.java)

        // add dividers between RecyclerView's row items
        val decoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        binding.moviesRv.addItemDecoration(decoration)



        // bind the state
        binding.bindState(
            uiState = viewModel.uiState,
            pagingData = viewModel.pagingDataFlow,
            uiActions = viewModel.uiActions
        )

    }



    override fun onItemClickListener(model: MovieModel, position: Int) {

        findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToMovieDetailsFragment(movieId = model.id)
        )
    }

    override fun onFavoriteIconClick(model: MovieModel, position: Int) {
        if(model.isFav) {
            model.isFav = false
            viewModel.removeMovieFromFavorite(model)
        }
        else {
            model.isFav = true
            viewModel.addMovieToFavorite(model)
        }
        moviesAdapter.notifyItemChanged(position,model)
    }

    private fun HomeFragmentBinding.bindState(
        uiState: StateFlow<UiState>,
        pagingData: Flow<PagingData<MovieModel>>,
        uiActions: (UiAction) -> Unit
    ) {
        moviesRv.adapter = moviesAdapter.withLoadStateHeaderAndFooter(
            header = MoviesLoadStateAdapter { moviesAdapter.retry() },
            footer = MoviesLoadStateAdapter { moviesAdapter.retry() }
        )
        retryButton.setOnClickListener { moviesAdapter.retry() }
        bindSearch(
            uiState = uiState,
            onQueryChanged = uiActions
        )
        bindList(
            movieAdapter = moviesAdapter,
            uiState = uiState,
            pagingData = pagingData,
            onScrollChanged = uiActions
        )
    }

    private fun HomeFragmentBinding.bindSearch(
        uiState: StateFlow<UiState>,
        onQueryChanged: (UiAction.Search) -> Unit
    ) {
        searchET.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                updateMovieListFromInput(onQueryChanged)
                true
            } else {
                false
            }
        }
        searchET.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updateMovieListFromInput(onQueryChanged)
                true
            } else {
                false
            }
        }

        lifecycleScope.launch {
            uiState
                .map { it.query }
                .distinctUntilChanged()
                .collect(searchET::setText)
        }
    }

    private fun HomeFragmentBinding.updateMovieListFromInput(onQueryChanged: (UiAction.Search) -> Unit) {
        searchET.text.trim().let {
                moviesRv.scrollToPosition(0)
                onQueryChanged(UiAction.Search(query = it.toString()))
        }
    }

    private fun HomeFragmentBinding.bindList(
        movieAdapter: MoviesAdapter,
        uiState: StateFlow<UiState>,
        pagingData: Flow<PagingData<MovieModel>>,
        onScrollChanged: (UiAction.Scroll) -> Unit
    ) {
        moviesRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy != 0) onScrollChanged(UiAction.Scroll(currentQuery = uiState.value.query))
            }
        })
        val notLoading = movieAdapter.loadStateFlow
            // Only emit when REFRESH LoadState for RemoteMediator changes.
            .distinctUntilChangedBy { it.source.refresh }
            // Only react to cases where Remote REFRESH completes i.e., NotLoading.
            .map { it.source.refresh is LoadState.NotLoading }

        val hasNotScrolledForCurrentSearch = uiState
            .map { it.hasNotScrolledForCurrentSearch }
            .distinctUntilChanged()

        val shouldScrollToTop = combine(
            notLoading,
            hasNotScrolledForCurrentSearch,
            Boolean::and
        )
            .distinctUntilChanged()

        lifecycleScope.launch {
            pagingData.collectLatest(movieAdapter::submitData)
        }

        lifecycleScope.launch {
            shouldScrollToTop.collect { shouldScroll ->
                if (shouldScroll) moviesRv.scrollToPosition(0)
            }
        }

        lifecycleScope.launch {
            movieAdapter.loadStateFlow.collect { loadState ->
                val isListEmpty = loadState.refresh is LoadState.NotLoading && movieAdapter.itemCount == 0
                // show empty list
                emptyList.isVisible = isListEmpty
                // Only show the list if refresh succeeds.
                moviesRv.isVisible = !isListEmpty
                // Show loading spinner during initial load or refresh.
                loadingProgress.isVisible = loadState.source.refresh is LoadState.Loading
                // Show the retry state if initial load or refresh fails.
                if(loadState.source.refresh is LoadState.Error){
                    retryButton.isVisible = true
                    errorMsg.isVisible = true
                    errorMsg.text = (loadState.source.refresh as LoadState.Error).error
                        .getErrorType()
                        .getMessage(requireContext())
                }else{
                    retryButton.isVisible = false
                    errorMsg.isVisible = false
                }
                // Toast on any error, regardless of whether it came from RemoteMediator or PagingSource
                val errorState = loadState.source.append as? LoadState.Error
                    ?: loadState.source.prepend as? LoadState.Error
                    ?: loadState.append as? LoadState.Error
                    ?: loadState.prepend as? LoadState.Error
                errorState?.let {
                    requireContext().showLongToast("\uD83D\uDE28 Wooops ${it.error
                        .getErrorType()
                        .getMessage(requireContext())}")
                }
            }
        }
    }
}