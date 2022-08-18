package com.dexcom.sdk.aac_fullcontentapp.service

import android.app.IntentService
import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.dexcom.sdk.aac_fullcontentapp.service.NoteBackup.ALL_COURSES

//IntentService and JobIntentService are deprecated as of Android 31, use WorkMaNanger instead Implement Service using WorkManager instead
class NoteBackupService(val context: Context, val params: WorkerParameters): Worker(context, params) {

    override fun doWork(): Result {
        val data = params.inputData

        //All this is wanted to be done by this service used to be handle intent from which the parameters received will turn into  the actual
        //courseId to perform NoteKeeperContentProvider query
        return try {
            data.getString(EXTRA_COURSE_ID)?.let{NoteBackup.doBackup(context, it)}

            Result.success()
        }catch(throwable:Throwable){
            throwable.printStackTrace()
            Result.failure()
        }

    }
    companion object{
        const val EXTRA_COURSE_ID = "com.dexcom.sdk.aac_fullcontentapp"
    }
}