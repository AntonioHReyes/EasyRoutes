package com.example.easyroutes

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.easyroutes.databinding.ActivityMapsBinding
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.PolylineOptions
import com.tonyakitori.inc.easyroutes.EasyRoutesDirections
import com.tonyakitori.inc.easyroutes.EasyRoutesDrawer
import com.tonyakitori.inc.easyroutes.enums.TransportationMode
import com.tonyakitori.inc.easyroutes.extensions.drawRoute
import com.tonyakitori.inc.easyroutes.extensions.getGoogleMapsLink
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private val markersList: ArrayList<Marker> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val mexico = LatLng(19.43488491844211, -99.13136781301444)
        mMap.addMarker(MarkerOptions().position(mexico).title("Templo Mayor, México"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mexico))


        val placeDirections = EasyRoutesDirections(
            originPlace = "Ixhuatlán del café, Veracruz, México",
            destinationPlace = "Hermosillo, Sonora, México",
            apiKey = getString(R.string.google_maps_key),
            waypointsLatLng = arrayListOf(
                LatLng(20.077550759401632, -98.36895687240255),
                LatLng(19.43488491844211, -99.13136781301444)
            ),
            waypointsPlaces = arrayListOf(
                "Santiago de Queretaro",
                "Aguascalientes"
            ),
            showDefaultMarkers= true,
            transportationMode = TransportationMode.WALKING
        )

        val placeAndLatLng = EasyRoutesDirections(
            originPlace = "Oaxaca",
            destinationLatLng = LatLng(19.417708496429597, -102.05174097597963),
            apiKey = getString(R.string.google_maps_key)
        )

        val latLngAndPlace = EasyRoutesDirections(
            originLatLng = LatLng(19.417708496429597, -102.05174097597963),
            destinationPlace = "Merida, Yucatan, México",
            apiKey = getString(R.string.google_maps_key)
        )

        val customPolylineOptions = PolylineOptions()
        customPolylineOptions.color(ContextCompat.getColor(this@MapsActivity, R.color.red))
        customPolylineOptions.width(15f)

        val routeDrawer = EasyRoutesDrawer.Builder(mMap)
            .pathWidth(10f)
            .pathColor(Color.GREEN)
            .geodesic(true)
            .previewMode(false)
            .build()

        val routeDrawerWithCustomPolyline = EasyRoutesDrawer.Builder(mMap, customPolylineOptions)
            .previewMode(false)
            .build()

        mMap.drawRoute(
            context = this@MapsActivity,
            easyRoutesDirections = placeDirections,
            routeDrawer = routeDrawer,
            markersListCallback = {markers -> markersList.addAll(markers) },
            googleMapsLink = { url -> Log.d("GoogleLink", url)}
        ){ legs ->
            legs?.forEach {
                Log.d("Point startAddress:", it?.startAddress.toString())
                Log.d("Point endAddress:", it?.endAddress.toString())
                Log.d("Distance:", it?.distance.toString())
                Log.d("Duration:", it?.duration.toString())
            }
        }

        binding.mapsDirections.setOnClickListener {
            val url = getGoogleMapsLink(placeDirections)

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }


        lifecycleScope.launch {
            delay(120_000)
            routeDrawer.removeRoute()
            routeDrawerWithCustomPolyline.removeRoute()

            markersList.forEach {
                it.remove()
            }
        }

    }
}