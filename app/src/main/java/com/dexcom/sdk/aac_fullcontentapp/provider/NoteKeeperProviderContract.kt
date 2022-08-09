package com.dexcom.sdk.aac_fullcontentapp.provider

import android.net.Uri
import android.provider.BaseColumns

object NoteKeeperProviderContract {
    // private constructor() {
    val AUTHORITY = "com.dexcom.sdk.aac_fullcontentapp.provider.provider"
    val AUTHORITY_URI = Uri.parse("{content://$AUTHORITY")

    interface CoursesIdColumns {
        val COLUMN_COURSE_ID: String
            get() = "course_id"
    }

    interface CoursesColumns {
        val COLUMN_COURSE_TITLE: String
            get() = "course_title"
    }

    interface NotesColumns {
        val COLUMN_NOTE_TITLE: String
            get() = "note_title"
        val COLUMN_NOTE_TEXT: String
            get() = "note_text"
    }

    object Courses : BaseColumns, CoursesColumns,
        CoursesIdColumns {
        val PATH = "courses"
        val CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, PATH)
    }

    object Notes : BaseColumns, NotesColumns, CoursesIdColumns{
        val PATH = "notes"
        val CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, PATH)
    }
}
