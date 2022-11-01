package me.acayrin.assignment.activity.fragment.main

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import me.acayrin.assignment.R
import java.util.*

class MapFragment : Fragment(R.layout.fragment_main_map), OnMapReadyCallback {
    private var timer: Timer? = null
    private lateinit var geocoder: Geocoder
    private lateinit var mMap: GoogleMap

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context?.apply { geocoder = Geocoder(this) }

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        requireActivity().findViewById<EditText>(R.id.map_search).let {
            it.addTextChangedListener(
                object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                        if (timer != null)
                            timer = null
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                    }

                    override fun afterTextChanged(s: Editable?) {
                        timer = Timer()
                        timer!!.schedule(object : TimerTask() {
                            override fun run() {
                                try {
                                    // run it in main thread
                                    Handler(Looper.getMainLooper()).post {
                                        geocoder.getFromLocationName(
                                            it.text.toString(),
                                            1
                                        )?.get(0)?.let { add ->
                                            try {
                                                setLocation(
                                                    LatLng(add.latitude, add.longitude),
                                                    add.featureName
                                                )
                                            } catch (e: Exception) {
                                                // caught an IndexOutOfBound here for some reason
                                                e.message?.let { m -> Log.e("Fragment::Map", m) }
                                            }
                                        }
                                    }

                                } catch (e: Exception) {
                                    e.message?.let { m -> Log.e("Fragment::Map", m) }
                                }
                            }
                        }, 3_000)
                    }
                }
            )
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomGesturesEnabled = true

        setLocation(LatLng(10.8538211, 106.6256397), "CV Phan mem Quang Trung")
    }

    private fun setLocation(latLng: LatLng, title: String) {
        mMap.clear()
        mMap.addMarker(MarkerOptions().position(latLng).title(title))
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
    }
}