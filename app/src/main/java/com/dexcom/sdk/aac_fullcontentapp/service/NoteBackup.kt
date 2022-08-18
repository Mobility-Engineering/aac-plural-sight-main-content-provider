package com.dexcom.sdk.aac_fullcontentapp.service

import android.content.ContentProviderClient
import android.content.Context
import android.net.Uri
import android.os.Handler
import android.util.Log
import com.dexcom.sdk.aac_fullcontentapp.provider.NoteKeeperContentProvider
import com.dexcom.sdk.aac_fullcontentapp.provider.NoteKeeperProviderContract.Notes
import java.lang.Thread.sleep

object NoteBackup {
    var uri = Uri.parse("content://com.dexcom.sdk.aac_fullcontentapp.provider.provider")
    const val ALL_COURSES = "ALL COURSES"
    val TAG = NoteBackup::class.simpleName.toString()

    fun doBackup(context: Context, backUpCourseId: String) {
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
        if (!backUpCourseId.equals(ALL_COURSES)){
            selection = "${Notes.COLUMN_COURSE_ID} =?"
            selectionArgs = arrayOf(backUpCourseId)
        }

        var cursor = providerClient?.query(
            Notes.CONTENT_URI,
            columns,
            selection,
            selectionArgs,
            null
        )

        var courseIdPos = cursor?.getColumnIndex(Notes.COLUMN_COURSE_ID)
        var noteTitlePos = cursor?.getColumnIndex(Notes.COLUMN_NOTE_TITLE)
        var noteTextPos = cursor?.getColumnIndex(Notes.COLUMN_NOTE_TEXT)

        var count = 1
        while (cursor?.moveToNext() == true) {
            val courseId = courseIdPos?.let { cursor?.getString(it) }
            val noteTitle = noteTitlePos?.let { cursor?.getString(it) }
            val noteText = noteTextPos?.let { cursor?.getString(it) }

            if (!noteTitle.equals("")) {
                Log.i(TAG, ">>>Backing up Note<<<< ${courseId} | ${noteTitle} | ${noteText} with count ${count}")
                count++
                //simulateRunningWork()
            }
        }
        Log.i(TAG, ">>> BACKUP COMPLETE ***<<<")
        cursor?.close()


    }

    private fun simulateRunningWork() {
        val handler = Handler()
        handler.post {
            sleep(5000L)
        }
    }

}