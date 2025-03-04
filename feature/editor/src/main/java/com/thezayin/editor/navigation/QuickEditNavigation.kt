//package com.abizer_r.quickedit.ui.navigation
//
//import android.graphics.Bitmap
//import androidx.compose.animation.EnterTransition
//import androidx.compose.animation.ExitTransition
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.remember
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.lifecycle.compose.LocalLifecycleOwner
//import androidx.lifecycle.lifecycleScope
//import androidx.navigation.NavOptions
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
//import com.thezayin.editor.SharedEditorViewModel
//import com.thezayin.editor.cropMode.CropperScreen
//import com.thezayin.editor.drawMode.DrawModeScreen
//import com.thezayin.editor.editorScreen.presentation.EditorScreen
//import com.thezayin.editor.editorScreen.presentation.state.EditorScreenState
//import com.thezayin.editor.effectsMode.EffectsModeScreen
//import com.thezayin.editor.mainScreen.MainScreen
//import com.thezayin.editor.navigation.NavDestinations
//import com.thezayin.editor.textMode.TextModeScreen
//import com.thezayin.editor.utils.other.anim.enterTransition
//import com.thezayin.editor.utils.other.anim.exitTransition
//import com.thezayin.editor.utils.other.anim.popEnterTransition
//import com.thezayin.editor.utils.other.anim.popExitTransition
//import com.thezayin.editor.utils.other.bitmap.ImmutableBitmap
//
//@Composable
//fun QuickEditNavigation() {
//    val lifeCycleOwner = LocalLifecycleOwner.current
//    val lifecycleScope = lifeCycleOwner.lifecycleScope
//
//    val sharedEditorViewModel = hiltViewModel<SharedEditorViewModel>()
//    val navController = rememberNavController()
//
//    val onImageSelected = remember<(Bitmap) -> Unit> {{ bitmap ->
//        sharedEditorViewModel.addBitmapToStack(
//            bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, false),
//            triggerRecomposition = false
//        )
//        sharedEditorViewModel.useTransition = true
//        navController.navigate(NavDestinations.EDITOR_SCREEN)
//    }}
//
//    val goToCropModeScreenLambda = remember<(EditorScreenState) -> Unit> {{ finalEditorState ->
//        sharedEditorViewModel.updateStacksFromEditorState(finalEditorState)
//        navController.navigate(NavDestinations.CROPPER_SCREEN)
//    }}
//    val goToDrawModeScreenLambda = remember<(EditorScreenState) -> Unit> {{ finalEditorState ->
//        sharedEditorViewModel.updateStacksFromEditorState(finalEditorState)
//        navController.navigate(NavDestinations.DRAW_MODE_SCREEN)
//    }}
//    val goToTextModeScreenLambda = remember<(EditorScreenState) -> Unit> {{ finalEditorState ->
//        sharedEditorViewModel.updateStacksFromEditorState(finalEditorState)
//        navController.navigate(NavDestinations.TEXT_MODE_SCREEN)
//    }}
//    val goToEffectsModeScreenLambda = remember<(EditorScreenState) -> Unit> {{ finalEditorState ->
//        sharedEditorViewModel.updateStacksFromEditorState(finalEditorState)
//        navController.navigate(NavDestinations.EFFECTS_MODE_SCREEN)
//    }}
//    val goToMainScreenLambda = remember<() -> Unit> {{
//        sharedEditorViewModel.resetStacks()
//        sharedEditorViewModel.useTransition = true
//        navController.navigateUp()
//    }}
//
//
//    val onBackPressedLambda = remember<() -> Unit> {{
//        navController.navigateUp()
//    }}
//    val onDoneClickedLambda = remember<(Bitmap) -> Unit> {{
//        sharedEditorViewModel.addBitmapToStack(
//            bitmap = it.copy(Bitmap.Config.ARGB_8888, false),
//        )
//        navController.navigate(
//            NavDestinations.EDITOR_SCREEN,
//            navOptions = NavOptions.Builder()
//                .setPopUpTo(route = NavDestinations.EDITOR_SCREEN, inclusive = true)
//                .build()
//        )
//    }}
//
//
//    NavHost(
//        navController = navController,
//        startDestination = NavDestinations.MAIN_SCREEN,
//        enterTransition = { EnterTransition.None },
//        exitTransition = { ExitTransition.None }
//    ) {
//
//        composable(
//            route = NavDestinations.MAIN_SCREEN,
//            enterTransition = { enterTransition() },
//            exitTransition = { exitTransition() },
//            popEnterTransition = { popEnterTransition() },
//            popExitTransition = { popExitTransition() }
//        ) {
//            MainScreen(onImageSelected = onImageSelected)
//        }
//
//        composable(
//            route = NavDestinations.EDITOR_SCREEN,
//            enterTransition = {
//                if (sharedEditorViewModel.useTransition) enterTransition()
//                else EnterTransition.None
//            },
//            popEnterTransition = {
//                if (sharedEditorViewModel.useTransition) popEnterTransition()
//                else EnterTransition.None
//            },
//            exitTransition = {
//                if (sharedEditorViewModel.useTransition) exitTransition()
//                else ExitTransition.None
//            },
//            popExitTransition = {
//                if (sharedEditorViewModel.useTransition) popExitTransition()
//                else ExitTransition.None
//            }
//        ) {
//            sharedEditorViewModel.useTransition = false
//            val initialEditorState = EditorScreenState(
//                sharedEditorViewModel.bitmapStack, sharedEditorViewModel.bitmapRedoStack
//            )
//
//            EditorScreen(
//                initialEditorScreenState = initialEditorState,
//                goToCropModeScreen = goToCropModeScreenLambda,
//                goToDrawModeScreen = goToDrawModeScreenLambda,
//                goToTextModeScreen = goToTextModeScreenLambda,
//                goToEffectsModeScreen = goToEffectsModeScreenLambda,
//                goToMainScreen = goToMainScreenLambda
//            )
//
//        }
//
//        composable(route = NavDestinations.CROPPER_SCREEN) { entry ->
//            CropperScreen(
//                immutableBitmap = ImmutableBitmap((sharedEditorViewModel.getCurrentBitmap())),
//                onBackPressed = onBackPressedLambda,
//                onDoneClicked = onDoneClickedLambda,
//            )
//        }
//
//        composable(route = NavDestinations.DRAW_MODE_SCREEN) { entry ->
//            DrawModeScreen(
//                immutableBitmap = ImmutableBitmap((sharedEditorViewModel.getCurrentBitmap())),
//                onBackPressed = onBackPressedLambda,
//                onDoneClicked = onDoneClickedLambda,
//            )
//        }
//
//        composable(route = NavDestinations.TEXT_MODE_SCREEN) { entry ->
//            TextModeScreen(
//                immutableBitmap = ImmutableBitmap(sharedEditorViewModel.getCurrentBitmap()),
//                onBackPressed = onBackPressedLambda,
//                onDoneClicked = onDoneClickedLambda,
//            )
//        }
//
//        composable(route = NavDestinations.EFFECTS_MODE_SCREEN) { entry ->
//            EffectsModeScreen(
//                immutableBitmap = ImmutableBitmap(sharedEditorViewModel.getCurrentBitmap()),
//                onBackPressed = onBackPressedLambda,
//                onDoneClicked = onDoneClickedLambda,
//            )
//        }
//    }
//}