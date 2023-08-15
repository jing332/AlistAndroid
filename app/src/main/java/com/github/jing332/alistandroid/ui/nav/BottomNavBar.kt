package com.github.jing332.alistandroid.ui.nav

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavBar(navController: NavHostController) {
    val backStackEntry = navController.currentBackStackEntryAsState()
    NavigationBar {
        for (route in BottomNavRoute.routes) {
            val isSelected = backStackEntry.value?.destination?.route == route.id
            NavigationBarItem(
                icon = route.icon,
                label = { Text(stringResource(route.strId)) },
                selected = isSelected,
                onClick = {
                    navController.navigate(route.id) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}