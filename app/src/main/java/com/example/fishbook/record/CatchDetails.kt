package com.example.fishbook.record
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CatchDetails(
    var species: String = "",
    var lake: String = "",
    var length: Float = 0.0F,
    var weight: Float = 0.0F,
    var county: String = "",
    var lure: String = "",
    var time: String = "",
    var location: String = "",
    var remoteUri: String = "",
    var localUri: String = ""
) : Parcelable
