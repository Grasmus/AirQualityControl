package com.airqualitycontrol.common.utils

import android.os.Parcel
import android.os.Parcelable
import com.google.android.material.datepicker.CalendarConstraints
import java.util.Date

class GraphDateValidator() : CalendarConstraints.DateValidator {

    constructor(parcel: Parcel) : this()

    override fun isValid(dateMillis: Long): Boolean {
        return dateMillis < Date().time
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {}

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR: Parcelable.Creator<GraphDateValidator> {
        override fun createFromParcel(parcel: Parcel): GraphDateValidator {
            return GraphDateValidator(parcel)
        }

        override fun newArray(size: Int): Array<GraphDateValidator?> {
            return newArray(size)
        }
    }
}
