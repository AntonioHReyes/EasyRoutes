package com.tonyakitori.inc.easyroutes.extensions

import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.tonyakitori.inc.easyroutes.EasyRoutesDirections
import com.tonyakitori.inc.easyroutes.EasyRoutesDrawer
import com.tonyakitori.inc.easyroutes.R
import com.tonyakitori.inc.easyroutes.model.Distance
import com.tonyakitori.inc.easyroutes.model.Duration
import com.tonyakitori.inc.easyroutes.model.LegsItem
import com.tonyakitori.inc.easyroutes.model.RoutesItem
import com.tonyakitori.inc.easyroutes.rest.DirectionsRestImp
import com.tonyakitori.inc.easyroutes.utils.MarkerUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun GoogleMap.drawRoute(
    context: Context,
    easyRoutesDirections: EasyRoutesDirections,
    routeDrawer: EasyRoutesDrawer = EasyRoutesDrawer.Builder(this)
        .pathWidth(12f)
        .build(),
    markersListCallback: ((markers: List<Marker>) -> Unit)? = null,
    legsCallback: ((legs: List<LegsItem?>?) -> Unit)? = null
) {


    CoroutineScope(Dispatchers.Default).launch {
        try {
            val directions = DirectionsRestImp().getDirections(easyRoutesDirections)
            val routes = directions.routes

            withContext(Dispatchers.Main) {
                if (routes.isNullOrEmpty().not()) {

                    routeDrawer.drawPath(directions)

                    if(easyRoutesDirections.showDefaultMarkers){
                        val markers = this@drawRoute.handleLegsDirections(context, routes)

                        if(markersListCallback != null){
                            markersListCallback(markers)
                        }
                    }
                }
            }

            if (routes.isNullOrEmpty().not()) {
                if (routes?.get(0)?.legs.isNullOrEmpty().not()) {
                    if (legsCallback != null) {
                        legsCallback(routes?.get(0)?.legs)
                    }
                }
            }


        } catch (e: Exception) {
            Log.e("EasyRoutesError", "${e.message}")
        }

    }
}

private fun GoogleMap.handleLegsDirections(
    context: Context,
    routes: List<RoutesItem?>?
): ArrayList<Marker> {
    val markers = arrayListOf<Marker>()
    if (routes?.get(0)?.legs.isNullOrEmpty().not()) {
        val legsLastIndex = routes?.get(0)?.legs?.lastIndex

        if (legsLastIndex == null && legsLastIndex == -1) {
            throw Exception("Error not found legs")
        }

        routes?.get(0)?.legs?.get(0)?.let { legsItem ->
            //draw origin marker
            legsItem.startLocation?.let { startLocation ->
                if (startLocation.lat != null && startLocation.lng != null) {
                    val originMarker = this@handleLegsDirections.drawDefaultMarker(
                        context,
                        LatLng(startLocation.lat, startLocation.lng),
                        title = "Origin",
                        true
                    )

                    originMarker?.let { markers.add(it) }
                }
            }
        }

        routes?.get(0)?.legs?.get(legsLastIndex ?: 0)?.let { legsItem ->
            //draw destination marker
            legsItem.endLocation?.let { endLocation ->
                if (endLocation.lat != null && endLocation.lng != null) {
                    val destinationMarker = this@handleLegsDirections.drawDefaultMarker(
                        context,
                        LatLng(endLocation.lat, endLocation.lng),
                        title = "Destination",
                        true
                    )

                    destinationMarker?.let { markers.add(it) }
                }
            }
        }
    }

    return markers
}

fun GoogleMap.drawDefaultMarker(
    context: Context,
    location: LatLng,
    title: String,
    customDefaultMarker: Boolean = false
): Marker? {
    val defaultMarker = ContextCompat.getDrawable(context, R.drawable.ic_default_marker)
    val markerIcon = MarkerUtils.markerFromDrawable(defaultMarker)

    val markerOptions = MarkerOptions()
        .position(location)
        .title(title)

    if (customDefaultMarker) {
        markerOptions.icon(markerIcon)
    }

    val marker = this.addMarker(
        markerOptions
    )

    marker?.tag = "EasyRoutes"

    return marker
}