package org.wit.hillfort.views.hillfortlist

import com.google.firebase.auth.FirebaseAuth
import org.wit.hillfort.models.HillfortModel
import org.wit.hillfort.views.BasePresenter
import org.wit.hillfort.views.BaseView
import org.wit.hillfort.views.VIEW
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.AnkoLogger

class HillfortListPresenter(view: BaseView) : BasePresenter(view) {

    val logger = AnkoLogger<HillfortListPresenter>()

    var favorites = ArrayList<HillfortModel>()

    fun loadHillforts(showFavorites: Boolean) {
        async(UI) {
            var hillforts = app.hillforts.findAll()
            app.numHillforts = hillforts.size
            if (showFavorites){
                favorites.clear()
                hillforts.forEach {
                    if (it.favorite){
                        favorites.add(it)
                    }
                }
                hillforts = favorites
            }
            view?.showHillforts(hillforts)
        }
    }

    fun loadHillforts(showFavorites: Boolean, query: String) {
        async(UI) {
            var hillforts = app.hillforts.findAll().filter{it.name.contains(query, ignoreCase = true) || it.description.contains(query, ignoreCase = true)}
            if (showFavorites){
                favorites.clear()
                hillforts.forEach {
                    if (it.favorite){
                        favorites.add(it)
                    }
                }
                hillforts = favorites
            }
            view?.showHillforts(hillforts)
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
        app.hillforts.clear()
        view?.navigateTo(VIEW.LOGIN)
    }

    fun doShowSettings(){
        view?.navigateTo(VIEW.SETTINGS)
    }
}