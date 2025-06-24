package com.example.myshoppingadminapp.presentation.screen

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.myshoppingadminapp.common.PRODUCT
import com.example.myshoppingadminapp.domain.model.Product
import com.example.myshoppingadminapp.presentation.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreenUI(
    viewModel: AppViewModel = hiltViewModel(),
    navController: NavController
) {
    var productName by remember { mutableStateOf("") }
    var productPrice by remember { mutableStateOf("") }
    var productFinalPrice by remember { mutableStateOf("") }
    var productCategory by remember { mutableStateOf("") }
    var productDescription by remember { mutableStateOf("") }
    var productAvailableUnits by remember { mutableStateOf("") }

    var expanded by remember { mutableStateOf(false) }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageUrl by remember { mutableStateOf("") }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.getCategories()
    }

    val getCategoriesState = viewModel.getCategoriesState.collectAsState()
    val addProductState = viewModel.addProductState.collectAsState()
    val uploadImageState = viewModel.uploadImageState.collectAsState()

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        if (it != null) {
            viewModel.uploadImage(it, PRODUCT)
            imageUri = it
        }
    }

    // Toast events using LaunchedEffect
    LaunchedEffect(addProductState.value.data) {
        if (addProductState.value.data.isNotBlank()) {
            Toast.makeText(context, addProductState.value.data, Toast.LENGTH_SHORT).show()
            productName = ""
            productPrice = ""
            productFinalPrice = ""
            productCategory = ""
            productDescription = ""
            productAvailableUnits = ""
            imageUri = null
            imageUrl = ""
            viewModel.resetAddProductState()
        }
    }

    LaunchedEffect(addProductState.value.error) {
        if (addProductState.value.error.isNotBlank()) {
            Toast.makeText(context, addProductState.value.error, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(uploadImageState.value.data) {
        if (uploadImageState.value.data.isNotBlank()) {
            imageUrl = uploadImageState.value.data
            Toast.makeText(context, "Image uploaded", Toast.LENGTH_SHORT).show()
            viewModel.resetUploadImageState()
        }
    }

    if (addProductState.value.isLoading || uploadImageState.value.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Add Product", style = MaterialTheme.typography.headlineMedium)
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
                        launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    Text("Add Image")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = productName,
            onValueChange = { productName = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = productPrice,
                onValueChange = { productPrice = it },
                label = { Text("Price") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = productFinalPrice,
                onValueChange = { productFinalPrice = it },
                label = { Text("Final Price") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = productDescription,
            onValueChange = { productDescription = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth().height(100.dp),
            singleLine = false
        )

        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = productCategory,
                onValueChange = {},
                readOnly = true,
                label = { Text("Category") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                getCategoriesState.value.data.filterNotNull().forEach {
                    DropdownMenuItem(
                        text = { Text(it.name) },
                        onClick = {
                            productCategory = it.name
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = productAvailableUnits,
            onValueChange = { productAvailableUnits = it },
            label = { Text("Available Units") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(16.dp))

        val isValidForm = productName.isNotBlank() &&
                productPrice.isNotBlank() &&
                productFinalPrice.isNotBlank() &&
                productDescription.isNotBlank() &&
                productCategory.isNotBlank() &&
                productAvailableUnits.isNotBlank() &&
                imageUrl.isNotBlank()

        Button(
            onClick = {
                viewModel.addProduct(
                    Product(
                        name = productName,
                        price = productPrice,
                        finalPrice = productFinalPrice,
                        description = productDescription,
                        category = productCategory,
                        availableUnits = productAvailableUnits,
                        image = imageUrl
                    )
                )
            },
            enabled = isValidForm,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Product")
        }
    }
}