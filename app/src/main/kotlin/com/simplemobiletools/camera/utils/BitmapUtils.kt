package com.simplemobiletools.camera.utils

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream

object BitmapUtils {

    fun getBitmapFromAssets(context: Context, fileName: String, width: Int, height: Int): Bitmap? {
        val assetManager = context.assets

        val inputStream: InputStream
        val bitmap: Bitmap? = null
        try {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            inputStream = assetManager.open(fileName)

            options.inSampleSize = calculateInSampleSize(options, width, height)
            options.inJustDecodeBounds = false
            return BitmapFactory.decodeStream(inputStream, null, options)
        } catch(e: IOException) {
            Log.e("DEBUG", e.message)
        }
        return null
    }

    fun getBitmapFromGallery(context: Context, path: Uri, width: Int, height: Int): Bitmap {
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(path, filePathColumn, null, null, null)
        cursor!!.moveToFirst()
        val columnIndex = cursor.getColumnIndex(filePathColumn[0])
        val picturePath = cursor.getString(columnIndex)
        cursor.close()

        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(picturePath, options)
        options.inSampleSize = calculateInSampleSize(options, width, height)
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeFile(picturePath, options)
    }

    fun insertImage(contentResolver: ContentResolver, source: Bitmap?, title: String, description: String): String? {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, title)
        values.put(MediaStore.Images.Media.DISPLAY_NAME, title)
        values.put(MediaStore.Images.Media.DESCRIPTION, description)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis())
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())

        var url: Uri? = null
        var stringUrl: String? = null
        try {
            url = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            if (source != null) {
                val imageOut = contentResolver.openOutputStream(url!!)

                try {
                    source.compress(Bitmap.CompressFormat.JPEG, 50, imageOut)
                } finally {
                    imageOut!!.close()
                }

                val id = ContentUris.parseId(url)
                val miniThumb = MediaStore.Images.Thumbnails.getThumbnail(contentResolver, id, MediaStore.Images.Thumbnails.MINI_KIND, null)
                storeThumbnail(contentResolver, miniThumb, id, 50f, 50f, MediaStore.Images.Thumbnails.MICRO_KIND)
            } else {
                contentResolver.delete(url!!, null, null)
                url = null
            }
        } catch (e: Exception) {
            if (url != null) {
                contentResolver.delete(url, null, null)
                url = null
            }
            e.printStackTrace()
        }

        if (url != null) {
            stringUrl = url.toString()
        }
        return stringUrl
    }

    private fun storeThumbnail(contentResolver: ContentResolver, source: Bitmap?, id: Long, width: Float, height: Float, microKind: Int): Bitmap? {
        val matrix = Matrix()
        val scaleX = width/source!!.width
        val scaleY = height/source!!.height
        matrix.setScale(scaleX, scaleY)

        val thumb = Bitmap.createBitmap(source, 0, 0, source!!.width, source!!.height, matrix, true)

        val values = ContentValues(4)
        values.put(MediaStore.Images.Thumbnails.KIND, microKind)
        values.put(MediaStore.Images.Thumbnails.IMAGE_ID, id.toInt())
        values.put(MediaStore.Images.Thumbnails.HEIGHT, thumb.height)
        values.put(MediaStore.Images.Thumbnails.WIDTH, thumb.width)

        val url = contentResolver.insert(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, values)

        try {
            val thumbOut = contentResolver.openOutputStream(url!!)
            thumb.compress(Bitmap.CompressFormat.JPEG, 100, thumbOut)
            thumbOut!!.close()
            return thumb
        }
        catch (ex: FileNotFoundException) {
            return null
            ex.printStackTrace()
        }
        catch (ex: IOException) {
            return null
            ex.printStackTrace()
        }
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, regWidth: Int, regHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if ((height > regHeight) || (width > regWidth)) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            while (((halfHeight / inSampleSize) >= regWidth) && ((halfWidth / inSampleSize) >= regWidth)) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
}
