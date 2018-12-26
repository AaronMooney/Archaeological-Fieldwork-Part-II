package org.wit.hillfort.activities

import android.os.Bundle
import android.support.v4.app.NavUtils
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
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
import org.jetbrains.anko.contentView


class SettingsActivity: AppCompatActivity(), AnkoLogger {
    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        app = application as MainApp

        toolBarSettings.title = title
        setSupportActionBar(toolBarSettings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val currentUserName :TextView = findViewById(R.id.currentUser)
        val currentUserEmail: TextView = findViewById(R.id.currentUserEmail)
        val totalHillforts :TextView = findViewById(R.id.totalHillforts)
        val numHillfortsVisited: TextView = findViewById(R.id.numHillfortsVisited)

        val userName = java.lang.String.format(resources.getString(R.string.current_user), app.currentUser.name)
        val userEmail = java.lang.String.format(resources.getString(R.string.current_user_email), app.currentUser.email)
        val totalHillfortsText = java.lang.String.format(resources.getString(R.string.total_hillforts), app.currentUser.hillforts.size.toString())
        val numHillfortsVisitedText = java.lang.String.format(resources.getString(R.string.number_visited), app.numHillfortsVisited.toString())

        currentUserName.text = userName.replace("%","")
        currentUserEmail.text = userEmail.replace("%", "")
        totalHillforts.text = totalHillfortsText.replace("%", "")
        numHillfortsVisited.text = numHillfortsVisitedText.replace("%", "")
        btnChangePassword.setOnClickListener {
            showDialog(contentView!!)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {

            R.id.item_logout -> {
                setResult(AppCompatActivity.RESULT_OK)
                toast(R.string.logged_out)
                finish()
            }

            android.R.id.home -> {
                NavUtils.navigateUpFromSameTask(this)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun showDialog(view: View) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        builder.setTitle(R.string.settings_change_password)
        val dialogLayout = inflater.inflate(R.layout.dialog_settings, null)
        val newUserPass  = dialogLayout.findViewById<EditText>(R.id.newUserPassword)
        val newUserPassConfirm  = dialogLayout.findViewById<EditText>(R.id.newConfirmUserPassword)
        builder.setView(dialogLayout)
        builder.setPositiveButton(R.string.password_new_confirm) {
                dialogInterface, i ->
            if (newUserPass.text.toString() == newUserPassConfirm.text.toString()){
                app.currentUser.password = hashString(newUserPass.text.toString())
                    app.users.updateUser(app.currentUser)
                    toast(R.string.password_change_success)
            } else {
                toast(R.string.error_signup_confirm)
            }
        }
        builder.show()
    }
}