package com.dexcom.sdk.aac_fullcontentapp.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.dexcom.sdk.aac_fullcontentapp.NavigationDrawerActivity
import com.dexcom.sdk.aac_fullcontentapp.NoteActivity
import com.dexcom.sdk.aac_fullcontentapp.R

object NoteReminderNotification {


    private val CHANNEL_ID: String = "1"
     lateinit private var context:Context
     fun notify(context: Context, noteTitle:String?, noteText:String?, noteId:Int ){


            val title = noteTitle
            val text = noteText
            var builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_note_reminder)
                .setContentTitle(title)
                .setContentText(text)
                .setDefaults(Notification.DEFAULT_ALL)
                .setTicker("This is the review note for ${title}")
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .setBigContentTitle(title + title + title)
                        .bigText(text + text + text)
                        .setSummaryText("Review Notes")
                )
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = context.getString(R.string.chanel_name)
                val descriptionText = context.getString(R.string.channel_description)
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel =
                    NotificationChannel(context.getString(R.string.channel_id), name, importance).apply {
                        description = descriptionText
                    }
                // Register the channel with the system
                val notificationManager: NotificationManager =
                    context.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }

            //Create an explicit intent for the activity

            val pendingIntent = createPendingIntent(context, noteId)
            builder.setContentIntent(pendingIntent)
                .setChannelId(context.getString(R.string.channel_id))
                .setAutoCancel(true)


            //builder = addAction(context,builder)
            with(NotificationManagerCompat.from(context)) {
                // notificationId is a unique int for each notification that you must define
                notify(0, builder.build())
            }
    }


    private fun createPendingIntent(context:Context, noteId:Int): PendingIntent {
        //don't use intent activity atributte as it will be set to null once the activity is destroyed
        val intent = Intent(context, NoteActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        intent.putExtra(NoteActivity.NOTE_ID, noteId)
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
        return pendingIntent
    }

    private fun addAction(context:Context, builder: NotificationCompat.Builder): NotificationCompat.Builder {
        builder.addAction(
            0,
            "View all notes",
            PendingIntent.getActivity(
                context,
                0,
                Intent(context, NavigationDrawerActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                },
                PendingIntent.FLAG_IMMUTABLE
            )
        )
        return builder
    }
}