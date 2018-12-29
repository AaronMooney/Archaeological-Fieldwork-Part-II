package org.wit.hillfort.views.hillfortlist

import android.content.Intent
import android.os.Bundle
import com.google.android.material.navigation.NavigationView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.*
import kotlinx.android.synthetic.main.activity_hillfort_list.*
import org.wit.hillfort.R
import org.wit.hillfort.main.MainApp
import org.wit.hillfort.models.HillfortModel
import org.wit.hillfort.views.BaseView

class HillfortListView : BaseView(), HillfortListener, NavigationView.OnNavigationItemSelectedListener{

    lateinit var app: MainApp
    lateinit var presenter: HillfortListPresenter
    lateinit var drawerLayout: androidx.drawerlayout.widget.DrawerLayout
    lateinit var toggleDrawer: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hillfort_list)
        init(toolbarMain, false)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.abc_ic_menu_overflow_material)

        app = application as MainApp

        drawerLayout = findViewById(R.id.drawer)
        toggleDrawer = ActionBarDrawerToggle(this,drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggleDrawer)
        toggleDrawer.syncState()

        val navigationView : NavigationView = findViewById(R.id.navigation_view)
        navigationView.setNavigationItemSelectedListener(this)

        presenter = initPresenter(HillfortListPresenter(this)) as HillfortListPresenter

        val layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        presenter.loadHillforts()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.item_add -> presenter.doAddHillfort()

            R.id.item_map -> presenter.doShowHillfortsMap()

//            R.id.item_logout -> presenter.doLogout()

            R.id.item_settings -> presenter.doShowSettings()
        }
        if (toggleDrawer.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_add -> presenter.doAddHillfort()

            R.id.nav_settings -> presenter.doShowSettings()

//            R.id.nav_logout -> presenter.doLogout()
        }
        return false
    }

    override fun onHillfortClick(hillfort: HillfortModel) {
        presenter.doEditHillfort(hillfort)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        presenter.loadHillforts()
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun showHillforts(hillforts: List<HillfortModel> ) {
        recyclerView.adapter = HillfortAdapter(hillforts, this, app)
        recyclerView.adapter?.notifyDataSetChanged()
    }
}