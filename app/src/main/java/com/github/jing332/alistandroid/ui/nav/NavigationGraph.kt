package com.github.jing332.alistandroid.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.github.jing332.alistandroid.ui.nav.alist.AListScreen
import com.github.jing332.alistandroid.ui.nav.config.AListConfigScreen
import com.github.jing332.alistandroid.ui.nav.provider.AListProviderScreen
import com.github.jing332.alistandroid.ui.nav.settings.SettingsScreen

@Composable
fun NavigationGraph(navController: NavHostController, modifier: Modifier) {
    NavHost(navController, startDestination = BottomNavRoute.AList.id, modifier = modifier) {
        composable(BottomNavRoute.AListConfig.id) {
            AListConfigScreen()
        }

        composable(BottomNavRoute.AList.id) {
            AListScreen()
        }

        composable(BottomNavRoute.Settings.id) {
            SettingsScreen()
        }

        composable(BottomNavRoute.AListProvider.id) {
            AListProviderScreen()
        }

    }
}