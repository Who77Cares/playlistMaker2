package com.bignerdranch.playlistmaker.settings.ui.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource

import com.bignerdranch.playlistmaker.R

import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun SettingsCompose(
    viewModel: SettingsViewModel,
    onShareClicked: () -> Unit,
    onSupportClicked: () -> Unit,
    onTermsClicked: () -> Unit
) {
    // Просто наблюдаем за состоянием темы
    val isDarkTheme by viewModel.observeTheme().observeAsState(initial = false)

  Column(
      modifier = Modifier
          .fillMaxSize()
          .background(colorResource(id = R.color.colors_FFFFFFFF_1A1B22))
          .padding(
              top = dimensionResource(id = R.dimen.dimen14),
              start = dimensionResource(id = R.dimen.dimen16),
              end = dimensionResource(id = R.dimen.dimen16)
          )
  ) {
      Text(
          text = stringResource(id = R.string.settings),
          color = colorResource(id = R.color.colors_FF000000_FFFFFFFF),
          fontSize = dimensionResource(id = R.dimen.sp22).value.sp,
          fontFamily = FontFamily(Font(R.font.ys_display_medium)),
          fontWeight = FontWeight.Normal,
          modifier = Modifier
              .padding(bottom = dimensionResource(id = R.dimen.padding_for_settings))
      )



      // Переключатель темы
      SwitchSettingsItem(
          text = stringResource(id = R.string.dark_theme),
          isChecked = isDarkTheme,
          onCheckedChange = { checked ->
              viewModel.toggleTheme(checked)
          }
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Поделиться приложением
      SimpleSettingsItem(
          text = stringResource(id = R.string.share_with_app),
          icon = ImageVector.vectorResource(id = R.drawable.share),
          onClick = onShareClicked
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Поддержка
      SimpleSettingsItem(
          text = stringResource(id = R.string.support),
          icon = ImageVector.vectorResource(id = R.drawable.support),
          onClick = onSupportClicked
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Пользовательское соглашение
      SimpleSettingsItem(
          text = stringResource(id = R.string.user_agreement),
          icon = ImageVector.vectorResource(id = R.drawable.arrow_forward),
          onClick = onTermsClicked
      )
  }
}



@Composable
private fun SwitchSettingsItem(
    text: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = text,
            color = colorResource(id = R.color.colors_FF000000_FFFFFFFF),
            fontSize = 16.sp,
            fontFamily = FontFamily(Font(R.font.ys_display_regular)),
            fontWeight = FontWeight.Normal
        )

        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = colorResource(id = R.color.thumb_on),
                checkedTrackColor = colorResource(id = R.color.track_on),


                // Для ВЫКЛЮЧЕННОГО состояния
                uncheckedThumbColor = colorResource(id = R.color.thumb_off),
                uncheckedTrackColor = colorResource(id = R.color.track_off),
            )
        )
    }
}

@Composable
private fun SimpleSettingsItem(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = text,
            color = colorResource(id = R.color.colors_FF000000_FFFFFFFF),
            fontSize = 16.sp,
            fontFamily = FontFamily(Font(R.font.ys_display_regular)),
            fontWeight = FontWeight.Normal
        )

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colorResource(id = R.color.color_AEAFB4_FFFFFFFF)
        )
    }

}


