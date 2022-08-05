package com.dexcom.sdk.aac_fullcontentapp

import android.os.Parcel
import android.os.Parcelable
import com.dexcom.sdk.aac_fullcontentapp.CourseInfo
import com.dexcom.sdk.aac_fullcontentapp.NoteInfo

class NoteInfo(var id: Int, var course: CourseInfo?, var title: String?, var text: String?) :
    Parcelable {
    private val compareKey: String
        private get() = course?.courseId + "|" + title + "|" + text

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readParcelable(CourseInfo::class.java.classLoader),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as NoteInfo
        return compareKey == that.compareKey
    }

    override fun hashCode(): Int {
        return compareKey.hashCode()
    }

    override fun toString(): String {
        return compareKey
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeParcelable(course, flags)
        parcel.writeString(title)
        parcel.writeString(text)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<NoteInfo> {
        override fun createFromParcel(parcel: Parcel): NoteInfo {
            return NoteInfo(parcel)
        }

        override fun newArray(size: Int): Array<NoteInfo?> {
            return arrayOfNulls(size)
        }
    }
}