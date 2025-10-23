package com.bignerdranch.playlistmaker.util

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.bignerdranch.playlistmaker.R
import com.bignerdranch.playlistmaker.databinding.ActivityRootBinding


class RootActivity: AppCompatActivity() {

    private lateinit var binding: ActivityRootBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRootBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.rootFragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)






        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {

                in setOf(
                    R.id.audioPlayerFragment,
                    R.id.newPlaylistFragment
                ) -> {
                    binding.bottomNavigationView.visibility = View.GONE
                    binding.line.visibility = View.GONE
                }



                else -> {
                    binding.bottomNavigationView.visibility = View.VISIBLE
                    binding.line.visibility = View.VISIBLE
                }
            }
        }
    }
}