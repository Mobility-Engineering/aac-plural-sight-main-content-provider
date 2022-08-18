package com.dexcom.sdk.aac_fullcontentapp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.ContentUris
import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.BaseColumns
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.SimpleCursorAdapter
import android.widget.Spinner
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.work.*
import com.dexcom.sdk.aac_fullcontentapp.database.NoteKeeperDatabaseContract.CourseInfoEntry
import com.dexcom.sdk.aac_fullcontentapp.database.NoteKeeperDatabaseContract.NoteInfoEntry
import com.dexcom.sdk.aac_fullcontentapp.database.NoteKeeperOpenHelper
import com.dexcom.sdk.aac_fullcontentapp.databinding.ActivityMainBinding
import com.dexcom.sdk.aac_fullcontentapp.provider.NoteKeeperProviderContract.Notes
import com.dexcom.sdk.aac_fullcontentapp.service.NoteBackup
import com.dexcom.sdk.aac_fullcontentapp.service.NoteBackup.ALL_COURSES
import com.dexcom.sdk.aac_fullcontentapp.service.NoteBackupService
import com.dexcom.sdk.aac_fullcontentapp.service.NoteBackupService.Companion.EXTRA_COURSE_ID
import java.time.Duration
import java.util.concurrent.TimeUnit


class NoteActivity : AppCompatActivity() {
    private val CHANNEL_ID: String = "1"
    private var noteCursor: Cursor? = null
    private var noteTextPos: Int = 0
    private var noteTitlePos: Int = 0
    private var courseIdPos: Int = 0
    private var id: Int = 0
    private var noteUri: Uri? = null

    /*private var originalNoteText: String? = null
            private var orginalNoteTitle: String? = null
            private var originalNoteCourseId: String? = "" */
    private var shouldFinish: Boolean = false
    private var notePosition: Int = 0
    private var isNewNote: Boolean = false
    private lateinit var noteInfo: NoteInfo
    private lateinit var textNoteTitle: EditText
    private lateinit var textNoteText: EditText
    private lateinit var spinnerCourses: Spinner
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: NoteActivityViewModel
    lateinit var dbOpenHelper: NoteKeeperOpenHelper
    private lateinit var adapterCourses: SimpleCursorAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        dbOpenHelper = NoteKeeperOpenHelper(this)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        viewModel = ViewModelProvider(this).get(NoteActivityViewModel::class.java)


        val viewArray: IntArray? = IntArray(1) { i -> android.R.id.text1 }
        binding.spinnerCourses.also { spinnerCourses = it }
        val courses: List<CourseInfo> = DataManager.instance!!.courses


        viewModel.noteText.observe(this, { noteText -> textNoteText.setText(noteText) })
        viewModel.noteTitle.observe(this, { noteTitle -> textNoteTitle.setText(noteTitle) })
        viewModel.courseIndex.observe(this,
            { courseIndex -> spinnerCourses.setSelection(courseIndex) })
        viewModel.providerCoursesCursor.observe(
            this,
            { cursor -> adapterCourses.changeCursor(cursor) })
        //viewModel.noteUriPublisher.observe(this, { uri -> id = ContentUris.parseId(uri).toInt() })
        adapterCourses = SimpleCursorAdapter(
            this,
            android.R.layout.simple_spinner_item,
            null,
            arrayOf(CourseInfoEntry.COLUMN_COURSE_TITLE),
            viewArray,
            0
        ) //ArrayAdapter<CourseInfo> =
        //ArrayAdapter(this, android.R.layout.simple_spinner_item, courses)
        adapterCourses.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        spinnerCourses.adapter = adapterCourses
        viewModel.loadCourseData()

        if (viewModel.isNewlyCreated && savedInstanceState !== null) { //being equal to null implies that the activity was created for the first time, when that is not the case simply
            //restore viewModel state from Bundle
            //This is an overkill as when it is destroyed onConfigurationChanges viewModel will already hold the used value
            viewModel.restoreState(savedInstanceState)
        }
        viewModel.isNewlyCreated = false


