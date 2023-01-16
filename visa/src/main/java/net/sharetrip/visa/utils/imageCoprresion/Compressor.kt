package net.sharetrip.visa.utils.imageCoprresion

import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object Compressor {
    private var height = 0
    private var width = 0
    private var inSampleSize = 0

    @Throws(IOException::class)
    fun compressImageFile(
        imageFile: File?, reqHeight: Int, reqWidth: Int,
        filePath: String?, quality: Int,
        compressFormat: CompressFormat?, orientation: Int
    ): File {
        var fileOutputStream: FileOutputStream? = null
        val file = File(filePath).parentFile
        if (!file.exists()) {
            file.mkdirs()
        }
        try {
            fileOutputStream = FileOutputStream(filePath)
            decodeBitmapAndCompress(imageFile!!, reqHeight, reqWidth, orientation)
                .compress(compressFormat, quality, fileOutputStream)
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.flush()
                fileOutputStream.close()
            }
        }
        return File(filePath)
    }

    @Throws(IOException::class)
    fun decodeBitmapAndCompress(
        imageFile: File,
        reqHeight: Int,
        reqWidth: Int,
        reqOrientation: Int
    ): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(imageFile.absolutePath, options)
        //Calculating Sample Size
        options.inSampleSize = calculateSampleSize(options, reqHeight, reqWidth)
        options.inJustDecodeBounds = false
        var scaledBitmap = BitmapFactory.decodeFile(imageFile.absolutePath, options)
        val exifInterface = ExifInterface(imageFile.absolutePath)
        val orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0)
        val matrix = Matrix()

        when (orientation) {
            6 -> {
                matrix.postRotate(90f)
            }
            3 -> {
                matrix.postRotate(180f)
            }
            8 -> {
                matrix.postRotate(270f)
            }
        }

        if (reqOrientation > 0) {
            matrix.postRotate(reqOrientation.toFloat())
        }
        scaledBitmap = Bitmap.createBitmap(
            scaledBitmap, 0, 0, scaledBitmap.width, scaledBitmap.height, matrix, true
        )
        return scaledBitmap
    }

    private fun calculateSampleSize(
        options: BitmapFactory.Options,
        reqHeight: Int,
        reqWidth: Int
    ): Int {
        height = options.outHeight
        width = options.outWidth
        inSampleSize = 1
        val halfHeight = height / 2
        val halfWidth = width / 2
        if (height > reqHeight || width > reqWidth) {
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
}
