package com.example.myshoppingadminapp.presentation.navigation

import kotlinx.serialization.Serializable

sealed class Routes {
    @Serializable
    object AddBannerScreen : Routes()

    @Serializable
    object AddCategoryScreen : Routes()

    @Serializable
    object AddProductScreen : Routes()

    @Serializable
    object AppDetailScreen : Routes()
}