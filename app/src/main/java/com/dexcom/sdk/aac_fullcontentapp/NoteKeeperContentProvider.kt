package com.dexcom.sdk.aac_fullcontentapp

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import com.dexcom.sdk.aac_fullcontentapp.database.NoteKeeperDatabaseContract.*
import com.dexcom.sdk.aac_fullcontentapp.database.NoteKeeperOpenHelper
class NoteKeeperContentProvider : ContentProvider() {

    var dbOpenHelper:NoteKeeperOpenHelper? = null
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        TODO("Implement this to handle requests to delete one or more rows")
    }

    override fun getType(uri: Uri): String? {
        TODO(
            "Implement this to handle requests for the MIME type of the data" +
                    "at the given URI"
        )
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        TODO("Implement this to handle requests to insert a new row.")
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
        val db = dbOpenHelper?.readableDatabase
        var cursor = db?.query(CourseInfoEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder)
        return cursor
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        TODO("Implement this to handle requests to update one or more rows.")
    }
}