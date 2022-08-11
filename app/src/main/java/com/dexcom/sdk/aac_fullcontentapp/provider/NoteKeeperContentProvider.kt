package com.dexcom.sdk.aac_fullcontentapp.provider

 import android.app.UiAutomation
import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.provider.BaseColumns
import com.dexcom.sdk.aac_fullcontentapp.database.NoteKeeperDatabaseContract.*

import com.dexcom.sdk.aac_fullcontentapp.database.NoteKeeperOpenHelper
import kotlinx.coroutines.selects.select

class NoteKeeperContentProvider : ContentProvider() {

    private val COURSES = 0
    private val NOTES = 1
    private val NOTES_ROW = 2
    private val NOTES_EXPANDED = 2
    var dbOpenHelper: NoteKeeperOpenHelper? = null
    val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)

    init {
        uriMatcher.addURI(
            NoteKeeperProviderContract.AUTHORITY,
            NoteKeeperProviderContract.Courses.PATH,
            COURSES
        )
        uriMatcher.addURI(
            NoteKeeperProviderContract.AUTHORITY,
            NoteKeeperProviderContract.Notes.PATH,
            NOTES
        )
        uriMatcher.addURI(
            NoteKeeperProviderContract.AUTHORITY,
            "${NoteKeeperProviderContract.Notes.PATH}/#",
            NOTES_ROW
        )
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {

        val db = dbOpenHelper?.writableDatabase
        val uriMatch = uriMatcher.match(uri)
        when (uriMatch) {
            NOTES_ROW -> {
             db?.delete(NoteInfoEntry.TABLE_NAME, selection, selectionArgs)
            }
        }
        return 1
}

override fun getType(uri: Uri): String? {
    TODO(
        "Implement this to handle requests for the MIME type of the data" +
                "at the given URI"
    )
}

override fun insert(uri: Uri, values: ContentValues?): Uri? {
    val db = dbOpenHelper?.readableDatabase
    var rowId: Long? = -1
    var rowUri: Uri? = null
    val uriMatch = uriMatcher.match(uri)
    when (uriMatch) {
        NOTES -> {
            rowId = db?.insert(NoteInfoEntry.TABLE_NAME, null, values)
            rowUri =
                ContentUris.withAppendedId(
                    NoteKeeperProviderContract.Notes.CONTENT_URI,
                    rowId ?: rowId ?: -1
                )
        }
        COURSES -> {
            rowId = db?.insert(CourseInfoEntry.TABLE_NAME, null, values)
            rowUri = ContentUris.withAppendedId(
                NoteKeeperProviderContract.Courses.CONTENT_URI,
                rowId ?: rowId ?: -1
            )
        }
    }
    return rowUri
}

override fun onCreate(): Boolean {
    dbOpenHelper = NoteKeeperOpenHelper(context)
    //context for Content provider
    return true
}


override fun query(

    uri: Uri, projection: Array<String>?, selection: String?,
    selectionArgs: Array<String>?, sortOrder: String?
): Cursor? {
    val uriMatch = uriMatcher.match(uri)
    val db = dbOpenHelper?.readableDatabase
    var cursor: Cursor? = null
    when (uriMatch) {
        COURSES -> {
            cursor = db?.query(
                CourseInfoEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
            )
        }
        NOTES -> {
            cursor = db?.query(
                NoteInfoEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
            )
        }
        NOTES_ROW -> {
            val rowId = ContentUris.parseId(uri)
            val rowSelection = "${BaseColumns._ID} =?"
            val rowSelectionArgs = arrayOf(rowId.toString())
            cursor = db?.query(
                NoteInfoEntry.TABLE_NAME,
                projection,
                rowSelection,
                rowSelectionArgs,
                null,
                null,
                null
            )
        }
    }
    return cursor
}

override fun update(
    uri: Uri, values: ContentValues?, selection: String?,
    selectionArgs: Array<String>?
): Int {
    TODO("Implement this to handle requests to update one or more rows.")
}

}