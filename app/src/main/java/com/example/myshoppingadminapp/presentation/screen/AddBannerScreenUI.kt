package com.example.myshoppingadminapp.presentation.screen

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
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
import com.example.myshoppingadminapp.common.BANNER
import com.example.myshoppingadminapp.domain.model.Banner
import com.example.myshoppingadminapp.presentation.viewmodel.AppViewModel

@Composable
fun AddBannerScreenUI(
    viewModel: AppViewModel = hiltViewModel(),
    navController: NavController
) {
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<android.net.Uri?>(null) }

    val bannerState = viewModel.addBannerState.collectAsState().value
    val imageUploadState = viewModel.uploadImageState.collectAsState().value

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) selectedImageUri = uri
    }

    LaunchedEffect(bannerState) {
        if (bannerState.data.isNotEmpty()) {
            Toast.makeText(context, bannerState.data, Toast.LENGTH_SHORT).show()
            viewModel.resetAddBannerState()
            title = ""
            description = ""
            selectedImageUri = null
        }
        if (bannerState.error.isNotEmpty()) {
            Toast.makeText(context, bannerState.error, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(imageUploadState) {
        if (imageUploadState.data.isNotEmpty()) {
            val banner = Banner(
                image = imageUploadState.data,
                title = title,
                description = description
            )
            viewModel.addBanner(banner)
            viewModel.resetUploadImageState()
        }
        if (imageUploadState.error.isNotEmpty()) {
            Toast.makeText(context, imageUploadState.error, Toast.LENGTH_SHORT).show()
        }
    }

    val isFormValid = title.isNotEmpty() &&
            description.isNotEmpty() &&
            selectedImageUri != null

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text("Add Banner", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        if (selectedImageUri != null) {
            AsyncImage(
                model = selectedImageUri,
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
            value = title,
            onValueChange = { title = it },
            label = { Text("Banner Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Banner Description") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            maxLines = 5
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                viewModel.uploadImage(selectedImageUri!!, BANNER)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = isFormValid
        ) {
            Text("Upload Banner")
        }
    }
}
