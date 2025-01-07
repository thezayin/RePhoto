package com.thezayin.rephoto.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.thezayin.background_blur.BackgroundBlurScreen
import com.thezayin.background_changer.BackgroundChangerScreen
import com.thezayin.background_remover.BackgroundRemoverScreen
import com.thezayin.enhance.presentation.EnhanceScreen
import com.thezayin.presentation.GalleryScreen
import com.thezayin.start_up.onboarding.OnboardingScreen
import com.thezayin.start_up.splash.SplashScreen

@Composable
fun NavHost(navController: NavHostController) {
    androidx.navigation.compose.NavHost(
        navController = navController,
        startDestination = SplashScreenNav
    ) {
        composable<SplashScreenNav> {
            SplashScreen(
                navigateToHome = {
                    navController.navigate(GalleryScreenNav)
                },
                navigateToOnboarding = {
                    navController.navigate(OnBoardingScreenNav)
                }
            )
        }

        composable<BackgroundBlurScreenNav> {
            BackgroundBlurScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<GalleryScreenNav> {
            GalleryScreen(
                onNavigateToNextScreen = {
                    navController.navigate(EnhanceScreenNav)
                },
            )
        }

        composable<BackgroundChangerScreenNav> {
            BackgroundChangerScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onApplyClick = {

                }
            )
        }

        composable<BackgroundRemoverScreenNav> {
            BackgroundRemoverScreen(
                onBack = {
                    navController.popBackStack()
                },
            )
        }

        composable<EnhanceScreenNav> {
            EnhanceScreen(

            )
        }

        composable<OnBoardingScreenNav> {
            OnboardingScreen(
                navigateToHome = {
                    navController.navigate(GalleryScreenNav)
                }
            )
        }
    }
}