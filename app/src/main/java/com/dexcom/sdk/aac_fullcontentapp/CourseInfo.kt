package com.dexcom.sdk.aac_fullcontentapp

import android.os.Parcelable
import android.os.Parcel
import com.dexcom.sdk.aac_fullcontentapp.CourseInfo
import java.util.ArrayList

class CourseInfo : Parcelable {
    val courseId: String?
    val title: String
    val modules: List<ModuleInfo?>?

    constructor(courseId: String?, title: String, modules: List<ModuleInfo?>?) {
        this.courseId = courseId
        this.title = title
        this.modules = modules
    }

    private constructor(source: Parcel) {
        courseId = source.readString()
        title = source.readString()!!
        modules = ArrayList<ModuleInfo?>()
        //this is a list that can be nullable
        modules?.let {
            source.readTypedList(modules, ModuleInfo.CREATOR)
        }
    }

    var modulesCompletionStatus: BooleanArray?
        get() {
            val status = modules?.let { BooleanArray(it.size) }
            for (i in modules?.indices!!) modules?.get(i)?.let { status?.set(i, it.isComplete) }
            return status
        }
        set(status) {
            for (i in modules?.indices!!) modules?.get(i)?.isComplete = status?.get(i) == true
        }

    fun getModule(moduleId: String): ModuleInfo? {
        if (modules != null) {
            for (moduleInfo in modules) {
                if (moduleInfo != null) {
                    if (moduleId == moduleInfo.moduleId) return moduleInfo
                }
            }
        }
        return null
    }

    override fun toString(): String {
        return title
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as CourseInfo
        return courseId == that.courseId
    }

    override fun hashCode(): Int {
        return courseId.hashCode()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(courseId)
        dest.writeString(title)
        dest.writeTypedList(modules)
    }

    companion object CREATOR : Parcelable.Creator<CourseInfo?> {
        override fun createFromParcel(source: Parcel): CourseInfo? {
            return CourseInfo(source)
        }

        override fun newArray(size: Int): Array<CourseInfo?> {
            return arrayOfNulls(size)
        }
    }
}
