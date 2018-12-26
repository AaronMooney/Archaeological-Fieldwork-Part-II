package org.wit.hillfort.views.hillfort

import android.content.Intent
import android.support.v4.app.NavUtils
import android.widget.TextView
import org.jetbrains.anko.intentFor
import org.wit.hillfort.R
import org.wit.hillfort.views.editlocation.EditLocationView
import org.wit.hillfort.helpers.showImagePicker
import org.wit.hillfort.main.MainApp
import org.wit.hillfort.models.Location
import org.wit.hillfort.models.HillfortModel
import org.wit.hillfort.views.*

class HillfortPresenter(view: BaseView) : BasePresenter(view){

    var hillfort = HillfortModel()
    var location = Location(52.245696, -7.139102, 15f)
    var edit = false

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
        }
    }

    fun doAddOrSave(name: String, description: String, notes: String) {
        hillfort.name = name
        hillfort.description = description
        hillfort.notes = notes
        if (edit) {
            app.users.updateUser(app.currentUser.copy(), hillfort)
        } else {
            app.currentUser.copy().hillforts.add(hillfort.copy())
            app.users.updateUser(app.currentUser.copy(), hillfort)
        }
        view?.finish()
    }

    fun doCancel() {
        view?.finish()
    }

    fun doDelete() {
        app.users.deleteHillfort(app.currentUser,hillfort.copy())
        view?.finish()
    }

    fun doSelectImage() {
        showImagePicker(view!!, IMAGE_REQUEST)
    }

    fun doUp() {
        NavUtils.navigateUpFromSameTask(view!!)
    }

    fun doImageClick(image: String){
        view?.startActivityForResult(view!!.intentFor<ImageView>().putExtra("image", image), IMAGE_GALLERY_REQUEST)
    }

    fun doSetLocation() {
        if (edit == false) {
            view?.navigateTo(VIEW.LOCATION, LOCATION_REQUEST, "location", location)
        } else {
            view?.navigateTo(VIEW.LOCATION, LOCATION_REQUEST, "location", Location(hillfort.lat, hillfort.lng, hillfort.zoom))
        }
    }

    override fun doActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        when (requestCode) {
            IMAGE_REQUEST -> {
                hillfort.images.add(data.getData().toString())
                view?.loadImages(hillfort.images)
                view?.showHillfort(hillfort)
            }
            LOCATION_REQUEST -> {
                location = data.extras.getParcelable<Location>("location")
                hillfort.lat = location.lat
                hillfort.lng = location.lng
                hillfort.zoom = location.zoom
            }
        }
    }
}