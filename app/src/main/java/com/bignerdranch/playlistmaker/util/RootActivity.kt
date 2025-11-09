package com.bignerdranch.playlistmaker.util

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.bignerdranch.playlistmaker.R
import com.bignerdranch.playlistmaker.databinding.ActivityRootBinding
import com.bignerdranch.playlistmaker.settings.domain.api.ThemeInteractor
import org.koin.android.ext.android.inject
import kotlin.getValue


class RootActivity: AppCompatActivity() {

    private lateinit var binding: ActivityRootBinding
    private lateinit var navController: NavController

    private val themeInteractor: ThemeInteractor by inject()

    override fun onCreate(savedInstanceState: Bundle?) {

        themeInteractor.applyThemeToApp()

        super.onCreate(savedInstanceState)
        binding = ActivityRootBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.rootFragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)


        // скрываем bottomNavigationView если мы находимся на одном из трех экранов
        navController.addOnDestinationChangedListener { _, destination, _ ->
             when (destination.id) {
                    in setOf(
                        R.id.audioPlayerFragment,
                        R.id.newPlaylistFragment,
                        R.id.singlePlaylistFragment
                    ) -> {
                        binding.bottomNavigationView.visibility = View.GONE
                        binding.line.visibility = View.GONE
                    }
                    else -> {
                        binding.bottomNavigationView.visibility = View.VISIBLE
                        binding.line.visibility = View.VISIBLE
                    } } }
    }
}