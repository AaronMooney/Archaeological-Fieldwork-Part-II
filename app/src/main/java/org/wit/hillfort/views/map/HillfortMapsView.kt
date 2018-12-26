package org.wit.hillfort.views.map

import android.os.Bundle
import android.support.v7.app.AppCompatActivity;
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_hillfort.*
import org.wit.hillfort.R

import kotlinx.android.synthetic.main.activity_hillfort_maps.*
import kotlinx.android.synthetic.main.content_hillfort_maps.*
import org.wit.hillfort.models.HillfortModel
import org.wit.hillfort.views.ImageGalleryAdapter
import org.wit.hillfort.views.ImageListener

class HillfortMapsView : AppCompatActivity(), GoogleMap.OnMarkerClickListener, ImageListener {

    lateinit var presenter: HillfortMapPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hillfort_maps)
        setSupportActionBar(toolbarMaps)
        presenter = HillfortMapPresenter(this)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync {
            presenter.doPopulateMap(it)
        }
    }

    fun showHillfort(hillfort: HillfortModel) {
        currentTitle.text = hillfort.name
        currentDescription.text = hillfort.description
        loadImages(hillfort.images)
    }

    fun loadImages(images: List<String>) {
        imageGallery.adapter = ImageGalleryAdapter(images, this)
        imageGallery.adapter?.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        presenter.doMarkerSelected(marker)
        return true
    }

    override fun onHillfortImageClick(image: String) {
        presenter.doImageClick(image)
    }
}
