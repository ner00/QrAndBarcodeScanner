package com.example.barcodescanner.usecase

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore.Images
import androidx.core.content.FileProvider
import com.example.barcodescanner.model.ParsedBarcode
import java.io.File
import java.io.FileOutputStream


object BarcodeImageSaver {

    fun saveImageToCache(context: Context, image: Bitmap, barcode: ParsedBarcode): Uri? {
        // Create folder for image
        val imagesFolder = File(context.cacheDir, "images")
        imagesFolder.mkdirs()

        // Create image file
        val imageFileName = "${barcode.format}_${barcode.schema}_${barcode.date}.png"
        val imageFile = File(imagesFolder, imageFileName)

        // Save image to file
        FileOutputStream(imageFile).apply {
            image.compress(Bitmap.CompressFormat.PNG, 100, this)
            flush()
            close()
        }

        // Return Uri for image file
        return FileProvider.getUriForFile(context, "com.example.barcodescanner.fileprovider", imageFile)
    }

    fun saveImageToPublicDirectory(context: Context, image: Bitmap, barcode: ParsedBarcode) {
        val contentResolver = context.contentResolver

        val imageTitle = "${barcode.format}_${barcode.schema}_${barcode.date}"

        val values = ContentValues().apply {
            put(Images.Media.TITLE, imageTitle)
            put(Images.Media.DISPLAY_NAME, imageTitle)
            put(Images.Media.MIME_TYPE, "image/png")
            put(Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
        }

        val uri = contentResolver.insert(Images.Media.EXTERNAL_CONTENT_URI, values)
        contentResolver.openOutputStream(uri!!)?.apply {
            image.compress(Bitmap.CompressFormat.PNG, 100, this)
            flush()
            close()
        }
    }
}