package com.tonyakitori.inc.easyroutes.rest

import com.google.android.gms.maps.model.LatLng
import com.google.gson.GsonBuilder
import com.tonyakitori.inc.easyroutes.EasyRoutesDirections
import com.tonyakitori.inc.easyroutes.model.Directions
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.ArrayList

class DirectionsRestImp() : DirectionsRest {

    private val okHttpClient = OkHttpClient()

    override suspend fun getDirections(easyRoutesDirections: EasyRoutesDirections): Directions {

        val gson = GsonBuilder().create()
        val data = callDirections(easyRoutesDirections)

        return gson.fromJson<Any>(data, Directions::class.java) as Directions
    }


    private fun callDirections(
        easyRoutesDirections: EasyRoutesDirections
    ): String? {
        var result: String? = null
        try {
            var url = "https://maps.googleapis.com/maps/api/directions/json?"

            val (
                apiKey,
                originLatLng,
                originPlace,
                destinationLatLng,
                destinationPlace,
                waypointsLatLng,
                waypointsPlaces,
                transportationMode
            ) = easyRoutesDirections

            url += handleOrigin(originLatLng, originPlace)
            url += handleDestination(destinationLatLng, destinationPlace)
            url += "&mode=${transportationMode.apiName}"
            url += handleWayPoints(waypointsLatLng, waypointsPlaces)


            url += "&key=${apiKey}"

            val request = Request.Builder()
                .url(url)
                .build()

            val response = okHttpClient.newCall(request).execute()

            result = response.body?.string()
        } catch (e: Error) {
            println("Error in get directions")
        }

        return result
    }

    private fun handleOrigin(originLatLng: LatLng?, originPlace: String?): String{
        var origin = ""

        if((originLatLng == null && originPlace.isNullOrBlank())){
            throw Exception("Set a origin is required")
        }

        if(originLatLng != null && originPlace.isNullOrBlank().not()){
            throw Exception("Select only one origin")
        }

        if(originLatLng != null){
            origin += "origin=${originLatLng.latitude}, ${originLatLng.longitude}"
        }

        if (originPlace.isNullOrBlank().not()){
            origin += "origin=${originPlace}"
        }

        return origin
    }

    private fun handleDestination(destinationLatLng: LatLng?, destinationPlace: String?): String{

        var destination = ""

        if((destinationLatLng == null && destinationPlace.isNullOrBlank())){
            throw Exception("Set a destination is required")
        }

        if(destinationLatLng != null && destinationPlace.isNullOrBlank().not()){
            throw Exception("Select only one destination")
        }

        if(destinationLatLng != null){
            destination += "&destination=${destinationLatLng.latitude}, ${destinationLatLng.longitude}"
        }

        if (destinationPlace.isNullOrBlank().not()){
            destination += "&destination=${destinationPlace}"
        }

        return destination
    }

    private fun handleWayPoints(waypointsLatLng: ArrayList<LatLng>, waypointsPlaces: ArrayList<String>): String {
        var wayPoints = ""

        val wayPointsStringList = arrayListOf<String>()

        if(waypointsLatLng.isNotEmpty()){
            waypointsLatLng.forEach {
                wayPointsStringList.add("${it.latitude},${it.longitude}")
            }
        }

        if(waypointsPlaces.isNotEmpty()){
            wayPointsStringList.addAll(waypointsPlaces)
        }

        if(wayPointsStringList.isNotEmpty()){
            wayPoints += "&waypoints="
        }

        wayPointsStringList.forEachIndexed { index, s ->
            wayPoints += if(index == wayPointsStringList.lastIndex){
                s
            }else{
                "$s|"
            }
        }

        return wayPoints
    }

}