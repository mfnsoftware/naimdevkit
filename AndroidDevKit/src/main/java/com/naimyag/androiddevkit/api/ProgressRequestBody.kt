package com.naimyag.androiddevkit.api

import android.os.Handler
import android.os.Looper
import android.webkit.MimeTypeMap
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.BufferedSink
import okio.IOException
import java.io.File
import java.io.FileInputStream

class ProgressRequestBody(file: File, listener: UploadCallbacks) :
    RequestBody() {
    private val mFile: File = file
    private val mListener: UploadCallbacks = listener

    override fun contentType(): MediaType? {
        // i want to upload only images
        //return "image/*".toMediaTypeOrNull()
        return getMimeType(mFile.path)?.toMediaTypeOrNull() ?: "img/*".toMediaTypeOrNull()
        //return "multipart/form-data".toMediaTypeOrNull()
    }

    @Throws(IOException::class)
    override fun contentLength(): Long {
        return mFile.length()
    }

    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        val fileLength: Long = mFile.length()
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE) //2048
        val `in` = FileInputStream(mFile)
        var uploaded: Long = 0
        try {
            var read: Int
            val handler = Handler(Looper.getMainLooper())
            while (`in`.read(buffer).also { read = it } != -1) {
                uploaded += read.toLong()
                sink.write(buffer, 0, read)
                handler.post(ProgressUpdater(uploaded, fileLength))
            }
        } finally {
            `in`.close()
        }
    }

    private inner class ProgressUpdater(private val mUploaded: Long, private val mTotal: Long) :
        Runnable {
        override fun run() {
            try {
                val progress = (100 * mUploaded / mTotal).toInt()
                if (progress == 100) mListener.onFinish(null) else mListener.onProgressUpdate(progress)
            } catch (e: ArithmeticException) {
                mListener.onError()
                e.printStackTrace()
            }
        }
    }

    companion object {
        fun getMimeType(url: String?): String? {
            var type: String? = null
            val extension = MimeTypeMap.getFileExtensionFromUrl(url)
            if (extension != null) {
                type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
            }
            return type
        }
    }

    private val DEFAULT_BUFFER_SIZE = 2048
    init {
        mListener.uploadStart()
    }

}