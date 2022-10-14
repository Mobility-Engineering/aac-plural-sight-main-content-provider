package com.dexcom.sdk.aac_fullcontentapp.receiver

import android.content.Context
import android.content.Intent


object CourseEventsBroadcastHelper {
    const val projectRoot = "com.dexcom.sdk.aac_fullcontentapp"
    const val ACTION_COURSE_EVENT = "$projectRoot.action.COURSE_EVENT"
    const val EXTRA_COURSE_ID ="$projectRoot.extra.COURSE_ID"
    const val EXTRA_COURSE_MESSAGE = "$projectRoot.extra.COURSE_MESSAGE"

    fun sendEventBroadcast(context: Context, courseId: String, message: String) {
    val intent = Intent(ACTION_COURSE_EVENT)
        intent.putExtra(EXTRA_COURSE_ID, courseId)
        intent.putExtra(EXTRA_COURSE_MESSAGE, message)
        context.sendBroadcast(intent)
    }
}