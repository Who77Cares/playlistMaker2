package com.bignerdranch.playlistmaker.unsorted

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bignerdranch.playlistmaker.Creator
import com.bignerdranch.playlistmaker.R
import com.bignerdranch.playlistmaker.search.App
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView

class SettingsActivity : AppCompatActivity() {




    private lateinit var buttonArrowBack: ImageView
    private lateinit var switch: SwitchMaterial
    private lateinit var share: MaterialTextView
    private lateinit var sendToSupport: MaterialTextView
    private lateinit var userAgreement: MaterialTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)




        buttonArrowBack = findViewById(R.id.arrow_back)
        switch = findViewById(R.id.switch_compat)
        share = findViewById(R.id.share)
        sendToSupport = findViewById(R.id.support)
        userAgreement = findViewById(R.id.user_agreement)


        // Синхронизация переключателя с текущей темой
        val app = applicationContext as App
        switch.isChecked = app.darkTheme

        buttonArrowBack.setOnClickListener {
            val intent = Intent(this@SettingsActivity, MainActivity::class.java)
            startActivity(intent)
        }


        share.setOnClickListener {
            val url = getString(R.string.share_url)

            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, url)  // передаем ссылку на страницу Google
                type = "text/plain"  // Указываем тип данных - обычный текст
            }

            // Проверяем, может ли устройство обработать этот интент
            val chooserIntent = Intent.createChooser(intent, "Поделиться через")
            startActivity(chooserIntent)
        }


        sendToSupport.setOnClickListener {
            val email = getString(R.string.sendToSupport_email)
            val theme = getString(R.string.sendToSupport_theme)
            val text = getString(R.string.sendToSupport_text)

            val intent = Intent().apply {
                action = Intent.ACTION_SENDTO
                data = Uri.parse("mailto:$email")

                putExtra(Intent.EXTRA_SUBJECT, theme)
                putExtra(Intent.EXTRA_TEXT, text)
            }

            val chooserIntent = Intent.createChooser(intent, "Отправить по почте:")
            startActivity(chooserIntent)
        }


        userAgreement.setOnClickListener {
            val url = getString(R.string.userAgreement_url)
            val intent = Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse(url)
            }

            val chooserIntent = Intent.createChooser(intent, "Выберите бразуер:")
            startActivity(chooserIntent)
        }

        switch.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)
        }

    }
}