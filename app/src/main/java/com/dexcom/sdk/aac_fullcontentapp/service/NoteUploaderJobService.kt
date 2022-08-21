package com.dexcom.sdk.aac_fullcontentapp.service

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

class NoteUploaderJobService() : JobService() {
    override fun onStartJob(jobParameters: JobParameters): Boolean {
        val stringDataUri = jobParameters.getExtras().getString(EXTRA_DATA_URI)
        val dataUri = Uri.parse(stringDataUri)
        Executors.newSingleThreadExecutor().execute(kotlinx.coroutines.Runnable {
            NoteUploader.doUpload(baseContext, dataUri)

            if (NoteUploader.isCanceled)
                jobFinished(jobParameters, false)

        })
            return false
    }



    override fun onStopJob(params: JobParameters?): Boolean {
        NoteUploader.cancel()
        return false
    }

    companion object {
        const val EXTRA_DATA_URI = "EXTRA_DATA_URI"
    }
}