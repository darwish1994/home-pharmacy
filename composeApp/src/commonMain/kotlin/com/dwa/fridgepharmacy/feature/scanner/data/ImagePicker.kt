package com.dwa.fridgepharmacy.feature.scanner.data

import androidx.compose.runtime.Composable

data class ImagePickerLauncher(
    val launchCamera: () -> Unit,
    val launchGallery: () -> Unit
)

@Composable
expect fun rememberImagePickerLauncher(
    onResult: (ByteArray) -> Unit
): ImagePickerLauncher
