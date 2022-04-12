package com.smart.movieslist.ui.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper.getMainLooper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.navigation.fragment.findNavController
import com.smart.movieslist.databinding.SplashFragmentBinding


class SplashFragment : Fragment() {


    private lateinit var binding: SplashFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = SplashFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val springAnim =
            SpringAnimation(binding.appLogo, DynamicAnimation.TRANSLATION_X, 0f).apply {
                this.spring.dampingRatio = 0.4f
                this.spring.stiffness = 200f
            }
        springAnim.addEndListener { _, _, _, _ ->
            binding.appName.animate().alpha(1F).setDuration(800).withEndAction {
            }
                .setInterpolator(LinearInterpolator()).start()
        }


        Handler(getMainLooper()).postDelayed({
            binding.appLogo.animate().alpha(1F).setDuration(250)
                .setInterpolator(LinearInterpolator())
                .start()
            springAnim.start()
        }, 100)

        Handler(getMainLooper()).postDelayed({
            findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToHomeFragment())
        }, 2500)

    }

}