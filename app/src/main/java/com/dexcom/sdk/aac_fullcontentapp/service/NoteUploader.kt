package com.dexcom.sdk.aac_fullcontentapp.service

import android.content.ContentProviderClient
import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.dexcom.sdk.aac_fullcontentapp.provider.NoteKeeperContentProvider
import com.dexcom.sdk.aac_fullcontentapp.provider.NoteKeeperProviderContract.Notes
import kotlinx.coroutines.*
import java.lang.Thread.sleep
import java.util.concurrent.Executors

object NoteUploader {
    var uri = Uri.parse("content://com.dexcom.sdk.aac_fullcontentapp.provider.provider")
    val TAG = NoteUploader::class.simpleName.toString()

    var isCanceled = false
    fun cancel() {
        isCanceled = true
    }

    fun doUpload(
        context: Context,
        dataUri: Uri
    ) {//suspend fun doUpload(context: Context, dataUri: Uri) {
        Log.i(
            "MAIN_THREAD",
            "Current thread before entering runBlocking is ${Thread.currentThread()}"
        )
        //runBlocking {
            Log.i(
                "COUROUTINE _THREAD",
                "Current thread when entering runBlocking is ${Thread.currentThread()}"
            )
            //val contentResolver = applicationContext.contentResolver
            //launch {
                Log.i(
                    "LAUNCH _THREAD",
                    "Current thread when entering launch() ${Thread.currentThread()}"
                )
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
                        Log.i(
                            TAG,
                            ">>> Uploading Note on thread ${Thread.currentThread()} where main thread is: ${
                                Looper.getMainLooper().getThread()
                            }<<<< ${courseId} | ${noteTitle} | ${noteText} with count ${count}"
                        )
                        count++

                        simulateRunningWork()

                        Log.i("Coroutine", Thread.currentThread().toString())
                    }
                }

                Log.i(TAG, ">>> UPLOAD COMPLETE ***<<<")
                cursor?.close()
            }
        //}
        //Executors.newSingleThreadExecutor().execute(Runnable {
            // todo: do your background tasks
            //Log.i(
            //    "EXECUTOR_THREAD",
            //    "Current thread when entering Executor: ${Thread.currentThread()}"
            //)
        //})

    //}

    private fun simulateRunningWork() {//private suspend fun simulateRunningWork()
        Log.i(
            "SUSPEND_THREAD",
            "Current thread when entering suspend function() ${Thread.currentThread()}"
        )
        //delay(400L)
        //Looper.getMainLooper()

        //val handler = Handler(Looper.getMainLooper())
        //handler.post() {
            Log.i(
                "M_LOOPER_HANDLER_THREAD",
                "Current thread when entering main looper handler function() ${Thread.currentThread()}"
            )
           sleep(400L)
        }
    }

