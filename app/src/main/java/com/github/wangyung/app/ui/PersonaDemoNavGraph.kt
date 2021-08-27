package com.github.wangyung.app.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.wangyung.app.model.AnimationType
import com.github.wangyung.app.model.toAnimationType
import com.github.wangyung.app.ui.screen.animation.AnimationScreen
import com.github.wangyung.app.ui.screen.main.MainScreen

private const val KEY_ANIMATION_TYPE = "type"

// Inspired by https://github.com/chrisbanes/tivi
internal sealed class Screen(val route: String) {
    object Main : Screen("main")
    object AnimationDemo : Screen("demo/{$KEY_ANIMATION_TYPE}") {
        fun createTargetRoute(type: AnimationType): String = "demo/${type.value}"
    }
}

@Composable
fun PersonaDemoNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Main.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        addMain(navController)
        addAnimationDemo()
    }
}

private fun NavGraphBuilder.addMain(navController: NavHostController) {
    composable(route = Screen.Main.route) {
        MainScreen { animationType ->
            navController.navigate(
                Screen.AnimationDemo.createTargetRoute(animationType)
            )
        }
    }
}

private fun NavGraphBuilder.addAnimationDemo() {
    composable(
        route = Screen.AnimationDemo.route,
    ) { backStackEntry ->
        val animationType: AnimationType? =
            backStackEntry.arguments?.getString(KEY_ANIMATION_TYPE)?.toAnimationType()
        AnimationScreen(animationType)
    }
}
