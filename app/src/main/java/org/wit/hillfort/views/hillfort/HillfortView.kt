package org.wit.hillfort.views.hillfort

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.maps.GoogleMap
import kotlinx.android.synthetic.main.activity_hillfort.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.toast
import org.wit.hillfort.R
import org.wit.hillfort.views.ImageListener
import org.wit.hillfort.models.HillfortModel
import org.wit.hillfort.views.BaseView

class HillfortView : BaseView(), AnkoLogger, ImageListener {

    lateinit var presenter: HillfortPresenter
    var hillfort = HillfortModel()
    lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hillfort)
        init(toolbarAdd, true)

        presenter =  initPresenter(HillfortPresenter(this)) as HillfortPresenter

        val layoutManager = androidx.recyclerview.widget.GridLayoutManager(this, 2)
        imageGallery.layoutManager = layoutManager

        chooseImage.setOnClickListener {
            presenter.doSelectImage()
        }

        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync {
            presenter.doConfigureMap(it)
            it.setOnMapClickListener {
                presenter.doSetLocation()
            }
        }
    }

    override fun showHillfort(hillfort: HillfortModel) {
        hillfortName.setText(hillfort.name)
        description.setText(hillfort.description)
        loadImages(hillfort.images)
        lat.setText("%.6f".format(hillfort.lat))
        lng.setText("%.6f".format(hillfort.lng))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_placemark, menu)
        if (presenter.edit) menu?.getItem(0)?.setVisible(true)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.item_cancel -> {
                presenter.doCancel()
            }
            R.id.item_delete -> {
                presenter.doDelete()
                finish()
            }
            R.id.item_save -> {
                if (hillfortName.text.toString().isEmpty()) {
                    toast(R.string.enter_hillfort_Name)
                } else {
                    presenter.doAddOrSave(
                        hillfortName.text.toString(),
                        description.text.toString(),
                        additionalNotes.text.toString()
                    )
                }
            }
            android.R.id.home -> {
                presenter.doUp()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            presenter.doActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onHillfortImageClick(image: String) {
        presenter.doImageClick(image)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        presenter.doResartLocationUpdates()
    }
}
