package com.dexcom.sdk.aac_fullcontentapp

import android.app.Application
import android.content.ContentProviderClient
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.BaseColumns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dexcom.sdk.aac_fullcontentapp.database.NoteKeeperDatabaseContract.*
import com.dexcom.sdk.aac_fullcontentapp.database.NoteKeeperOpenHelper
import com.dexcom.sdk.aac_fullcontentapp.provider.NoteKeeperContentProvider
import com.dexcom.sdk.aac_fullcontentapp.provider.NoteKeeperProviderContract.Courses
import com.dexcom.sdk.aac_fullcontentapp.provider.NoteKeeperProviderContract.Notes
import kotlinx.coroutines.launch

class NoteActivityViewModel(application: Application) : AndroidViewModel(application) {

    var originalNoteText: String? = null
    var originalNoteTitle: String? = null
    var originalNoteCourseId: String? = ""
    var noteId: Int = 0
    var isNewlyCreated: Boolean = true
    var dbOpenHelper = NoteKeeperOpenHelper(application.baseContext)
    var coursesCursor: Cursor? = null
    var notesCursor: Cursor? = null

    var courseIndex = MutableLiveData<Int>()
    var noteTitle = MutableLiveData<String>()
    var noteText = MutableLiveData<String>()

    //ContentProvider parameters
    var uri = Uri.parse("content://com.dexcom.sdk.aac_fullcontentapp.provider.provider")
    var noteKeeperProvider = NoteKeeperContentProvider()
    val contentResolver = application.applicationContext.contentResolver
    var providerClient: ContentProviderClient? = contentResolver.acquireContentProviderClient(uri)

    //Used for populating spinner via ContentProvider as opossed to SQLite querying directly
    var providerCoursesCursor = MutableLiveData<Cursor>()
    var providerNotesCursor = MutableLiveData<Cursor>()
    var providerExpandedNotesCursor = MutableLiveData<Cursor>()

    val courseColumns = arrayOf(
        Courses.COLUMN_COURSE_TITLE,
        Courses.COLUMN_COURSE_ID,
        BaseColumns._ID
    )
    val noteColumns = arrayOf(
        Notes.COLUMN_NOTE_TITLE,
        Notes.COLUMN_NOTE_TEXT,
        Notes.COLUMN_COURSE_ID,
        BaseColumns._ID
    )

    companion object Constants {
        const val ORIGINAL_NOTE_COURSE_ID =
            "com.dexcom.sdk.aac_fullcontentapp.ORIGINAL_NOTE_COURSE_ID"
        const val ORIGINAL_NOTE_COURSE_TITLE =
            "com.dexcom.sdk.aac_fullcontentapp.ORIGINAL_NOTE_COURSE_TITLE"
        const val ORIGINAL_NOTE_COURSE_TEXT =
            "com.dexcom.sdk.aac_fullcontentapp.ORIGINAL_NOTE_COURSE_TEXT"
    }

    fun saveState(outState: Bundle) {
        outState.putString(ORIGINAL_NOTE_COURSE_ID, originalNoteCourseId)
        outState.putString(ORIGINAL_NOTE_COURSE_TITLE, originalNoteTitle)
        outState.putString(ORIGINAL_NOTE_COURSE_TEXT, originalNoteText)
    }


    fun restoreState(inState: Bundle) {
        originalNoteCourseId = inState.getString(ORIGINAL_NOTE_COURSE_ID)
        originalNoteTitle = inState.getString(ORIGINAL_NOTE_COURSE_TITLE)
        originalNoteText = inState.getString(ORIGINAL_NOTE_COURSE_TEXT)
    }


    fun loadCourseData() {

        viewModelScope.launch {

            loadCourseContent()
        }
    }

    private suspend fun loadCourseContent() {
        val courseUri = Courses.CONTENT_URI
        providerCoursesCursor.value =
            providerClient?.query(
                courseUri,
                courseColumns,
                null,
                null,
                CourseInfoEntry.COLUMN_COURSE_TITLE
            )
        coursesCursor = providerCoursesCursor.value
    }


    fun loadNoteData() {

        //Loads Note Data without ContentProvider but SLite
        viewModelScope.launch {

            //loadNoteDataEquinox()
            loadNoteContent()
        }
    }

