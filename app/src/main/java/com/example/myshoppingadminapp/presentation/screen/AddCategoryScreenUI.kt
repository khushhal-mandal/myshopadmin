package com.example.myshoppingadminapp.presentation.screen

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.myshoppingadminapp.common.CATEGORY
import com.example.myshoppingadminapp.domain.model.Category
import com.example.myshoppingadminapp.presentation.viewmodel.AppViewModel

@Composable
fun AddCategoryScreenUI(
    viewModel: AppViewModel = hiltViewModel(),
    navController: NavController
) {
    var categoryName by remember { mutableStateOf("") }
    var categoryDescription by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageUrl by remember { mutableStateOf("") }

    val context = LocalContext.current
    val addCategoryState by viewModel.addCategoryState.collectAsState()
    val uploadImageState by viewModel.uploadImageState.collectAsState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) {
        if (it != null) {
            imageUri = it
            viewModel.uploadImage(imageUri = it, location = CATEGORY)
        }
    }

    // Toasts and state resets
    LaunchedEffect(addCategoryState.data) {
        if (addCategoryState.data.isNotEmpty()) {
            Toast.makeText(context, addCategoryState.data, Toast.LENGTH_SHORT).show()
            categoryName = ""
            categoryDescription = ""
            imageUri = null
            imageUrl = ""
            viewModel.resetAddCategoryState()
        }
    }

    LaunchedEffect(addCategoryState.error) {
        if (addCategoryState.error.isNotEmpty()) {
            Toast.makeText(context, addCategoryState.error, Toast.LENGTH_SHORT).show()
            viewModel.resetAddCategoryState()
        }
    }

    LaunchedEffect(uploadImageState.data) {
        if (uploadImageState.data.isNotEmpty()) {
            imageUrl = uploadImageState.data
            Toast.makeText(context, "Image uploaded", Toast.LENGTH_SHORT).show()
            viewModel.resetUploadImageState()
        }
    }

    // Show loading
    if (addCategoryState.isLoading || uploadImageState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Add Category", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        if (imageUri != null) {
            AsyncImage(
                model = imageUri,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(
                        width = 1.dp,
                        color = androidx.compose.ui.graphics.Color.Gray,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable {
                        launcher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    Text("Add Image")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = categoryName,
            onValueChange = { categoryName = it },
            label = { Text("Category Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = categoryDescription,
            onValueChange = { categoryDescription = it },
            label = { Text("Category Description") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        val isFormValid = categoryName.isNotBlank() &&
                categoryDescription.isNotBlank() &&
                imageUrl.isNotBlank()

        Button(
            onClick = {
                viewModel.addCategory(
                    Category(
                        name = categoryName,
                        description = categoryDescription,
                        image = imageUrl
                    )
                )
            },
            enabled = isFormValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Category")
        }
    }
}