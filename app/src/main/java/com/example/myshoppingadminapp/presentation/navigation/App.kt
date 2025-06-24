package com.example.myshoppingadminapp.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Rectangle
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Rectangle
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.myshoppingadminapp.presentation.screen.AddBannerScreenUI
import com.example.myshoppingadminapp.presentation.screen.AddCategoryScreenUI
import com.example.myshoppingadminapp.presentation.screen.AddProductScreenUI
import com.example.myshoppingadminapp.presentation.screen.AppDetailScreenUI

@Composable
fun App() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavItems = listOf(
        BottomNavItem(
            route = Routes.AppDetailScreen::class.qualifiedName!!,
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home
        ),
        BottomNavItem(
            route = Routes.AddCategoryScreen::class.qualifiedName!!,
            selectedIcon = Icons.Filled.Category,
            unselectedIcon = Icons.Outlined.Category
        ),
        BottomNavItem(
            route = Routes.AddProductScreen::class.qualifiedName!!,
            selectedIcon = Icons.Filled.AddBox,
            unselectedIcon = Icons.Outlined.Add
        ),
        BottomNavItem(
            route = Routes.AddBannerScreen::class.qualifiedName!!,
            selectedIcon = Icons.Filled.Rectangle,
            unselectedIcon = Icons.Outlined.Rectangle
        )
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { item ->
                    val isSelected = item.route == currentRoute
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            if (!isSelected) {
                                navController.navigate(item.route)
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = null,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    )
                }
            }
        }

    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavHost(navController = navController, startDestination = Routes.AppDetailScreen) {
                composable<Routes.AppDetailScreen> {
                    AppDetailScreenUI(navController = navController)
                }

                composable<Routes.AddBannerScreen> {
                    AddBannerScreenUI(navController = navController)
                }

                composable<Routes.AddCategoryScreen> {
                    AddCategoryScreenUI(navController = navController)
                }

                composable<Routes.AddProductScreen> {
                    AddProductScreenUI(navController = navController)
                }
            }
        }
    }


}

data class BottomNavItem(
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)