    private suspend fun loadNoteContent() {
        val noteUri = Notes.CONTENT_URI

        providerNotesCursor.value =
            providerClient?.query(
                noteUri,
                noteColumns,
                "${BaseColumns._ID} = ?",
                arrayOf(Integer.toString(noteId)),
                NoteInfoEntry.COLUMN_NOTE_TITLE
            )
        notesCursor = providerNotesCursor.value

        loadNoteDataProvider()
    }

    private suspend fun loadNoteDataProvider() {
        val courseIdPos =
            notesCursor?.getColumnIndex(NoteInfoEntry.COLUMN_COURSE_ID)!!
        val noteTitlePos =
            notesCursor?.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TITLE)!!
        val noteTextPos =
            notesCursor?.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TEXT)!!

        //When a cursor is returned its position before the first row in the result to move specifically to that row
        notesCursor!!.moveToNext()

        displayNote(notesCursor!!, courseIdPos, noteTitlePos, noteTextPos)

    }

    private suspend fun loadNoteDataEquinox() {

        val db = dbOpenHelper.getReadableDatabase()

        /*
    val courseId = "android_intents"
    val titleStart = "%Delegating%"
    */
        /*
    val selection =
        "${NoteInfoEntry.COLUMN_COURSE_ID} = ? AND ${NoteInfoEntry.COLUMN_NOTE_TITLE} LIKE ?"
    val selectionArgs = arrayOf(courseId, titleStart)
    */

        val selection = "${BaseColumns._ID} = ?"
        val selectionArgs = arrayOf(Integer.toString(noteId))


        val noteColumns = arrayOf(
            NoteInfoEntry.COLUMN_NOTE_TITLE,
            NoteInfoEntry.COLUMN_NOTE_TEXT,
            NoteInfoEntry.COLUMN_COURSE_ID
        )

        val noteCursor = db.query(
            NoteInfoEntry.TABLE_NAME,
            noteColumns,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        val courseIdPos =
            noteCursor?.getColumnIndex(NoteInfoEntry.COLUMN_COURSE_ID)!!
        val noteTitlePos =
            noteCursor?.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TITLE)!!
        val noteTextPos =
            noteCursor?.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TEXT)!!

        //When a cursor is returned its position before the first row in the result to move specifically to that row
        noteCursor!!.moveToNext()

        displayNote(noteCursor, courseIdPos, noteTitlePos, noteTextPos)

    }

    private fun displayNote(
        noteCursor: Cursor,
        courseIdPos: Int,
        noteTitlePos: Int,
        noteTextPos: Int
    ) {
        val courseId = noteCursor.getString(courseIdPos)
        noteTitle.value = noteCursor.getString(noteTitlePos)
        noteText.value = noteCursor.getString(noteTextPos)


        //This time, course is obtained from the corresponding id that was read for this note from the database
        // val courses: List<CourseInfo> = DataManager.instance!!.courses
        //val course = courseId?.let { DataManager.instance?.getCourse(it) }

        // .. and its index obtained with respect Data binding List of RecyclerView using it
        //val courseIndex = courses.indexOf(noteInfo?.course)

        //val courseIndex = courses.indexOf(course)

        courseIndex.value = getIndexOfCourseId(courseId)

        //spinner.setSelection(courseIndex)
        //textNoteTitle.setText(noteInfo?.title)
        //textNoteText.setText(noteInfo?.text)

        //spinnerCourses.setSelection(courseIndex)
        //textNoteTitle.setText(noteTitle)
        //textNoteText.setText(noteText)
    }

    private fun getIndexOfCourseId(courseId: String?): Int {
        val cursor = providerCoursesCursor.value
        val currentIdPos =
            cursor!!.getColumnIndex(CourseInfoEntry.COLUMN_COURSE_ID)
        var courseRowIndex = 0
        var isFound = false
        var more = cursor?.moveToFirst()
        while (more) {
            val cursorCourseId = cursor.getString(currentIdPos)
            courseId?.let { if (it.equals(cursorCourseId)) isFound = true }

            if (isFound)
                break
            else
                courseRowIndex++
            more = cursor.moveToNext()
        }
        return courseRowIndex
    }
}
