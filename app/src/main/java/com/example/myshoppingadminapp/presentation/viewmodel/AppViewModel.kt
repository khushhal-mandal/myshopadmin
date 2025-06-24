package com.example.myshoppingadminapp.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myshoppingadminapp.common.state.ResultState
import com.example.myshoppingadminapp.domain.model.AnalyticsResult
import com.example.myshoppingadminapp.domain.model.Banner
import com.example.myshoppingadminapp.domain.model.Category
import com.example.myshoppingadminapp.domain.model.Product
import com.example.myshoppingadminapp.domain.repo.Repo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val repo: Repo
) : ViewModel() {

    // UI States
    private val _addCategoryState = MutableStateFlow(AddCategoryState())
    val addCategoryState = _addCategoryState.asStateFlow()

    private val _getCategoriesState = MutableStateFlow(GetCategoriesState())
    val getCategoriesState = _getCategoriesState.asStateFlow()

    private val _addProductState = MutableStateFlow(AddProductState())
    val addProductState = _addProductState.asStateFlow()

    private val _uploadImageState = MutableStateFlow(UploadImageState())
    val uploadImageState = _uploadImageState.asStateFlow()

    private val _addBannerState = MutableStateFlow(AddBannerState())
    val addBannerState = _addBannerState.asStateFlow()

    private val _analyticsResult = MutableStateFlow(AnalyticsResult())
    val analyticsResult = _analyticsResult.asStateFlow()

    // Add category
    fun addCategory(category: Category) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.addCategory(category).collectLatest {
                when (it) {
                    is ResultState.Loading -> _addCategoryState.value = AddCategoryState(isLoading = true)
                    is ResultState.Success -> _addCategoryState.value = AddCategoryState(data = it.data)
                    is ResultState.Error -> _addCategoryState.value = AddCategoryState(error = it.message)
                }
            }
        }
    }

    fun getCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getCategories().collectLatest {
                when (it) {
                    is ResultState.Loading -> _getCategoriesState.value = GetCategoriesState(isLoading = true)
                    is ResultState.Success -> _getCategoriesState.value = GetCategoriesState(data = it.data)
                    is ResultState.Error -> _getCategoriesState.value = GetCategoriesState(error = it.message)
                }
            }
        }
    }

    fun addProduct(product: Product) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.addProduct(product).collectLatest {
                when (it) {
                    is ResultState.Loading -> _addProductState.value = AddProductState(isLoading = true)
                    is ResultState.Success -> _addProductState.value = AddProductState(data = it.data)
                    is ResultState.Error -> _addProductState.value = AddProductState(error = it.message)
                }
            }
        }
    }

    fun uploadImage(imageUri: Uri, location: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.uploadImage(imageUri, location).collectLatest {
                when (it) {
                    is ResultState.Loading -> _uploadImageState.value = UploadImageState(isLoading = true)
                    is ResultState.Success -> _uploadImageState.value = UploadImageState(data = it.data)
                    is ResultState.Error -> _uploadImageState.value = UploadImageState(error = it.message)
                }
            }
        }
    }

    fun addBanner(banner: Banner) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.addBanner(banner).collectLatest {
                when (it) {
                    is ResultState.Loading -> _addBannerState.value = AddBannerState(isLoading = true)
                    is ResultState.Success -> _addBannerState.value = AddBannerState(data = it.data)
                    is ResultState.Error -> _addBannerState.value = AddBannerState(error = it.message)
                }
            }
        }
    }


    fun fetchAnalytics() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repo.getAnalytics()
            _analyticsResult.value = result
        }
    }

    // Resets
    fun resetAddCategoryState() {
        _addCategoryState.value = AddCategoryState()
    }

    fun resetAddProductState() {
        _addProductState.value = AddProductState()
    }

    fun resetUploadImageState() {
        _uploadImageState.value = UploadImageState()
    }

    fun resetAddBannerState() {
        _addBannerState.value = AddBannerState()
    }
}


// UI State Models
data class AddCategoryState(
    val isLoading: Boolean = false,
    val data: String = "",
    val error: String = ""
)

data class GetCategoriesState(
    val isLoading: Boolean = false,
    val data: List<Category> = emptyList(),
    val error: String = ""
)

data class AddProductState(
    val isLoading: Boolean = false,
    val data: String = "",
    val error: String = ""
)

data class UploadImageState(
    val isLoading: Boolean = false,
    val data: String = "",
    val error: String = ""
)

data class AddBannerState(
    val isLoading: Boolean = false,
    val data: String = "",
    val error: String = ""
)
