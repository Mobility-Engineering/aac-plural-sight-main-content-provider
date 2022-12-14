package com.dexcom.sdk.aac_fullcontentapp

import android.os.Parcelable
import kotlin.jvm.JvmOverloads
import android.os.Parcel

class ModuleInfo : Parcelable {
    val moduleId: String?
    val title: String
    var isComplete = false

    @JvmOverloads
    constructor(moduleId: String?, title: String, isComplete: Boolean = false) {
        this.moduleId = moduleId
        this.title = title
        this.isComplete = isComplete
    }

    private constructor(source: Parcel) {
        moduleId = source.readString()
        title = source.readString()!!
        isComplete = source.readByte().toInt() == 1
    }

    override fun toString(): String {
        return title
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as ModuleInfo
        return moduleId == that.moduleId
    }

    override fun hashCode(): Int {
        return moduleId.hashCode()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(moduleId)
        dest.writeString(title)
        dest.writeByte((if (isComplete) 1 else 0).toByte())
    }

    companion object CREATOR:Parcelable.Creator<ModuleInfo?> {
            override fun createFromParcel(source: Parcel): ModuleInfo? {
                return ModuleInfo(source)
            }

            override fun newArray(size: Int): Array<ModuleInfo?> {
                return arrayOfNulls(size)
            }
        }
    }