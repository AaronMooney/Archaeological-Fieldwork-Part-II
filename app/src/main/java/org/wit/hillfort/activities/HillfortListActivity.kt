package org.wit.hillfort.activities

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.widget.CheckBox
import kotlinx.android.synthetic.main.activity_hillfort_list.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast
import org.wit.hillfort.R
import org.wit.hillfort.helpers.hashString
import org.wit.hillfort.main.MainApp
import org.wit.hillfort.models.HillfortModel

class HillfortListActivity : AppCompatActivity(), HillfortListener, NavigationView.OnNavigationItemSelectedListener{

    lateinit var app:MainApp
    lateinit var drawerLayout: DrawerLayout
    lateinit var toggleDrawer: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hillfort_list)
        app = application as MainApp
        drawerLayout = findViewById(R.id.drawer)
        toggleDrawer = ActionBarDrawerToggle(this,drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggleDrawer)
        toggleDrawer.syncState()

        toolbarMain.title = title
        setSupportActionBar(toolbarMain)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.abc_ic_menu_overflow_material)

        var navigationView : NavigationView = findViewById(R.id.navigation_view)
        navigationView.setNavigationItemSelectedListener(this)

        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        loadHillforts()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.item_add -> startActivityForResult<HillfortActivity>(0)

            R.id.item_logout -> {
                setResult(AppCompatActivity.RESULT_OK)
                toast(R.string.logged_out)
                finish()
            }

            R.id.item_settings -> {
                startActivityForResult<SettingsActivity>(0)
            }
        }
        if (toggleDrawer.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_add -> startActivityForResult<HillfortActivity>(0)

            R.id.nav_settings -> {
                startActivityForResult<SettingsActivity>(0)
            }

            R.id.nav_logout -> {
                setResult(AppCompatActivity.RESULT_OK)
                toast(R.string.logged_out)
                finish()
            }
        }
        return false
    }

    override fun onHillfortClick(hillfort: HillfortModel) {
        startActivityForResult(intentFor<HillfortActivity>().putExtra("hillfort_edit", hillfort), 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        loadHillforts()
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun loadHillforts() {
        showHillforts(app.currentUser.hillforts)
    }

    fun showHillforts (hillforts: List<HillfortModel>) {
        recyclerView.adapter = HillfortAdapter(hillforts, this, app)
        recyclerView.adapter?.notifyDataSetChanged()
    }
}