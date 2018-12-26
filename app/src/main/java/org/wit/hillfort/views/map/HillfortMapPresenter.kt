package org.wit.hillfort.views.map

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.content_hillfort_maps.*
import org.jetbrains.anko.intentFor
import org.wit.hillfort.helpers.readImageFromPath
import org.wit.hillfort.main.MainApp
import org.wit.hillfort.models.HillfortModel
import org.wit.hillfort.views.BasePresenter
import org.wit.hillfort.views.BaseView
import org.wit.hillfort.views.ImageView

class HillfortMapPresenter(view: BaseView) : BasePresenter(view) {

    fun doPopulateMap(map: GoogleMap, hillforts: List<HillfortModel>) {
        map.uiSettings.setZoomControlsEnabled(true)
        hillforts.forEach {
            val loc = LatLng(it.lat, it.lng)
            val options = MarkerOptions().title(it.name).position(loc)
            map.addMarker(options).tag = it.id
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, it.zoom))
        }
    }

    fun doMarkerSelected(marker: Marker) {
        val tag = marker.tag as Long
        val hillfort = app.users.findById(app.currentUser.copy(), tag)
        view?.currentTitle?.text = hillfort!!.name
        view?.currentDescription?.text = hillfort!!.description
        if (!hillfort.images.isEmpty()) {
            view?.imageView?.setImageBitmap(readImageFromPath(view!!, hillfort.images[0]))
        }
    }

    fun doImageClick(image: String){
        view?.startActivityForResult(view!!.intentFor<ImageView>().putExtra("image", image), 3)
    }

    fun loadHillforts() {
        view?.showHillforts(app.currentUser.hillforts)
    }
}