        textNoteTitle = binding.textNoteTitle
        textNoteText = binding.textNodeText

        readDisplayStates() //get noteInfo in order to populate the Views
        if (!isNewNote) {
            //loadNoteData()
            viewModel.loadNoteData()
            //supportLoaderManager
            //loaderManager
        }

        saveOriginalNoteValues() //Store backup of original noteInfo values used onCancel()

    }


    private fun loadCourseData() {
        //Loads the Course Titles on Spinner from Database
        //It is needed by viewModel.loadNoteData()
        val db = dbOpenHelper.getReadableDatabase()
        val courseColumns = arrayOf(
            CourseInfoEntry.COLUMN_COURSE_ID,
            CourseInfoEntry.COLUMN_COURSE_TITLE,
            BaseColumns._ID
        )
        val cursor = db.query(
            CourseInfoEntry.TABLE_NAME,
            courseColumns,
            null,
            null,
            null,
            null,
            CourseInfoEntry.COLUMN_COURSE_TITLE
        )
        adapterCourses.changeCursor(cursor)
        viewModel.coursesCursor = cursor
    }


    private fun loadNoteData() {
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
        val selectionArgs = arrayOf(Integer.toString(id))


        val noteColumns = arrayOf(
            NoteInfoEntry.COLUMN_NOTE_TITLE,
            NoteInfoEntry.COLUMN_NOTE_TEXT,
            NoteInfoEntry.COLUMN_COURSE_ID
        )

        noteCursor = db.query(
            NoteInfoEntry.TABLE_NAME,
            noteColumns,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        courseIdPos = noteCursor?.getColumnIndex(NoteInfoEntry.COLUMN_COURSE_ID)!!
        noteTitlePos = noteCursor?.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TITLE)!!
        noteTextPos = noteCursor?.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TEXT)!!

        //When a cursor is returned its position before the first row in the result to move specifically to that row
        noteCursor!!.moveToNext()

        displayNote()
    }

    private fun saveOriginalNoteValues() {
        if (isNewNote)
            return
        viewModel.originalNoteCourseId = noteInfo.course?.courseId
        viewModel.originalNoteTitle = noteInfo.title
        viewModel.originalNoteText = noteInfo.text
    }

    private fun displayNote() {
        val courseId = noteCursor?.getString(courseIdPos)
        val noteTitle = noteCursor?.getString(noteTitlePos)
        val noteText = noteCursor?.getString(noteTextPos)


        //This time, course is obtained from the corresponding id that was read for this note from the database
        // val courses: List<CourseInfo> = DataManager.instance!!.courses
        //val course = courseId?.let { DataManager.instance?.getCourse(it) }

        // .. and its index obtained with respect Data binding List of RecyclerView using it
        //val courseIndex = courses.indexOf(noteInfo?.course)

        //val courseIndex = courses.indexOf(course)

        val courseIndex = getIndexOfCourseId(courseId)

        //spinner.setSelection(courseIndex)
        //textNoteTitle.setText(noteInfo?.title)
        //textNoteText.setText(noteInfo?.text)


        //Dispatchers.IO implemention using Observers is current implementation
        spinnerCourses.setSelection(courseIndex)
        textNoteTitle.setText(noteTitle)
        textNoteText.setText(noteText)

    }

    private fun  getIndexOfCourseId(courseId: String?): Int {
        val cursor = adapterCourses.cursor
        //viewModel.coursesCursor = cursor
        val currentIdPos = cursor.getColumnIndex(CourseInfoEntry.COLUMN_COURSE_ID)
        var courseRowIndex = 0
        var isFound = false
        var more = cursor.moveToFirst()
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


    override fun onPause() {
        super.onPause()
        if (shouldFinish) {
            if (isNewNote) {
                DataManager.instance?.removeNote(notePosition)
                val selection = BaseColumns._ID + " =?"
                //deleteNodeFromDatabase()
                //viewModel.deleteNote()
            } else {
                storePreviousNoteValues()
            }
        } else
            saveNote()
    }

    private fun deleteNodeFromDatabase() {

        val selection = BaseColumns._ID + " =?"
        val selectionArgs = arrayOf(id.toString())
        val db = dbOpenHelper.writableDatabase
        db.delete(NoteInfoEntry.TABLE_NAME, selection, selectionArgs)

        /* AsyncTask is deprecated in API 30 move to other imp
        val task:AsyncTask  AsyncTask(){
            @Override
            protected doInBackground(params:Object){

            }
       }
        db.delete(NoteInfoEntry.TABLE_NAME, selection, selectionArgs)
        */
    }

    private fun storePreviousNoteValues() {
        //Save the actual data that lays behind courtains nbut keep showing new values on display
        //as it will update them back from Spinner and EditText when saveNote()
        val course = viewModel.originalNoteCourseId?.let { DataManager.instance?.getCourse(it) }
        noteInfo.course = course
        noteInfo.text = viewModel.originalNoteText
        noteInfo.title = viewModel.originalNoteTitle
    }

    private fun saveNote() {
        //noteInfo?.course = spinnerCourses.selectedItem as CourseInfo
        //When using the DataManager for in base memory storage this will be enough to update the DataManager
        //and will hold the values for the current session. (DataManager is recreated every time the application reloads

        //Commented lines due to SQLite integration i.e Database persistance over local
        //noteInfo?.title = textNoteTitle?.getText().toString()
        //noteInfo?.text = textNoteText?.getText().toString()

        val courseId = selectedCourseId()
        val noteTitle = textNoteTitle.text.toString()
        val noteText = textNoteText.text.toString()
        saveNoteToDatabase(courseId, noteTitle, noteText)
    }

    private fun selectedCourseId(): String {
        val selectedPosition = spinnerCourses.selectedItemPosition
        val cursor = adapterCourses.cursor
        cursor.moveToPosition(selectedPosition) //Cursor does not need to know about the values themselves but as the selectedItemPosition and its associated
        //value on the cursor suffices
        val courseIdPos = cursor.getColumnIndex(CourseInfoEntry.COLUMN_COURSE_ID)
        return cursor.getString(courseIdPos)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewModel.isNewlyCreated = false
    }

    private fun saveNoteToDatabase(courseId: String?, noteTitle: String?, noteText: String?) {

        val db = dbOpenHelper.writableDatabase

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
        val selectionArgs = arrayOf(Integer.toString(id))


        val values = ContentValues()
        values.put(NoteInfoEntry.COLUMN_NOTE_TITLE, noteTitle)
        values.put(NoteInfoEntry.COLUMN_NOTE_TEXT, noteText)
        values.put(NoteInfoEntry.COLUMN_COURSE_ID, courseId)
        db.update(NoteInfoEntry.TABLE_NAME, values, selection, selectionArgs)
    }

    private fun readDisplayStates() {
        val intent = getIntent()
        //val position = intent.getIntExtra(NOTE_POSITION, POSITION_NOT_SET)
        id = intent.getIntExtra(NOTE_ID, ID_NOT_SET)
        viewModel.noteId = id
        viewModel.noteUri = ContentUris.withAppendedId(Notes.CONTENT_URI, id.toLong())
        isNewNote = id == ID_NOT_SET
        //App Step - Create a new node
        if (isNewNote) {
            createNewNote()
        }
        //No longer needed as it will be accesed directly from Database knowing the  ccrresponding id needed
        /*
        for (note in DataManager.instance!!.notes) {
            //DataManager.instance!!.notes.map{it.id}.toSet().filter{}

            if (note.id == id) {
                noteInfo = note
                break
            }
        }
         */
        else {
            noteInfo = DataManager.instance!!.notes.first { it.id == id }
        }
        //noteInfo = DataManager.instance!!.notes[position]
        //App step->Parcel extra removal
        //noteInfo = intent.getParcelableExtra<NoteInfo>(Companion.NOTE_INFO)
    }

    private fun createNewNote() {
        //SQLite integration no longer needs Data Manager to create note locally i.e NoteActivity RecyclerView populated from DB
        /*
        val dm = DataManager.instance
        notePosition = dm!!.createNewNote()
        noteInfo = dm?.notes?.get(notePosition)
        */
        /* Replaced by ContetProvider insertion
        val values = ContentValues()
        values.put(NoteInfoEntry.COLUMN_NOTE_TITLE, "")
        values.put(NoteInfoEntry.COLUMN_NOTE_TEXT, "")
        values.put(NoteInfoEntry.COLUMN_COURSE_ID, "")
        val db = dbOpenHelper.writableDatabase
        id = db.insert(NoteInfoEntry.TABLE_NAME, null, values).toInt()
        */

        viewModel.insertNote()
    }

    private class CreateNewNoteWithAsyncTask() {


    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_note, menu)
        return true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId
        when (id) {
            R.id.action_send_mail -> {
                sendMail()
                return true
            }
            R.id.action_cancel -> {
                shouldFinish = true
                //deleteNoromDatabase()
                viewModel.deleteNote()
                finish()
            }
            R.id.action_notify -> {
                showReminderNotification()
                return true

            }
            R.id.action_note_backup -> {
                //noteBackup()
                workManagerNoteBackup()
                return true
            }
        }
        return false

    }

    private fun noteBackup() {
        NoteBackup.doBackup(this, ALL_COURSES)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun  workManagerNoteBackup(){
        //val course = "android_async"
        /*val intent:Intent? = Intent(this, NoteBackupService::class.java)
        intent?.putExtra( EXTRA_COURSE_ID, ALL_COURSES)*/
        // Create the Constraints
        val constraints = Constraints.Builder()
            .build()

// Define the input

        val cpData = workDataOf(EXTRA_COURSE_ID to ALL_COURSES)

// Bring it all together by creating the WorkRequest; this also sets the back off criteria
            val backupNoteRequest = OneTimeWorkRequestBuilder<NoteBackupService>()
            .setInputData(cpData)
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(applicationContext).enqueue(backupNoteRequest)

    }

    private fun showReminderNotification() {

        val title = binding.textNoteTitle.text.toString()
        val text = binding.textNodeText.text.toString()
        var builder = NotificationCompat.Builder(this, CHANNEL_ID)
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
            val name = getString(R.string.chanel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel =
                NotificationChannel(getString(R.string.channel_id), name, importance).apply {
                    description = descriptionText
                }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        //Create an explicit intent for the activity

        val pendingIntent = createPendingIntent()
        builder.setContentIntent(pendingIntent)
            .setChannelId(getString(R.string.channel_id))
            .setAutoCancel(true)


        builder = addAction(builder)
        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(0, builder.build())
        }
    }

    private fun createPendingIntent(): PendingIntent {
        //don't use intent activity atributte as it will be set to null once the activity is destroyed
        val intent = Intent(applicationContext, NoteActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val id = viewModel.noteId
        intent.putExtra(NOTE_ID, id)
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(this, 0, intent, FLAG_UPDATE_CURRENT)
        return pendingIntent
    }

    private fun addAction(builder: NotificationCompat.Builder): NotificationCompat.Builder {
        builder.addAction(
            0,
            "View all notes",
            PendingIntent.getActivity(
                this,
                0,
                Intent(this, NavigationDrawerActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                },
                FLAG_UPDATE_CURRENT
            )
        )
        return builder
    }


    private fun sendMail() {
        var course = spinnerCourses.selectedItem as CourseInfo
        var subject = textNoteTitle.text.toString()
        var text =
            "Checkout what I learned in the Pluralsight course \"" + course.title + "\"\n" + textNoteText.text.toString()
        val intent = Intent(Intent.ACTION_SEND)
        intent.setType("message/rfc2822")
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        intent.putExtra(Intent.EXTRA_TEXT, text)
        startActivity(intent)
    }

    companion object {
        const val NOTE_INFO = "NOTE_INFO"
        const val NOTE_ID = "NOTE_POSITION"
        const val ID_NOT_SET = -1
    }
}