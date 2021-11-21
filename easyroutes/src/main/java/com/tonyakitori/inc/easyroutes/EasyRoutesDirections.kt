package com.tonyakitori.inc.easyroutes

import com.google.android.gms.maps.model.LatLng
import com.tonyakitori.inc.easyroutes.enums.TransportationMode

data class EasyRoutesDirections(
    var apiKey: String,
    var originLatLng: LatLng? = null,
    var originPlace: String? = null,

    var destinationLatLng: LatLng? = null,
    var destinationPlace: String? = null,

    var waypointsLatLng: ArrayList<LatLng> = arrayListOf(),
    var waypointsPlaces: ArrayList<String> = arrayListOf(),
    var transportationMode: TransportationMode = TransportationMode.DRIVING,

    var showDefaultMarkers: Boolean = true,
)