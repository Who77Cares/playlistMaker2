package com.bignerdranch.playlistmaker.util

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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


        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())


            // Скрываем bottom navigation когда клавиатура открыта
            binding.bottomNavigationView.visibility =
                if (imeVisible) View.GONE else View.VISIBLE

            // Также можно скрыть линию
            binding.line.visibility =
                if (imeVisible) View.GONE else View.VISIBLE

            insets
        }

// Навигационный слушатель остается без изменений
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