package com.example.fishbook.record
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CatchDetails(
    val id: String = "",
    var species: String = "",
    var lake: String = "",
    var length: String = "",
    var weight: String = "",
    var county: String = "",
    var lure: String = "",
    var time: String = "",
    var latitude: String = "",
    var longitude: String = "",
    var location: String = "",
    var remoteUri: String = "",
    var localUri: String = ""
) : Parcelable
