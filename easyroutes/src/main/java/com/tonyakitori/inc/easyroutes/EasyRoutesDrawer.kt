package com.tonyakitori.inc.easyroutes

import android.graphics.Color
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import com.tonyakitori.inc.easyroutes.extensions.drawDefaultMarker
import com.tonyakitori.inc.easyroutes.model.Directions

class EasyRoutesDrawer private constructor(builder: Builder) {

    companion object {
        private const val DEFAULT_PATH_WIDTH = 12f
        private const val DEFAULT_PATH_COLOR = Color.MAGENTA
        private const val DEFAULT_GEODESIC = true
        private const val DEFAULT_PREVIEW = true
    }


    private val googleMap: GoogleMap = builder.googleMap
    private val pathWidth: Float = builder.pathWidth
    private val pathColor: Int = builder.pathColor
    private val geodesic: Boolean = builder.geodesic
    private val isPreview: Boolean = builder.isPreview
    private val polylineOptions: PolylineOptions? = builder.polylineOptions

    private var polyline: Polyline? = null


    fun drawPath(directions: Directions){

        val polylineOptions = if(isPreview){
            pathWithoutPrecision(directions)
        }else{
            pathWithPrecision(directions)
        }

        polyline = googleMap.addPolyline(polylineOptions)
        polyline?.tag = "EasyPolyline"
    }

    fun removeRoute(){
        polyline?.remove()
    }

    private fun setUpPolyline(): PolylineOptions {
        val polylineOptions = PolylineOptions()
        polylineOptions.width(pathWidth)
        polylineOptions.geodesic(geodesic)
        polylineOptions.color(pathColor)

        return polylineOptions
    }

    private fun pathWithPrecision(directions: Directions): PolylineOptions{
        val polyline = polylineOptions ?: setUpPolyline()

        for(route in directions.routes ?: listOf()){
            for(legs in route?.legs ?: listOf()){
                for(step in legs?.steps ?: listOf()){

                    step?.polyline?.points?.let {
                        val pointsList = PolyUtil.decode(it)
                        for (point in pointsList) {
                            polyline.add(point)
                        }
                    }

                }
            }
        }

        return polyline
    }

    private fun pathWithoutPrecision(directions: Directions): PolylineOptions{
        val polyline = polylineOptions ?: setUpPolyline()

        for(route in directions.routes ?: listOf()){
            route?.overviewPolyline?.let {
                val pointsList = PolyUtil.decode(it.points)
                for (point in pointsList) {
                    polyline.add(point)
                }
            }
        }

        return polyline
    }


    class Builder(
        internal val googleMap: GoogleMap,
        internal val polylineOptions: PolylineOptions? = null
        ){

        internal var pathWidth: Float = DEFAULT_PATH_WIDTH
        internal var pathColor: Int = DEFAULT_PATH_COLOR
        internal var geodesic: Boolean = DEFAULT_GEODESIC
        internal var isPreview: Boolean = DEFAULT_PREVIEW

        fun pathWidth(width: Float): Builder {
            this.pathWidth = width
            return this
        }

        fun pathColor(color: Int): Builder {
            this.pathColor = color
            return this
        }

        fun geodesic(geodesic: Boolean): Builder{
            this.geodesic = geodesic
            return this
        }

        fun previewMode(isPreview: Boolean): Builder{
            this.isPreview = isPreview
            return this
        }

        fun build(): EasyRoutesDrawer {
            return EasyRoutesDrawer(this)
        }

    }
}