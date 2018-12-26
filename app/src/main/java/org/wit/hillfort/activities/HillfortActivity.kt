package org.wit.hillfort.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.NavUtils
import android.support.v7.widget.GridLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_hillfort.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast
import org.wit.hillfort.R
import org.wit.hillfort.helpers.showImagePicker
import org.wit.hillfort.main.MainApp
import org.wit.hillfort.models.HillfortModel
import org.wit.hillfort.models.Location
import java.util.*

class HillfortActivity : AppCompatActivity(), AnkoLogger, ImageListener {

    val IMAGE_REQUEST = 1
    val IMAGE_GALLERY_REQUEST = 3
    val LOCATION_REQUEST = 2
    var hillfort = HillfortModel()
    lateinit var app: MainApp
    var edit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hillfort)
        app = application as MainApp
        toolbarAdd.title = title
        setSupportActionBar(toolbarAdd)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val layoutManager = GridLayoutManager(this,2)
        imageGallery.layoutManager = layoutManager

        if (intent.hasExtra("hillfort_edit")) {
            edit = true
            hillfort = intent.extras.getParcelable<HillfortModel>("hillfort_edit")
            hillfortName.setText(hillfort.name)
            description.setText(hillfort.description)
            additionalNotes.setText(hillfort.notes)
            btnAdd.setText(R.string.save_hillfort)
        }

        loadImages(hillfort.images)

        btnAdd.setOnClickListener {
            hillfort.name = hillfortName.text.toString()
            hillfort.description = description.text.toString()
            hillfort.notes = additionalNotes.text.toString()
            if (hillfort.name.isEmpty()) {
                toast(R.string.enter_hillfort_Name)
            } else {
                if (edit) {
                    app.users.updateUser(app.currentUser.copy(), hillfort)
                } else {
                    app.currentUser.hillforts.add(hillfort.copy())
                    app.users.updateUser(app.currentUser.copy(), hillfort)
                }
            }
            info("add Button Pressed: $hillfortName")
            setResult(AppCompatActivity.RESULT_OK)
            finish()
        }

        chooseImage.setOnClickListener {
            showImagePicker(this, IMAGE_REQUEST)
        }

        placemarkLocation.setOnClickListener {
            val location = Location(52.245696, -7.139102, 15f)
            if (hillfort.zoom != 0f) {
                location.lat =  hillfort.lat
                location.lng = hillfort.lng
                location.zoom = hillfort.zoom
            }
            startActivityForResult(intentFor<MapsActivity>().putExtra("location", location), LOCATION_REQUEST)
        }

        val dateVisited : TextView = findViewById(R.id.dateVisited)
        val descriptionContent : TextView = findViewById(R.id.descriptionContent)
        val additionalNotes : TextView = findViewById(R.id.additionalNotesContent)

        val dateVisitedText = java.lang.String.format(resources.getString(R.string.date_visited), hillfort.dateVisited)
        descriptionContent.text = hillfort.description
        additionalNotes.text = hillfort.notes

        if (hillfort.dateVisited != "") {
            dateVisited.text = dateVisitedText.replace("%", "")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_placemark, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.item_cancel -> {
                finish()
            }
            R.id.item_delete -> {
                app.users.deleteHillfort(app.currentUser,hillfort.copy())
                finish()
            }
            android.R.id.home -> {
                NavUtils.navigateUpFromSameTask(this)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            IMAGE_REQUEST -> {
                if (data != null) {
                    hillfort.images.add(data.getData().toString())
                    loadImages(hillfort.images)
                }
            }
            LOCATION_REQUEST -> {
                if (data != null) {
                    val location = data.extras.getParcelable<Location>("location")
                    hillfort.lat = location.lat
                    hillfort.lng = location.lng
                    hillfort.zoom = location.zoom
                }
            }
        }
    }

    override fun onHillfortImageClick(image: String) {
        startActivityForResult(intentFor<ImageActivity>().putExtra("image", image), IMAGE_GALLERY_REQUEST)
    }

    fun loadImages(images: List<String>) {
        imageGallery.adapter = ImageGalleryAdapter(images, this)
        imageGallery.adapter?.notifyDataSetChanged()
    }
}
