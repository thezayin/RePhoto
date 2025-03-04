package com.thezayin.rephoto.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.thezayin.background_blur.BackgroundBlurScreen
import com.thezayin.background_changer.BackgroundChangerScreen
import com.thezayin.background_remover.BackgroundRemoverScreen
import com.thezayin.editor.cropMode.CropperScreen
import com.thezayin.editor.drawMode.DrawModeScreen
import com.thezayin.editor.editor.presentation.EditorScreen
import com.thezayin.editor.effectsMode.EffectsModeScreen
import com.thezayin.editor.textMode.TextModeScreen
import com.thezayin.editor.utils.other.bitmap.ImmutableBitmap
import com.thezayin.enhance.presentation.EnhanceScreen
import com.thezayin.framework.session.SessionManager
import com.thezayin.presentation.GalleryScreen
import com.thezayin.start_up.onboarding.OnboardingScreen
import com.thezayin.start_up.splash.SplashScreen

@Composable
fun AppNavHost(navController: NavHostController, sessionManager: SessionManager) {
    NavHost(
        navController = navController,
        startDestination = SplashScreenNav
    ) {
        // Splash Screen
        composable<SplashScreenNav> {
            SplashScreen(
                navigateToHome = {
                    navController.navigate(GalleryScreenNav) {
                        popUpTo(SplashScreenNav) { inclusive = true }
                    }
                },
                navigateToOnboarding = {
                    navController.navigate(OnBoardingScreenNav) {
                        popUpTo(SplashScreenNav) { inclusive = true }
                    }
                }
            )
        }

        // Background Blur Screen
        composable<BackgroundBlurScreenNav> {
            BackgroundBlurScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // Gallery Screen
        composable<GalleryScreenNav> {
            GalleryScreen(
                onNavigateToNextScreen = {
                    navController.navigate(EditorScreenNav)
                },
            )
        }

        // Background Changer Screen
        composable<BackgroundChangerScreenNav> {
            BackgroundChangerScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onApplyClick = {
                    // TODO: Handle apply action, possibly update SessionManager
                }
            )
        }

        // Background Remover Screen
        composable<BackgroundRemoverScreenNav> {
            BackgroundRemoverScreen(
                onBack = {
                    navController.popBackStack()
                },
            )
        }

        // Enhance Screen
        composable<EnhanceScreenNav> {
            EnhanceScreen(
                // TODO: Add necessary parameters or callbacks
            )
        }

        // OnBoarding Screen
        composable<OnBoardingScreenNav> {
            OnboardingScreen(
                navigateToHome = {
                    navController.navigate(GalleryScreenNav) {
                        popUpTo(OnBoardingScreenNav) { inclusive = true }
                    }
                }
            )
        }

        // **Integrated Module's Navigation Destinations**

        // Editor Screen
        composable<EditorScreenNav> {
            EditorScreen(
                goToCropModeScreen = {
                    navController.navigate(CropperScreenNav)
                },
                goToDrawModeScreen = {
                    navController.navigate(DrawModeScreenNav)
                },
                goToTextModeScreen = {
                    navController.navigate(TextScreenNav)
                },
                goToEffectsModeScreen = {
                    navController.navigate(EffectsModeScreenNav)
                },
                goToMainScreen = {
                    navController.popBackStack()
                }
            )
        }

        // Cropper Screen
        composable<CropperScreenNav> {
            CropperScreen(
                onBackPressed = {
                    navController.popBackStack()
                },
            )
        }

        // Draw Mode Screen
        composable<DrawModeScreenNav> {
            DrawModeScreen(
                onBackPressed = { navController.popBackStack() },
            )
        }

        // Text Mode Screen
        composable<TextScreenNav> {
            TextModeScreen(
                immutableBitmap = ImmutableBitmap(
                    bitmap = TODO()/* Provide Bitmap Here */
                ),
                onBackPressed = {
                    navController.popBackStack()
                },
                onDoneClicked = { bitmap ->
                    // Handle the edited bitmap
                    navController.navigate(
                        EditorScreenNav,
                        navOptions = NavOptions.Builder()
                            .setPopUpTo(EditorScreenNav, inclusive = true).build()
                    )
                },
            )
        }

        // Effects Mode Screen
        composable<EffectsModeScreenNav> {
            EffectsModeScreen(
                immutableBitmap = ImmutableBitmap(
                    bitmap = TODO()/* Provide Bitmap Here */
                ),
                onBackPressed = {
                    navController.popBackStack()
                },
                onDoneClicked = { bitmap ->
                    // Handle the edited bitmap
                    navController.navigate(
                        EditorScreenNav,
                        navOptions = NavOptions.Builder()
                            .setPopUpTo(EditorScreenNav, inclusive = true).build()
                    )
                },
            )
        }
    }
}
