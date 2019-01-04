package org.wit.hillfort.main

import android.app.Application
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.wit.hillfort.models.*
import org.wit.hillfort.models.firebase.HillfortFireStore
import org.wit.hillfort.models.room.HillfortStoreRoom

class MainApp : Application(), AnkoLogger {

    lateinit var hillforts: HillfortStore
    var numHillforts = 0

    override fun onCreate() {
        super.onCreate()

        hillforts = HillfortFireStore(applicationContext)
        info("app started")
    }
}