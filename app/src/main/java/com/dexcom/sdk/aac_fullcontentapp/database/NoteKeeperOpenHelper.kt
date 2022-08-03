package com.dexcom.sdk.aac_fullcontentapp.database

import android.content.Context
import android.database.sqlite.SQLiteOpenHelper
import com.dexcom.sdk.aac_fullcontentapp.database.NoteKeeperOpenHelper
import android.database.sqlite.SQLiteDatabase
import com.dexcom.sdk.aac_fullcontentapp.database.NoteKeeperDatabaseContract
import com.dexcom.sdk.aac_fullcontentapp.database.DatabaseDataWorker

/**
 * Created by Jim.
 */
class NoteKeeperOpenHelper(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(NoteKeeperDatabaseContract.CourseInfoEntry.SQL_CREATE_TABLE)
        db.execSQL(NoteKeeperDatabaseContract.NoteInfoEntry.SQL_CREATE_TABLE)
        val worker = DatabaseDataWorker(db)
        worker.insertCourses()
        worker.insertSampleNotes()
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    companion object {
        const val DATABASE_NAME = "NoteKeeper.db"
        const val DATABASE_VERSION = 1
    }
}