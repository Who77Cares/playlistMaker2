package com.bignerdranch.playlistmaker.settings.ui.ui

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bignerdranch.playlistmaker.R
import com.bignerdranch.playlistmaker.App
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView

import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var buttonArrowBack: ImageView
    private lateinit var switch: SwitchMaterial
    private lateinit var share: MaterialTextView
    private lateinit var sendToSupport: MaterialTextView
    private lateinit var userAgreement: MaterialTextView

    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        buttonArrowBack = findViewById(R.id.arrow_back)
        switch = findViewById(R.id.switch_compat)
        share = findViewById(R.id.share)
        sendToSupport = findViewById(R.id.support)
        userAgreement = findViewById(R.id.user_agreement)

        viewModel.observeTheme().observe(this) { settings ->
            switch.isChecked = settings.isDarkTheme
        }

        buttonArrowBack.setOnClickListener {
            finish()
        }

        share.setOnClickListener {
            viewModel.shareApp()
        }

        sendToSupport.setOnClickListener {
            viewModel.openSupport()
        }

        userAgreement.setOnClickListener {
            viewModel.openTerms()
        }

        switch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateTheme(isChecked)
            (applicationContext as App).switchTheme(isChecked)
        }
    }
}