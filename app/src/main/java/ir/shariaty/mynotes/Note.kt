package ir.shariaty.mynotes


import android.os.Parcel
import android.os.Parcelable

data class Note(
    val title: String,
    val content: String,
    var documentId: String = "",
    var title: String = "",
    var content: String = "",
    var userId: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(documentId)
        parcel.writeString(title)
        parcel.writeString(content)
        parcel.writeString(userId)
    }

    override fun describeContents(): Int {
        return 0
    }
    companion object CREATOR : Parcelable.Creator<Note> {
        override fun createFromParcel(parcel: Parcel): Note {
            return Note(parcel)
        }
        override fun newArray(size: Int): Array<Note?> {
            return arrayOfNulls(size)
        }
    }
}