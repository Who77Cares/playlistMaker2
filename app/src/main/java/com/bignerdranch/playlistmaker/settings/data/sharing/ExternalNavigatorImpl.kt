package com.bignerdranch.playlistmaker.settings.data.sharing

import android.content.Context
import android.content.Intent
import android.net.Uri

class ExternalNavigatorImpl(
    private val context: Context
): ExternalNavigator {
    override fun shareLink(url: String) {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, url)
            type = "text/plain"
        }
        val chooserIntent = Intent.createChooser(intent, "Поделиться через")
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooserIntent)
    }

    override fun openEmail(email: String, subject: String, body: String) {
        val intent = Intent().apply {
            action = Intent.ACTION_SENDTO
            data = Uri.parse("mailto:$email")
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
        val chooserIntent = Intent.createChooser(intent, "Отправить по почте:")
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooserIntent)
    }

    override fun openLink(url: String) {
        val intent = Intent().apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse(url)
        }
        val chooserIntent = Intent.createChooser(intent, "Выберите браузер:")
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooserIntent)
    }
}