package org.wit.hillfort.views.hillfort

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.RatingBar
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.gms.maps.GoogleMap
import kotlinx.android.synthetic.main.activity_hillfort.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.find
import org.jetbrains.anko.info
import org.jetbrains.anko.toast
import org.wit.hillfort.R
import org.wit.hillfort.helpers.readImageFromPath
import org.wit.hillfort.models.HillfortModel
import org.wit.hillfort.views.BaseView

class HillfortView : BaseView(), AnkoLogger, RatingBar.OnRatingBarChangeListener {

    lateinit var presenter: HillfortPresenter
    var hillfort = HillfortModel()
    val logger = AnkoLogger<HillfortView>()
    var favorite = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hillfort)
        init(toolbarAdd, true)

        presenter =  initPresenter(HillfortPresenter(this)) as HillfortPresenter

        chooseImage.setOnClickListener {
            presenter.doSelectImage()
        }

        ratingBar.onRatingBarChangeListener = this

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
        ratingBar.rating = hillfort.rating
        favorite = hillfort.favorite

        Glide.with(this).load(hillfort.image).into(hillfortImage)
        if (!hillfort.image.isEmpty()) {
            chooseImage.setText(R.string.change_hillfort_image)
        }
        lat.setText("%.6f".format(hillfort.location.lat))
        lng.setText("%.6f".format(hillfort.location.lng))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_placemark, menu)
        if (!favorite) {
            menu?.getItem(0)?.setIcon(ContextCompat.getDrawable(this, android.R.drawable.star_big_off))
        } else {
            menu?.getItem(0)?.setIcon(ContextCompat.getDrawable(this, android.R.drawable.star_big_on))
        }
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
                        additionalNotes.text.toString(),
                        ratingBar.rating,
                        favorite
                    )
                }
            }
            R.id.item_favorite -> {
                if (!favorite) {
                    item.setIcon(ContextCompat.getDrawable(this, android.R.drawable.star_big_on))
                    favorite = true
                } else {
                    item.setIcon(ContextCompat.getDrawable(this, android.R.drawable.star_big_off))
                    favorite = false
                }
            }
            android.R.id.home -> {
                presenter.doUp()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRatingChanged(p0: RatingBar?, p1: Float, p2: Boolean) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            presenter.doActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        presenter.doResartLocationUpdates()
    }
}
