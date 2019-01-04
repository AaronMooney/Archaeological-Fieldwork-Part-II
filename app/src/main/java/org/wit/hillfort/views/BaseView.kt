package org.wit.hillfort.views

import android.content.Context
import android.content.Intent

import android.os.Parcelable
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import org.jetbrains.anko.AnkoLogger

import org.wit.hillfort.models.HillfortModel
import org.wit.hillfort.views.editlocation.EditLocationView
import org.wit.hillfort.views.map.HillfortMapsView
import org.wit.hillfort.views.hillfort.HillfortView
import org.wit.hillfort.views.hillfortlist.HillfortListView
import org.wit.hillfort.views.login.LoginView
import org.wit.hillfort.views.settings.SettingsView

val IMAGE_REQUEST = 1
val LOCATION_REQUEST = 2
val REQUEST_IMAGE_CAPTURE = 3
val REQUEST_TAKE_PHOTO = 4

enum class VIEW {
    LOCATION, HILLFORT, MAPS, LIST, SETTINGS, LOGIN
}

open abstract class BaseView() : AppCompatActivity(), AnkoLogger {

    var basePresenter: BasePresenter? = null

    fun navigateTo(view: VIEW, code: Int = 0, key: String = "", value: Parcelable? = null) {
        var intent = Intent(this, HillfortListView::class.java)
        when (view) {
            VIEW.LOCATION -> intent = Intent(this, EditLocationView::class.java)
            VIEW.HILLFORT -> intent = Intent(this, HillfortView::class.java)
            VIEW.MAPS -> intent = Intent(this, HillfortMapsView::class.java)
            VIEW.LIST -> intent = Intent(this, HillfortListView::class.java)
            VIEW.SETTINGS -> intent = Intent(this, SettingsView::class.java)
            VIEW.LOGIN -> intent = Intent(this, LoginView::class.java)
        }
        if (key != "") {
            intent.putExtra(key, value)
        }
        startActivityForResult(intent, code)
    }

    fun initPresenter(presenter: BasePresenter): BasePresenter {
        basePresenter = presenter
        return presenter
    }

    fun init(toolbar: Toolbar, upEnabled: Boolean) {
        toolbar.title = title
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(upEnabled)
    }

    fun hideKeyboard(view: View) {
        view.apply {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun onDestroy() {
        basePresenter?.onDestroy()
        super.onDestroy()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            basePresenter?.doActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        basePresenter?.doRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    open fun showHillfort(hillfort: HillfortModel) {}
    open fun showHillforts(hillforts: List<HillfortModel>) {}
    open fun showProgress() {}
    open fun hideProgress() {}
}