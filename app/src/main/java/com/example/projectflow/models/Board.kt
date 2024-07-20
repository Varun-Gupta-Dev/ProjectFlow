package com.example.projectflow.models

import android.os.Parcel
import android.os.Parcelable

data class Board(
    val name: String = "",
    val image: String = "",
    val createdBy: String = "",
    val createdOn: String = "",
    val assignedTo: ArrayList<String> = ArrayList(),
    var documentId: String = "",
   var taskList: ArrayList<Task> = ArrayList()
) : Parcelable{
    //Indicates that the Boards class implements the Parcelable interface. This is essential for passing instances of this
    // class between different components of an Android application, such as activities or fragments,
    // using Intents or Bundles.
//    Parcel: A Parcel is a container for a message (data and object references) that can be sent through an IBinder.
    //    It's used for inter-process communication (IPC) in Android, such as when passing
    //    data between activities or services.
    constructor( parcel: Parcel): this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()!!,
        parcel.readString()!!,
        parcel.createTypedArrayList(Task.CREATOR)!!
    ){

    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(name)
        writeString(image)
        writeString(createdBy)
        writeStringList(assignedTo)
        writeString(documentId)
        writeTypedList(taskList)
    }

    companion object CREATOR : Parcelable.Creator<Board> {
        override fun createFromParcel(parcel: Parcel): Board {
            return Board(parcel)
        }

        override fun newArray(size: Int): Array<Board?> {
            return arrayOfNulls(size)
        }
    }
}