package com.dexcom.sdk.aac_fullcontentapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Note.NOTE
import android.provider.MediaStore
import android.text.Layout
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavType
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dexcom.sdk.aac_fullcontentapp.databinding.ActivityNoteList2Binding

class NoteListActivity : AppCompatActivity() {


    private var noteRecyclerAdapter: NoteRecyclerAdapter? = null
    private lateinit var adapterNotes: ArrayAdapter<NoteInfo>
    private lateinit var binding: ActivityNoteList2Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        //binding = DataBindingUtil.setContentView(this, R.layout.activity_note_list2)

        binding = ActivityNoteList2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        val fab = binding.floatingActionButton
        super.onCreate(savedInstanceState)
      initializeDisplayContent()
        fab.setOnClickListener {  view ->
            val intent = Intent (this, NoteActivity::class.java)
                startActivity(intent)

        }

    }

    fun initializeDisplayContent(){
        /*
        var listNotes = binding.listNotes
        var notes = DataManager.instance!!.notes
        //adapterNotes = ArrayAdapter(this, android.R.layout.simple_list_item_1, notes)
        listNotes.setAdapter(adapterNotes)
        listNotes.setOnItemClickListener{parent, view, position, id ->
            val intent = Intent(this, NoteActivity::class.java)
            val note = listNotes.getItemAtPosition(position) as NoteInfo
                //App-step: Parcel extra removal intent.putExtra (NoteActivity.NOTE_INFO, note)
                intent.putExtra(NoteActivity.NOTE_POSITION, position)
                    startActivity(intent)

                    */
        val recyclerNotes = binding.listNotes
        val notesLayoutManager = LinearLayoutManager(this)
        recyclerNotes.layoutManager = notesLayoutManager
        val notes = DataManager.instance?.notes
        noteRecyclerAdapter = notes?.let{ NoteRecyclerAdapter(this, it)}
        recyclerNotes.adapter = noteRecyclerAdapter

         }


    override fun onResume() {
           noteRecyclerAdapter?.notifyDataSetChanged()
        super.onResume()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_note, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var id = item.itemId
        when (id){
                R.id.action_take_photo -> {
                    intent = Intent(this, CameraActivity::class.java)
                    startActivity(intent)
             }
            else -> {}
        }
        return super.onOptionsItemSelected(item)
    }

}