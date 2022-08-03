package com.dexcom.sdk.aac_fullcontentapp

import android.content.Context
import android.content.Intent
import android.provider.ContactsContract
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlin.coroutines.coroutineContext


class NoteRecyclerAdapter: RecyclerView.Adapter<NoteRecyclerAdapter.ViewHolder> {
    private var id: Int = 0
    private var layoutInflater: LayoutInflater
    private var notes:List<NoteInfo>
    private var context:Context
    constructor(context:Context, notes: List<NoteInfo>){
        this.notes = notes
        this.context = context
        layoutInflater = LayoutInflater.from(context)
    }

     inner class ViewHolder : RecyclerView.ViewHolder {

        var textTitle: TextView?
        var textCourse: TextView?
        var id:Int = 0

         constructor(itemView: View) : super(itemView) {
          textCourse = itemView.findViewById<TextView>(R.id.text_course)
          textTitle = itemView.findViewById<TextView>(R.id.text_title)
            itemView.setOnClickListener(View.OnClickListener { view ->
                run {

                   val intent = Intent(context, NoteActivity::class.java)
                    //(context as NavigationDrawerActivity).navController.navigate(R.id.action_nav_home_to_nav_gallery)
                    intent.putExtra(NOTE_POSITION, id)
                    DataManager!!.instance?.currentlyDisplayedNote =  adapterPosition
                    context.startActivity(intent)
                }
            })
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
        layoutInflater  =LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.item_note_list, parent, false))
    }

    override fun onBindViewHolder(holder:ViewHolder, position:Int){
    val note = notes.get(position)
        holder.textCourse?.setText(note.course?.title)
        holder.textTitle?.setText(note.title)
        holder.id = note.id
    }

    override fun getItemCount():Int{
    return notes.size
    }
    companion object{
        const val NOTE_POSITION = "NOTE_POSITION"
    }
}
