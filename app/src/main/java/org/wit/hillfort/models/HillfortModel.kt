package org.wit.hillfort.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class HillfortModel(var id: Long = 0,
                         var name: String = "",
                         var description: String = "",
                         var images: ArrayList<String> = ArrayList(),
                         var lat : Double = 0.0,
                         var lng: Double = 0.0,
                         var zoom: Float = 0f,
                         var visited: Boolean = false,
                         var dateVisited: String = "",
                         var notes : String = ""):Parcelable

@Parcelize
data class Location(var lat: Double = 0.0,
                    var lng: Double = 0.0,
                    var zoom: Float = 0f) : Parcelable