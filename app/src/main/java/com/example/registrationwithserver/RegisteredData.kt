package com.example.registrationwithserver

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RegisteredData(
    var imageType: String,
    var photoPath: String,
    var name: String,
    var email: String,
    var phoneNumber: String,
    var city: String
) : Parcelable