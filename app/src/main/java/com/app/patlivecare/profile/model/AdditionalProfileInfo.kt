package com.app.patlivecare.profile.model

import com.google.gson.annotations.SerializedName

data class AdditionalProfileInfo(
@SerializedName("bloodGroup") var bloodGroup: String="",
@SerializedName("height") var height: Float=0.0f,
@SerializedName("isVegetarian") var isVegetarian: Boolean=false,
@SerializedName("useAlcohol") var useAlcohol: Boolean=false,
@SerializedName("useDrug") var useDrug: Boolean=false,
@SerializedName("useSmoke") var useSmoke: Boolean=false,
@SerializedName("weight") var weight:Float=0.0f
)