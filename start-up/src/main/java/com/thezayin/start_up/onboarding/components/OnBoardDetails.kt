package com.thezayin.start_up.onboarding.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import com.thezayin.start_up.onboarding.model.OnboardingPage
import com.thezayin.values.R
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

@Composable
fun OnBoardDetails(
    modifier: Modifier = Modifier, currentPage: OnboardingPage, onNextClicked: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.Black.copy(alpha = 1f),
                        Color.Black.copy(alpha = 1f),
                        Color.Black.copy(alpha = 1f),
                        Color.Black.copy(alpha = 1f)
                    )
                ),
            )
            .padding(12.sdp)
    ) {
        Text(
            text = currentPage.title,
            fontSize = 22.ssp,
            textAlign = TextAlign.Start,
            fontFamily = FontFamily(Font(R.font.gilroy_bold)),
            modifier = Modifier.fillMaxWidth(),
            color = colorResource(id = R.color.white)
        )
        Spacer(modifier = Modifier.height(10.sdp))
        Text(
            text = currentPage.subtitle,
            fontSize = 12.ssp,
            fontFamily = FontFamily(Font(R.font.gilroy_regular)),
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth(),
            color = colorResource(id = R.color.greyish)
        )
        OnBoardNavButton(
            modifier = Modifier.padding(top = 10.sdp, bottom = 10.sdp),
        ) {
            onNextClicked()
        }
    }
}