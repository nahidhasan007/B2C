package net.sharetrip.shared.utils.imageCoprresion

import android.content.Context
import android.graphics.Bitmap.CompressFormat
import java.io.File
import java.io.IOException

class ImageZipper(context: Context) {
    private val maxWidth = 612
    private val maxHeight = 816
    private val quality = 80
    private val orientation = 0
    private val compressFormat = CompressFormat.JPEG
    private var destinationDirectory: String = context.cacheDir.path + File.separator + "images"

    @JvmOverloads
    @Throws(IOException::class)
    fun compressToFile(imageFile: File?, fileName: String = imageFile!!.name, orientation: Int = this.orientation): File {
        return Compressor.compressImageFile(imageFile, maxHeight, maxWidth,
                destinationDirectory + File.separator + fileName, quality, compressFormat, orientation)
    }
}