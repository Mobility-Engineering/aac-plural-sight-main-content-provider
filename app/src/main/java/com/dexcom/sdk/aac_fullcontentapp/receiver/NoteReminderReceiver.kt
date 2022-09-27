package com.dexcom.sdk.aac_fullcontentapp.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.dexcom.sdk.aac_fullcontentapp.notification.NoteReminderNotification

class NoteReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val noteTitle = intent.getStringExtra(EXTRA_NOTE_TITLE)
        val noteText = intent.getStringExtra(EXTRA_NOTE_TEXT)
        val noteId = intent.getIntExtra(EXTRA_NOTE_ID, INVALID_ID)


        if (intent.action.equals(ACTION_NOTE_REMINDER_EVENT)) {
            NoteReminderNotification.notify(context, noteTitle, noteText, noteId,)
        }
    }

    companion object {
        const val INVALID_ID = -1
        const val absPathRoot = "com.dexcom.sdk.aac_fullcontentapp"

        const val ACTION_NOTE_REMINDER_EVENT = "$absPathRoot.action.NOTE_REMINDER_EVENT"
        const val EXTRA_NOTE_TITLE = "$absPathRoot.extra.NOTE_TITLE"
        const val EXTRA_NOTE_TEXT = "$absPathRoot.extra.NOTE_TEXT"
        const val EXTRA_NOTE_ID = "$absPathRoot.extra.NOTE_ID"
    }
}