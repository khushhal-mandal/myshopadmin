package com.example.myshoppingadminapp.domain.repo

import android.net.Uri
import com.example.myshoppingadminapp.common.state.ResultState
import com.example.myshoppingadminapp.domain.model.AnalyticsResult
import com.example.myshoppingadminapp.domain.model.Banner
import com.example.myshoppingadminapp.domain.model.Category
import com.example.myshoppingadminapp.domain.model.Order
import com.example.myshoppingadminapp.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface Repo {
    fun addCategory(category: Category): Flow<ResultState<String>>
    fun getCategories(): Flow<ResultState<List<Category>>>
    fun addProduct(product: Product): Flow<ResultState<String>>
    fun uploadImage(imageUri: Uri, location: String): Flow<ResultState<String>>
    fun addBanner(banner: Banner): Flow<ResultState<String>>
    suspend fun getAllOrders(): List<Order>
    suspend fun getAnalytics(): AnalyticsResult
}