package org.wit.hillfort.activities.hillfort

import android.support.v7.app.AppCompatActivity
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast
import org.wit.hillfort.R
import org.wit.hillfort.activities.SettingsView
import org.wit.hillfort.main.MainApp
import org.wit.hillfort.models.HillfortModel

class HillfortListPresenter(val view: HillfortListView) {

    var app: MainApp

    init {
        app = view.application as MainApp
    }

    fun getHillforts(): ArrayList<HillfortModel> {
        return app.currentUser.hillforts
    }

    fun doAddHillfort() {
        view.startActivityForResult<HillfortView>(0)
    }

    fun doEditHillfort(hillfort: HillfortModel) {
        view.startActivityForResult(view.intentFor<HillfortView>().putExtra("hillfort_edit", hillfort), 0)
    }

    fun doShowHillfortsMap() {
        view.startActivityForResult<HillfortMapsView>(0)
    }

    fun doLogout(){
        view.setResult(AppCompatActivity.RESULT_OK)
        view.toast(R.string.logged_out)
        view.finish()
    }

    fun doShowSettings(){
        view.startActivityForResult<SettingsView>(0)
    }
}