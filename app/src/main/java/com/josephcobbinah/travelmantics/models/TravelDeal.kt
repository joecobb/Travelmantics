package com.josephcobbinah.travelmantics.models

import java.io.Serializable

data class TravelDeal(
    var title: String?="",
    var description: String?="",
    var price: String?="",
    var imageUrl: String?="",
    var imageName: String?="",
    var id:String?=""
):Serializable