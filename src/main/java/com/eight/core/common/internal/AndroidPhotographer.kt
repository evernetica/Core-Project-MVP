package com.eight.core.common.internal

import android.app.Activity
import android.content.ClipData
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.eight.core.common.Photographer
import com.eight.core.common.ResultHandler
import com.eight.core.extension.weak
import java.io.File
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class AndroidPhotographer(
    fragment: Fragment,
    private val authority: String
) : Photographer, ResultHandler, LifecycleObserver {

    private companion object {
        const val REQUEST_CODE_CAMERA = 0x400
        const val REQUEST_CODE_GALLERY = 0x500

        const val TYPE_IMAGES = "image/*"
    }

    private val reference = fragment.weak()

    private var file: File? = null
    private var continuation: Continuation<File?>? = null

    init {
        fragment.context?.deleteTemporaryFiles()
        fragment.lifecycle.addObserver(this)
    }

    override suspend fun choose(source: Photographer.Source): File? = suspendCoroutine { c ->
        val fragment = reference.get()
        val context = fragment?.context

        if (fragment == null || context == null) {
            c.resumeWithException(Photographer.Exception.ContextNotFound())
            return@suspendCoroutine
        }

        when (source) {
            Photographer.Source.CAMERA -> {
                val file = context.createImageFile() ?: run {
                    c.resumeWithException(Photographer.Exception.UnableCreateFile())
                    return@suspendCoroutine
                }

                this.file = file

                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (intent.resolveActivity(context.packageManager) == null) {
                    c.resumeWithException(Photographer.Exception.UnableLaunchCamera())
                    return@suspendCoroutine

                }

                val uri = FileProvider.getUriForFile(context, authority, file)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)

                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                    intent.clipData = ClipData.newRawUri("", uri)
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                fragment.startActivityForResult(intent, REQUEST_CODE_CAMERA)
            }

            Photographer.Source.GALLERY -> fragment.startActivityForResult(
                Intent(Intent.ACTION_GET_CONTENT).setType(TYPE_IMAGES),
                REQUEST_CODE_GALLERY
            )
        }

        continuation = c
    }

    override fun handle(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        when (requestCode) {
            REQUEST_CODE_CAMERA -> if (resultCode != Activity.RESULT_OK) {
                file?.delete(); file = null
                continuation?.resume(null)

            } else continuation?.resume(file)

            REQUEST_CODE_GALLERY -> {
                val uri = data?.data
                val file = reference.get()?.context
                    ?.getRealPathFromUri(uri ?: Uri.EMPTY)
                    ?.let(::File)

                continuation?.resume(file)
            }

            else -> return false
        }

        continuation = null
        return true
    }

    // region Internal

    private fun Context.createImageFile(): File? {
        val storage = getExternalFilesDir(Environment.DIRECTORY_PICTURES) ?: return null
        return createTempFile(suffix = ".jpg", directory = storage)
    }

    private fun Context.deleteTemporaryFiles() {
        file?.delete(); file = null
        val storage = getExternalFilesDir(Environment.DIRECTORY_PICTURES) ?: return
        storage.listFiles().forEach { it.delete() }
    }

    private fun Context.getRealPathFromUri(uri: Uri): String? {
        if (DocumentsContract.isDocumentUri(this, uri)) {
            if (uri.isExternalStorageDocument) {
                val id = DocumentsContract.getDocumentId(uri)
                val split = id.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]

                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }

            } else if (uri.isDownloadsDocument) {
                val id = DocumentsContract.getDocumentId(uri).let { id ->
                    if (id.startsWith("raw:")) id.removeRange(0, 4) else id
                }

                if (id.isNullOrBlank()) return null

                return try {
                    val contentUri = ContentUris.withAppendedId(
                        "content://downloads/public_downloads".toUri(), id.toLong()
                    )

                    getDataColumn(this, contentUri, null, null)

                } catch (e: NumberFormatException) {
                    e.printStackTrace()
                    null
                }

            } else if (uri.isMediaDocument) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

                val contentUri = when (split[0]) {
                    "image" -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    "video" -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    "audio" -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    else -> return null
                }

                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])

                return getDataColumn(this, contentUri, selection, selectionArgs)
            } // MediaProvider
            // DownloadsProvider
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {

            // Return the remote address
            return if (uri.isGooglePhotosUri) uri.lastPathSegment
            else getDataColumn(this, uri, null, null)

        } else if ("file".equals(uri.scheme, ignoreCase = true)) return uri.path

        return null
    }

    private fun getDataColumn(
        context: Context, uri: Uri, selection: String?, selectionArgs: Array<String>?
    ): String? {
        val column = "_data"
        val projection = arrayOf(column)

        context.contentResolver.query(uri, projection, selection, selectionArgs, null)
            ?.use { cursor ->
                if (!cursor.moveToFirst()) return null

                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }

        return null
    }

    private val Uri.isExternalStorageDocument: Boolean
        get() = "com.android.externalstorage.documents" == authority

    private val Uri.isDownloadsDocument: Boolean
        get() = "com.android.providers.downloads.documents" == authority

    private val Uri.isMediaDocument: Boolean
        get() = "com.android.providers.media.documents" == authority

    private val Uri.isGooglePhotosUri: Boolean
        get() = "com.google.android.apps.photos.content" == authority


    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun release() {
        continuation?.resume(null)
    }

    // endregion
}