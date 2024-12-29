package com.thezayin.start_up.onboarding.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import com.thezayin.start_up.onboarding.model.OnboardingPage
import com.thezayin.values.R

@Composable
fun OnboardingContent(
    currentPage: Int,
    onboardPages: List<OnboardingPage>,
    onNextClicked: () -> Unit
) {
    Scaffold(
        modifier = Modifier.navigationBarsPadding(),
        containerColor = colorResource(R.color.black),
        bottomBar = {
            OnBoardDetails(
                currentPage = onboardPages[currentPage],
                onNextClicked = onNextClicked
            )
        },
    ) { _ ->
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OnBoardImageView(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding(),
                afterImageRes = onboardPages[currentPage].afterImageRes,
                beforeImageRes = onboardPages[currentPage].beforeImageRes,
                triggerAnimationKey = currentPage
            )
        }
    }
}
