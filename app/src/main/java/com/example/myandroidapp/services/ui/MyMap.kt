package com.example.myandroidapp.services.ui

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

const val TAG = "MyMap"

@Composable
fun MyMap(lat: Double, long: Double, modifier: Modifier) {
    val markerState = rememberMarkerState(position = LatLng(lat, long))
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(markerState.position, 10f)
    }
    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        onMapClick = {
            Log.d(TAG, "onMapClick $it")
        },
        onMapLongClick = {
            Log.d(TAG, "onMapLongClick $it")
            markerState.position = it
        },
    ) {
        Marker(
            state = markerState,
            title = "User location title",
            snippet = "User location",
        )
    }
}
