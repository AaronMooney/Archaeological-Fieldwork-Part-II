package org.wit.hillfort.views.hillfortlist

import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import org.jetbrains.anko.toast
import org.wit.hillfort.R
import org.wit.hillfort.models.HillfortModel
import org.wit.hillfort.views.BasePresenter
import org.wit.hillfort.views.BaseView
import org.wit.hillfort.views.VIEW
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async

class HillfortListPresenter(view: BaseView) : BasePresenter(view) {

    fun loadHillforts() {
        async(UI) {
            view?.showHillforts(app.hillforts.findAll())
        }
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

    fun doLogout() {
        FirebaseAuth.getInstance().signOut()
        view?.navigateTo(VIEW.LOGIN)
    }

    fun doShowSettings(){
        view?.navigateTo(VIEW.SETTINGS)
    }
}