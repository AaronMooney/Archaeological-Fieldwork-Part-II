package org.wit.hillfort.views.settings

import android.os.Bundle
import androidx.core.app.NavUtils
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_settings.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.toast
import org.wit.hillfort.R
import org.wit.hillfort.helpers.hashString
import org.wit.hillfort.main.MainApp
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import org.jetbrains.anko.contentView
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.wit.hillfort.views.BaseView
import org.wit.hillfort.views.VIEW


class SettingsView: BaseView(), AnkoLogger {
    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        app = application as MainApp

        init(toolBarSettings, true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val user = FirebaseAuth.getInstance().currentUser

        val currentUserName :TextView = findViewById(R.id.currentUser)
        val totalHillforts :TextView = findViewById(R.id.totalHillforts)

        val totalHillfortsText = java.lang.String.format(resources.getString(R.string.total_hillforts), app.numHillforts.toString())

        if (user != null) {
            currentUserName.text = java.lang.String.format(resources.getString(R.string.current_user), "${user.email}")
        }
        currentUserName.text = currentUserName.text.toString().replace("%","")
        totalHillforts.text = totalHillfortsText.replace("%", "")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {

            R.id.item_logout -> {
                FirebaseAuth.getInstance().signOut()
                app.hillforts.clear()
                navigateTo(VIEW.LOGIN)
            }

            android.R.id.home -> {
                NavUtils.navigateUpFromSameTask(this)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}