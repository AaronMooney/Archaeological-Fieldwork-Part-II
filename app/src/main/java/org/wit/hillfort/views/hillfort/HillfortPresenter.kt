package org.wit.hillfort.views.hillfort

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.app.NavUtils
import android.widget.TextView
import androidx.core.content.FileProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.wit.hillfort.R
import org.wit.hillfort.helpers.checkLocationPermissions
import org.wit.hillfort.helpers.createDefaultLocationRequest
import org.wit.hillfort.helpers.isPermissionGranted
import org.wit.hillfort.helpers.showImagePicker
import org.wit.hillfort.models.Location
import org.wit.hillfort.models.HillfortModel
import org.wit.hillfort.views.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class HillfortPresenter(view: BaseView) : BasePresenter(view){

    var hillfort = HillfortModel()
    var location = Location(52.245696, -7.139102, 15f)
    var edit = false
    var map: GoogleMap? = null
    var locationService: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(view)
    val locationRequest = createDefaultLocationRequest()
    lateinit var photoFilePath: String
    val logger = AnkoLogger<HillfortPresenter>()

    init {
        if (view.intent.hasExtra("hillfort_edit")) {
            edit = true
            hillfort = view.intent.extras.getParcelable<HillfortModel>("hillfort_edit")
            val dateVisited : TextView = view.findViewById(R.id.dateVisited)
            val descriptionContent : TextView = view.findViewById(R.id.descriptionContent)
            val additionalNotes : TextView = view.findViewById(R.id.additionalNotesContent)

            val dateVisitedText = java.lang.String.format(view.resources.getString(R.string.date_visited), hillfort.dateVisited)
            descriptionContent.text = hillfort.description
            additionalNotes.text = hillfort.notes

            if (hillfort.dateVisited != "") {
                dateVisited.text = dateVisitedText.replace("%", "")
            }
            view.showHillfort(hillfort)
        } else {
            if (checkLocationPermissions(view)) {
                doSetCurrentLocation()
            }
        }
    }

    fun doAddOrSave(name: String, description: String, notes: String, rating: Float, favorite: Boolean) {
        hillfort.name = name
        hillfort.description = description
        hillfort.notes = notes
        hillfort.rating = rating
        hillfort.favorite = favorite
        async(UI) {
            if (edit) {
                app.hillforts.update(hillfort)
            } else {
                app.hillforts.create(hillfort)
            }
            view?.finish()
        }
    }

    fun doCancel() {
        view?.finish()
    }

    fun doDelete() {
        async(UI) {
            app.hillforts.delete(hillfort)
            view?.finish()
        }
    }

    fun doSelectImage() {
        showImagePicker(view!!, IMAGE_REQUEST)
    }

    fun doUp() {
        NavUtils.navigateUpFromSameTask(view!!)
    }

    fun doSetLocation() {
        view?.navigateTo(VIEW.LOCATION, LOCATION_REQUEST, "location", Location(hillfort.location.lat, hillfort.location.lng, hillfort.location.zoom))
    }

    override fun doActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        when (requestCode) {
            IMAGE_REQUEST -> {
                hillfort.image = data.data.toString()
                view?.showHillfort(hillfort)
            }
            LOCATION_REQUEST -> {
                val location = data.extras.getParcelable<Location>("location")
                hillfort.location = location
                locationUpdate(location)
            }
            REQUEST_IMAGE_CAPTURE -> {
                hillfort.image = photoFilePath
                view?.showHillfort(hillfort)
            }
        }
    }

    fun doConfigureMap(m: GoogleMap) {
        map = m
        locationUpdate(hillfort.location)
    }

    fun locationUpdate(location: Location) {
        hillfort.location = location
        hillfort.location.zoom = 15f
        map?.clear()
        map?.uiSettings?.setZoomControlsEnabled(true)
        val options = MarkerOptions().title(hillfort.name).position(LatLng(hillfort.location.lat, hillfort.location.lng))
        map?.addMarker(options)
        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(hillfort.location.lat, hillfort.location.lng), hillfort.location.zoom))
        view?.showHillfort(hillfort)
    }

    override fun doRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (isPermissionGranted(requestCode, grantResults)) {
            doSetCurrentLocation()
        } else {
            // permissions denied, so use the default location
            locationUpdate(location)
        }
    }

    @SuppressLint("MissingPermission")
    fun doSetCurrentLocation() {
        locationService.lastLocation.addOnSuccessListener {
            locationUpdate(Location(it.latitude, it.longitude))
        }
    }

    @SuppressLint("MissingPermission")
    fun doResartLocationUpdates() {
        var locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                if (locationResult != null && locationResult.locations != null) {
                    val l = locationResult.locations.last()
                    locationUpdate(Location(l.latitude, l.longitude))
                }
            }
        }
        if (!edit) {
            locationService.requestLocationUpdates(locationRequest, locationCallback, null)
        }
    }

    @Throws(IOException::class)
    fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = view!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            photoFilePath = absolutePath
            logger.info("path: " + photoFilePath)
        }
    }

    fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(view!!.packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        view!!,
                        "org.wit.hillfort.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    view!!.startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                }
            }
        }
    }
}