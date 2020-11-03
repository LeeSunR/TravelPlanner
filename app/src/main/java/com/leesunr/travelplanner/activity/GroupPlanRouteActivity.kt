package com.leesunr.travelplanner.activity

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.leesunr.travelplanner.R
import com.leesunr.travelplanner.model.Plan


class GroupPlanRouteActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var minLatitude: Double? = null
    private var maxLatitude: Double? = null
    private var minlongitude: Double? = null
    private var maxlongitude: Double? = null
    private var markerArray:ArrayList<MarkerOptions> = ArrayList()
    private var polylineOptions = PolylineOptions()
        .color(Color.BLUE)
        .width(10.0f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_plan_route)
        val planlist =intent.getSerializableExtra("planList") as ArrayList<ArrayList<Plan>>

        planlist.forEach {arrayList ->
            arrayList.forEach { plan ->
                if(plan.latitude!=null && plan.longitude!= null){
                    val marker = MarkerOptions().position(LatLng(plan.latitude!!,plan.longitude!!)).title(plan.pname)
                    if(minLatitude==null || minLatitude!!>plan.latitude!!){
                        minLatitude = plan.latitude
                    }
                    if(maxLatitude==null || maxLatitude!!<=plan.latitude!!){
                        maxLatitude = plan.latitude
                    }
                    if(minlongitude==null || minlongitude!!>plan.longitude!!){
                        minlongitude = plan.longitude
                    }
                    if(maxlongitude==null || maxlongitude!!<=plan.longitude!!){
                        maxlongitude = plan.longitude
                    }
                    markerArray.add(marker)
                    polylineOptions.add(marker.position)
                }
            }
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val sydney = LatLng(-34.0, 151.0)
        markerArray.forEach{ markerOption ->
            mMap.addMarker(markerOption)
        }
        mMap.addPolyline(polylineOptions).color
        val bounds = LatLngBounds(LatLng(minLatitude!!,minlongitude!!),LatLng(maxLatitude!!,maxlongitude!!))
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds,dpToPx(this,100.0f).toInt()))
    }

    private fun dpToPx(context: Context, dp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics)
    }
}