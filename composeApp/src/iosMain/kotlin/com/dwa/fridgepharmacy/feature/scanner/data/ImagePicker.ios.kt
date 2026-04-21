package com.dwa.fridgepharmacy.feature.scanner.data

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.UIKit.UIApplication
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerOriginalImage
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.darwin.NSObject

@Composable
actual fun rememberImagePickerLauncher(
    onResult: (ByteArray) -> Unit
): ImagePickerLauncher {
    return remember {
        ImagePickerLauncher(
            launchCamera = { showPicker(UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera, onResult) },
            launchGallery = { showPicker(UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypePhotoLibrary, onResult) }
        )
    }
}

private fun showPicker(sourceType: UIImagePickerControllerSourceType, onResult: (ByteArray) -> Unit) {
    val picker = UIImagePickerController()
    picker.sourceType = sourceType

    val delegate = object : NSObject(), UIImagePickerControllerDelegateProtocol, UINavigationControllerDelegateProtocol {
        override fun imagePickerController(
            picker: UIImagePickerController,
            didFinishPickingMediaWithInfo: Map<Any?, *>
        ) {
            val image = didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage
            image?.let {
                val data = UIImageJPEGRepresentation(it, 0.9)
                data?.let { nsData -> onResult(nsData.toByteArray()) }
            }
            picker.dismissViewControllerAnimated(true, null)
        }

        override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
            picker.dismissViewControllerAnimated(true, null)
        }
    }

    picker.delegate = delegate

    val rootVC = UIApplication.sharedApplication.keyWindow?.rootViewController
    rootVC?.presentViewController(picker, animated = true, completion = null)
}

@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArray(): ByteArray {
    val size = length.toInt()
    if (size == 0) return ByteArray(0)
    val bytes = ByteArray(size)
    bytes.usePinned { pinned ->
        platform.posix.memcpy(pinned.addressOf(0), this@toByteArray.bytes, length)
    }
    return bytes
}
