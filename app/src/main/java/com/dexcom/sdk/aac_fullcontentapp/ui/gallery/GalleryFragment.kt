package com.dexcom.sdk.aac_fullcontentapp.ui.gallery

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.dexcom.sdk.aac_fullcontentapp.CourseRecyclerAdapter
import com.dexcom.sdk.aac_fullcontentapp.DataManager
import com.dexcom.sdk.aac_fullcontentapp.NoteInfo
import com.dexcom.sdk.aac_fullcontentapp.NoteRecyclerAdapter
import com.dexcom.sdk.aac_fullcontentapp.databinding.FragmentGalleryBinding
import com.dexcom.sdk.aac_fullcontentapp.databinding.FragmentHomeBinding

class GalleryFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    //private var noteRecyclerAdapter: NoteRecyclerAdapter? = null
    private var courseRecyclerAdapter: CourseRecyclerAdapter? = null
    private lateinit var adapterNotes: ArrayAdapter<NoteInfo>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        initializeDisplayContent()
        return root
    }

    override fun onResume() {
        //noteRecyclerAdapter?.notifyDataSetChanged()

        super.onResume()
    }





    private fun initializeDisplayContent() {
        //displayNotes()
        displayCourses()
    }

    /*private fun displayNotes() {
        val context = activity as Context
        val recyclerNotes = binding.listItems
        val notesLayoutManager = LinearLayoutManager(context)
        recyclerNotes.layoutManager = notesLayoutManager
        val notes = DataManager.instance?.notes
        noteRecyclerAdapter = notes?.let{ NoteRecyclerAdapter(context, it) }
        recyclerNotes.adapter = noteRecyclerAdapter
    }*/

    private fun displayCourses() {
        val context = activity as Context
        val recyclerCourses = binding.listItems
        val courseLayoutManager = GridLayoutManager(context, 2)
        recyclerCourses.layoutManager = courseLayoutManager
        val courses = DataManager.instance?.courses
        courseRecyclerAdapter = courses?.let{ CourseRecyclerAdapter(context, it) }
        recyclerCourses.adapter = courseRecyclerAdapter
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}