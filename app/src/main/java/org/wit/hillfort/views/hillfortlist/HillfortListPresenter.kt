package org.wit.hillfort.views.hillfortlist

import android.support.v7.app.AppCompatActivity
import org.jetbrains.anko.toast
import org.wit.hillfort.R
import org.wit.hillfort.models.HillfortModel
import org.wit.hillfort.views.BasePresenter
import org.wit.hillfort.views.BaseView
import org.wit.hillfort.views.VIEW

class HillfortListPresenter(view: BaseView) : BasePresenter(view) {

    fun getHillforts(): ArrayList<HillfortModel> {
        return app.currentUser.copy().hillforts
    }

    fun doAddHillfort() {
        view?.navigateTo(VIEW.HILLFORT)
    }

    fun doEditHillfort(hillfort: HillfortModel) {
        view?.navigateTo(VIEW.HILLFORT, 0, "hillfort_edit", hillfort)
    }

    fun doShowHillfortsMap() {
        view?.navigateTo(VIEW.MAPS)
    }

    fun doLogout(){
        view?.setResult(AppCompatActivity.RESULT_OK)
        view?.toast(R.string.logged_out)
        view?.finish()
    }

    fun doShowSettings(){
        view?.navigateTo(VIEW.SETTINGS)
    }
}