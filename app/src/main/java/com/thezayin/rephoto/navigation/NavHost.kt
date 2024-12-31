package com.thezayin.rephoto.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.thezayin.background_changer.BackgroundChangerScreen
import com.thezayin.background_remover.BackgroundRemoverScreen
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

        composable<GalleryScreenNav> {
            GalleryScreen(
                onNavigateToNextScreen = {
                    navController.navigate(BackgroundChangerScreenNav)
                },
            )
        }

        composable<BackgroundChangerScreenNav> {
            BackgroundChangerScreen(
//                onBack = {
//                    navController.popBackStack()
//                },
            )
        }

        composable<BackgroundRemoverScreenNav> {
            BackgroundRemoverScreen(
                onBack = {
                    navController.popBackStack()
                },
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