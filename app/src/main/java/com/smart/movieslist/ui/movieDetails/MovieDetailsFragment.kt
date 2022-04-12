package com.smart.movieslist.ui.movieDetails

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.smart.movieslist.data.model.MovieDetailsResponse
import com.smart.movieslist.databinding.MovieDetailsFragmentBinding
import com.smart.movieslist.utils.*
import kotlinx.coroutines.flow.collect

class MovieDetailsFragment : Fragment() {

    private lateinit var viewModel: MovieDetailsViewModel
    private lateinit var binding: MovieDetailsFragmentBinding
    private val args: MovieDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MovieDetailsFragmentBinding.inflate(inflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(
            this,
            Injector.provideViewModelFactory(context = requireContext(), owner = this)
        )
            .get(MovieDetailsViewModel::class.java)
        viewModel.getMovieDetails(args.movieId)
        binding.onBackPressed = { findNavController().navigateUp() }

        lifecycleScope.launchWhenStarted {
            viewModel.state.collect { uiStates: UiStates ->
                binding.loadingProgress.isVisible = false
                when (uiStates) {
                    is UiStates.Error -> {
                        onError(uiStates)
                    }
                    UiStates.Loading -> {
                        binding.loadingProgress.isVisible = true
                    }
                    is UiStates.Success<*> -> {
                        val response = uiStates.data as MovieDetailsResponse
                        binding.movie = response
                    }
                }
            }
        }
    }

    private fun onError(uiStates: UiStates.Error) {
        when (uiStates.exception.getErrorType()) {
//            Do what you want in case of
            Constants.ErrorType.NETWORK -> {
//                No internet connection
            }
            Constants.ErrorType.TIMEOUT -> {
//                Request time out
            }
            Constants.ErrorType.UNKNOWN -> {}
        }
        Log.e("uiStatesError", uiStates.exception.toString())
        requireContext().showLongToast(
            "\uD83D\uDE28 Wooops ${
                uiStates.exception.getErrorType().getMessage(requireContext())
            }"
        )
    }
}