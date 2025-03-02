package com.example.disasterapp

import android.Manifest
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun MapScreen(
    locationUtils: LocationUtils,
    viewModel: LocationViewModel,
    navController: NavController,
    context: Context,
    address: String,
    location: LocationData?,
    userState: String?,
    db: FirebaseFirestore


)

{
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = {
                permissions ->
            if (permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
                && permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
                //have access
                locationUtils.requestLocationUpdates(viewModel)
            }
            else {
                val rationaleRequired = ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
                        ||  ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )

                if (rationaleRequired) {
                    Toast.makeText(
                        context,
                        "Location permission is required for this feature to work.",
                        Toast.LENGTH_LONG)
                        .show()
                }
                else {
                    Toast.makeText(
                        context,
                        "Location permission is required. Please enable it in the Android settings.",
                        Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    )

    LaunchedEffect(Unit) {
        if (!locationUtils.hasLocationPermission(context))
        {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        }
    }

    locationUtils.requestLocationUpdates(viewModel)
    viewModel.fetchHelpers(
        db,
        onFailure = {
            Toast.makeText(context, "Konumlara ulaşılamadı", Toast.LENGTH_SHORT).show()
        }
    )

//    Box(modifier = Modifier.fillMaxSize()) {
//        GoogleMap(
//            modifier = Modifier.fillMaxSize(),
//            properties = MapProperties(isMyLocationEnabled = true),
//            onMapLoaded = {
//    val foodHelpers = viewModel.getFoodHelpers()
//                foodHelpers.forEach { helper ->
//                    // Her bir yemek yardımı sağlayan yer için haritaya bir işaretçi (marker) ekleyin
//                    val position = LatLng(helper.location.latitude, helper.location.longitude)
//                    GoogleMapMarker(position, title = helper.name)
//                }
//            }
//        )
//    }

    viewModel.location.value?.let { DisplayMap(it, userState, viewModel, navController) }
}