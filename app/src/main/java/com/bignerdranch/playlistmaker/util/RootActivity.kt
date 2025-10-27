package com.bignerdranch.playlistmaker.util

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.bignerdranch.playlistmaker.R
import com.bignerdranch.playlistmaker.databinding.ActivityRootBinding


class RootActivity: AppCompatActivity() {

    private lateinit var binding: ActivityRootBinding
    private var isKeyboardOpen = false
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRootBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.rootFragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController // ← ИСПОЛЬЗУЙ ЭТУ ПЕРЕМЕННУЮ
        binding.bottomNavigationView.setupWithNavController(navController)

        setupKeyboardListener()

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (isKeyboardOpen) {
                binding.bottomNavigationView.visibility = View.GONE
                binding.line.visibility = View.GONE
            } else {
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

    private fun setupKeyboardListener() {
        binding.root.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            binding.root.getWindowVisibleDisplayFrame(rect)
            val screenHeight = binding.root.rootView.height
            val keypadHeight = screenHeight - rect.bottom

            val keyboardNowOpen = keypadHeight > screenHeight * 0.15

            if (keyboardNowOpen != isKeyboardOpen) {
                isKeyboardOpen = keyboardNowOpen

                if (isKeyboardOpen) {
                    binding.bottomNavigationView.visibility = View.GONE
                    binding.line.visibility = View.GONE
                } else {
                    // Теперь navController доступен
                    val currentDestination = navController.currentDestination?.id
                    when (currentDestination) {
                        in setOf(R.id.audioPlayerFragment, R.id.newPlaylistFragment) -> {
                            // Оставляем скрытым
                        }
                        else -> {
                            binding.bottomNavigationView.visibility = View.VISIBLE
                            binding.line.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }


}