package com.bignerdranch.playlistmaker.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.appcompat.app.AppCompatActivity
import com.bignerdranch.playlistmaker.R
import com.bignerdranch.playlistmaker.databinding.ActivityMainBinding
import com.bignerdranch.playlistmaker.media.FavoriteMediaFragment
import com.bignerdranch.playlistmaker.media.MediaActivity
import com.bignerdranch.playlistmaker.search.ui.ui.SearchActivity
//import com.bignerdranch.playlistmaker.media.MediaActivity
import com.bignerdranch.playlistmaker.settings.ui.ui.SettingsActivity

class MainActivity : AppCompatActivity(), OnClickListener {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.searchButton.setOnClickListener(this)
        binding.mediaButton.setOnClickListener(this)
        binding.settingsButton.setOnClickListener(this)


    }

    override fun onClick(v: View) {
        val intent: Intent = when (v.id) {
            R.id.searchButton -> Intent(this@MainActivity, SearchActivity::class.java)
            R.id.mediaButton -> Intent(this@MainActivity, MediaActivity::class.java)
            R.id.settingsButton -> Intent(this@MainActivity, SettingsActivity::class.java)
            else -> Intent(this@MainActivity, SearchActivity::class.java)
        }
        startActivity(intent)
    }

}

