package org.wit.hillfort.main

import android.app.Application
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.wit.hillfort.models.*

class MainApp : Application(), AnkoLogger {

    lateinit var users: UserStore
    lateinit var currentUser: UserModel
    var numHillfortsVisited: Int = 0

    override fun onCreate() {
        super.onCreate()

        users = UserJSONStore(applicationContext)
        info("app started")
    }
}