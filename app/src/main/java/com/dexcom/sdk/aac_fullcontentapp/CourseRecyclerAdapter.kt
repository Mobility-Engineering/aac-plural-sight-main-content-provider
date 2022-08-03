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
import com.google.android.material.snackbar.Snackbar
import kotlin.coroutines.coroutineContext


class CourseRecyclerAdapter(private var context: Context, private var courses: List<CourseInfo>) :
    RecyclerView.Adapter<CourseRecyclerAdapter.ViewHolder>() {
    private var currentPosition: Int = 0
    private var layoutInflater: LayoutInflater

    init {
        layoutInflater = LayoutInflater.from(context)
    }

     inner class ViewHolder : RecyclerView.ViewHolder {

        var textTitle: TextView?
        var textCourse: TextView?

        constructor(itemView: View) : super(itemView) {
          textCourse = itemView.findViewById<TextView>(R.id.text_course)
          textTitle = itemView.findViewById<TextView>(R.id.text_title)
            itemView.setOnClickListener(View.OnClickListener { view ->
                run {

                    //val intent = Intent(context, NoteActivity::class.java)
                    //(context as NavigationDrawerActivity).navController.navigate(R.id.action_nav_home_to_nav_gallery)
                    //intent.putExtra(NOTE_POSITION, adapterPosition)
                    //context.startActivity(intent)

                    Snackbar.make(view, courses.get(getAdapterPosition()).title, Snackbar.LENGTH_LONG).show()
                }
            })
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
        layoutInflater  =LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.item_course_list, parent, false))
    }

    override fun onBindViewHolder(holder:ViewHolder, position:Int){
    val course = courses.get(position)
        holder.textTitle?.setText(course.title)
    }

    override fun getItemCount():Int{
    return courses.size
    }
    companion object{
        const val NOTE_POSITION = "NOTE_POSITION"
    }
    }
