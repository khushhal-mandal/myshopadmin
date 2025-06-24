package com.example.myshoppingadminapp.data.repoimpl

import android.net.Uri
import com.example.myshoppingadminapp.common.BANNER
import com.example.myshoppingadminapp.common.CATEGORY
import com.example.myshoppingadminapp.common.PRODUCT
import com.example.myshoppingadminapp.common.state.ResultState
import com.example.myshoppingadminapp.domain.model.AnalyticsResult
import com.example.myshoppingadminapp.domain.model.Banner
import com.example.myshoppingadminapp.domain.model.Category
import com.example.myshoppingadminapp.domain.model.Order
import com.example.myshoppingadminapp.domain.model.Product
import com.example.myshoppingadminapp.domain.repo.Repo
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class RepoImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : Repo {

    override fun addCategory(category: Category): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        val docRef = firestore.collection(CATEGORY).document()
        val categoryWithId = category.copy(id = docRef.id)
        docRef.set(categoryWithId)
            .addOnSuccessListener { trySend(ResultState.Success("${category.name} added successfully")) }
            .addOnFailureListener { trySend(ResultState.Error(it.message.toString())) }
        awaitClose { close() }
    }

    override fun getCategories(): Flow<ResultState<List<Category>>> = callbackFlow {
        trySend(ResultState.Loading)
        firestore.collection(CATEGORY).get()
            .addOnSuccessListener { list ->
                val categories = list.mapNotNull { it.toObject(Category::class.java) }
                trySend(ResultState.Success(categories))
            }
            .addOnFailureListener { trySend(ResultState.Error(it.message.toString())) }
        awaitClose { close() }
    }

    override fun addProduct(product: Product): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        val docRef = firestore.collection(PRODUCT).document()
        val productWithId = product.copy(id = docRef.id)
        docRef.set(productWithId)
            .addOnSuccessListener { trySend(ResultState.Success("${product.name} added successfully")) }
            .addOnFailureListener { trySend(ResultState.Error(it.message.toString())) }
        awaitClose { close() }
    }

    override fun uploadImage(imageUri: Uri, location: String): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        val fileName = "${System.currentTimeMillis()}_${imageUri.lastPathSegment}"
        val ref = storage.reference.child("$location/$fileName")
        ref.putFile(imageUri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { uri ->
                    trySend(ResultState.Success(uri.toString()))
                }.addOnFailureListener { err ->
                    trySend(ResultState.Error(err.message ?: "Failed to get download URL"))
                }
            }
            .addOnFailureListener { err ->
                trySend(ResultState.Error(err.message ?: "Upload failed"))
            }
        awaitClose { close() }
    }

    override fun addBanner(banner: Banner): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        val docRef = firestore.collection(BANNER).document()
        val bannerWithId = banner.copy(id = docRef.id)
        docRef.set(bannerWithId)
            .addOnSuccessListener { trySend(ResultState.Success("${banner.title} added successfully")) }
            .addOnFailureListener { trySend(ResultState.Error(it.message.toString())) }
        awaitClose { close() }
    }

    override suspend fun getAllOrders(): List<Order> {
        val orders = mutableListOf<Order>()

        val ordersSnapshot = firestore.collectionGroup("ORDERS").get().await()
        for (orderDoc in ordersSnapshot.documents) {
            val order = orderDoc.toObject(Order::class.java)
            if (order != null) {
                orders.add(order)
            }
        }
        return orders
    }




    override suspend fun getAnalytics(): AnalyticsResult {
        val orders = getAllOrders()
        var totalRevenue = 0
        val quantityPerCategory = mutableMapOf<String, Int>()
        val revenuePerCategory = mutableMapOf<String, Int>()
        val quantityPerProduct = mutableMapOf<String, Int>()
        val revenuePerProduct = mutableMapOf<String, Int>()
        val ordersPerDay = mutableMapOf<String, Int>()
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        for (order in orders) {
            totalRevenue += order.totalPrice

            val date = formatter.format(Date(order.time))
            ordersPerDay[date] = ordersPerDay.getOrDefault(date, 0) + 1

            for (product in order.products) {
                val category = product.category
                val productName = product.productName
                val quantity = product.quantity.toIntOrNull() ?: 0
                val price = product.totalPrice.toIntOrNull() ?: 0

                // Category analytics
                quantityPerCategory[category] = quantityPerCategory.getOrDefault(category, 0) + quantity
                revenuePerCategory[category] = revenuePerCategory.getOrDefault(category, 0) + price

                // Product analytics
                quantityPerProduct[productName] = quantityPerProduct.getOrDefault(productName, 0) + quantity
                revenuePerProduct[productName] = revenuePerProduct.getOrDefault(productName, 0) + price
            }
        }

        val mostSoldCategory = quantityPerCategory.maxByOrNull { it.value }?.key ?: "N/A"
        val mostProfitableCategory = revenuePerCategory.maxByOrNull { it.value }?.key ?: "N/A"

        val topProductsByQuantity = quantityPerProduct.entries
            .sortedByDescending { it.value }
            .take(5)
            .map { it.key to it.value }

        val topProductsByRevenue = revenuePerProduct.entries
            .sortedByDescending { it.value }
            .take(5)
            .map { it.key to it.value }

        return AnalyticsResult(
            mostSoldCategory = mostSoldCategory,
            mostProfitableCategory = mostProfitableCategory,
            totalOrders = orders.size,
            totalRevenue = totalRevenue,
            averageOrderValue = if (orders.isNotEmpty()) totalRevenue / orders.size else 0,
            quantityPerCategory = quantityPerCategory,
            revenuePerCategory = revenuePerCategory,
            topProductsByQuantity = topProductsByQuantity,
            topProductsByRevenue = topProductsByRevenue,
            ordersPerDay = ordersPerDay
        )
    }

}
