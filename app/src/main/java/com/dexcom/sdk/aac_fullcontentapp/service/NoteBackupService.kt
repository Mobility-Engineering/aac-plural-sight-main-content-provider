package com.dexcom.sdk.aac_fullcontentapp.service


import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

//IntentService and JobIntentService are deprecated as of Android 31, use WorkMaNanger instead Implement Service using WorkManager instead
//Context appears to be set automatically when starting up service
class NoteBackupService(val context: Context, val params: WorkerParameters) :
    CoroutineWorker(context, params) {
    val handler = Handler(Looper.getMainLooper())

    override suspend fun doWork(): Result {
        val data = params.inputData
        return try {
            data.getString(EXTRA_COURSE_ID)?.let { NoteBackup.doBackup(context, it) }

            Result.success()
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
            Result.failure()
        }
    }

    companion object {
        const val EXTRA_COURSE_ID = "com.dexcom.sdk.aac_fullcontentapp"
    }
}