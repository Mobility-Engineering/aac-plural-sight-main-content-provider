package com.dexcom.sdk.aac_fullcontentapp.service

import android.content.ContentProviderClient
import android.content.Context
import android.net.Uri
import android.os.Handler
import android.util.Log
import com.dexcom.sdk.aac_fullcontentapp.provider.NoteKeeperContentProvider
import com.dexcom.sdk.aac_fullcontentapp.provider.NoteKeeperProviderContract.Notes
import java.lang.Thread.sleep

object NoteUploader {
    var uri = Uri.parse("content://com.dexcom.sdk.aac_fullcontentapp.provider.provider")
    val TAG = NoteUploader::class.simpleName.toString()


    var isCanceled = false
    fun cancel() {
        isCanceled = true
    }
    fun doUpload(context: Context, dataUri: Uri) {
        //val contentResolver = applicationContext.contentResolver
        var providerClient: ContentProviderClient? =
            context.contentResolver.acquireContentProviderClient(uri)
        val columns = arrayOf(
            Notes.COLUMN_COURSE_ID,
            Notes.COLUMN_NOTE_TITLE,
            Notes.COLUMN_NOTE_TEXT
        )
        var selection: String? = null
        var selectionArgs: Array<String>? = null


        var cursor = providerClient?.query(
            dataUri,
            columns,
            selection,
            selectionArgs,
            null
        )

        var courseIdPos = cursor?.getColumnIndex(Notes.COLUMN_COURSE_ID)
        var noteTitlePos = cursor?.getColumnIndex(Notes.COLUMN_NOTE_TITLE)
        var noteTextPos = cursor?.getColumnIndex(Notes.COLUMN_NOTE_TEXT)

        var count = 1
        while (!isCanceled && cursor?.moveToNext() == true) {
            val courseId = courseIdPos?.let { cursor?.getString(it) }
            val noteTitle = noteTitlePos?.let { cursor?.getString(it) }
            val noteText = noteTextPos?.let { cursor?.getString(it) }

            if (!noteTitle.equals("")) {
                Log.i(TAG, ">>> Uploading Note<<<< ${courseId} | ${noteTitle} | ${noteText} with count ${count}")
                count++
                simulateRunningWork()
            }
        }
        Log.i(TAG, ">>> UPLOAD COMPLETE ***<<<")
        cursor?.close()
    }

    private fun simulateRunningWork() {
        val handler = Handler()
        handler.post {
            sleep(400L)
        }
    }
}