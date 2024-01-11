package com.github.jing332.alistandroid.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.github.jing332.alistandroid.config.AppConfig
import com.github.jing332.alistandroid.ui.nav.alist.AListScreen
import com.github.jing332.alistandroid.ui.nav.config.AListConfigScreen
import com.github.jing332.alistandroid.ui.nav.provider.AListProviderScreen
import com.github.jing332.alistandroid.ui.nav.settings.SettingsScreen
import com.github.jing332.alistandroid.ui.nav.web.WebScreen

@Composable
fun NavigationGraph(navController: NavHostController, modifier: Modifier) {
    NavHost(
        navController,
        startDestination = if (AppConfig.autoOpenWebPage.value) BottomNavRoute.Web.id else BottomNavRoute.AList.id,
        modifier = modifier
    ) {
        composable(BottomNavRoute.AListConfig.id) {
            AListConfigScreen()
        }

        composable(BottomNavRoute.AList.id) {
            AListScreen()
        }

        composable(BottomNavRoute.Settings.id) {
            SettingsScreen()
        }

        composable(BottomNavRoute.Web.id) {
            WebScreen()
        }

        composable(BottomNavRoute.AListProvider.id) {
            AListProviderScreen()
        }

    }
}