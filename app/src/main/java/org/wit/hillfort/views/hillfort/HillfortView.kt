package org.wit.hillfort.activities.hillfort

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_hillfort.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.toast
import org.wit.hillfort.R
import org.wit.hillfort.activities.ImageGalleryAdapter
import org.wit.hillfort.activities.ImageListener
import org.wit.hillfort.models.HillfortModel

class HillfortView : AppCompatActivity(), AnkoLogger, ImageListener {

    lateinit var presenter: HillfortPresenter
    var hillfort = HillfortModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hillfort)
        toolbarAdd.title = title
        setSupportActionBar(toolbarAdd)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        presenter = HillfortPresenter(this)

        val layoutManager = GridLayoutManager(this,2)
        imageGallery.layoutManager = layoutManager

        btnAdd.setOnClickListener {
            if (hillfortName.text.toString().isEmpty()) {
                toast(R.string.enter_hillfort_Name)
            } else {
                presenter.doAddOrSave(hillfortName.text.toString(), description.text.toString(), additionalNotes.text.toString())
            }
        }

        chooseImage.setOnClickListener {
            presenter.doSelectImage()
        }

        placemarkLocation.setOnClickListener {
            presenter.doSetLocation()
        }
    }

    fun showHillfort(hillfort: HillfortModel) {
        hillfortName.setText(hillfort.name)
        description.setText(hillfort.description)
        loadImages(hillfort.images)
        btnAdd.setText(R.string.save_hillfort)
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

    fun loadImages(images: List<String>) {
        imageGallery.adapter = ImageGalleryAdapter(images, this)
        imageGallery.adapter?.notifyDataSetChanged()
    }
}
