package com.thezayin.datemate.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.thezayin.presentation.GalleryScreen
import com.thezayin.rephoto.navigation.GalleryScreenNav

@Composable
fun NavHost(navController: NavHostController) {
    androidx.navigation.compose.NavHost(
        navController = navController,
        startDestination = GalleryScreenNav
    ) {
//        composable<SplashScreenNav> {
//            SplashScreen(
//                onNavigate = {
//                    navController.navigate(HomeScreenNav)
//                }
//            )
//        }

        composable<GalleryScreenNav> {
            GalleryScreen()
        }

    }
}