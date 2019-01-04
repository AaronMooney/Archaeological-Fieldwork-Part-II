package org.wit.hillfort.views.hillfortlist

import android.content.Intent
import android.os.Bundle
import com.google.android.material.navigation.NavigationView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.*
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_hillfort_list.*
import kotlinx.android.synthetic.main.card_hillfort.*
import org.wit.hillfort.R
import org.wit.hillfort.main.MainApp
import org.wit.hillfort.models.HillfortModel
import org.wit.hillfort.views.BaseView
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.intentFor
import org.wit.hillfort.models.firebase.HillfortFireStore
import org.wit.hillfort.views.login.LoginView

class HillfortListView : BaseView(), HillfortListener, NavigationView.OnNavigationItemSelectedListener{

    lateinit var app: MainApp
    lateinit var presenter: HillfortListPresenter
    lateinit var drawerLayout: androidx.drawerlayout.widget.DrawerLayout
    lateinit var toggleDrawer: ActionBarDrawerToggle
    var showFavorites = false

    var fireStore: HillfortFireStore? = null

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

        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        async(UI) {
            if (app.hillforts is HillfortFireStore) {
                fireStore = app.hillforts as HillfortFireStore
                if (app.hillforts.findAll().isEmpty()) {
                    fireStore?.initHillforts()
                    presenter.loadHillforts(showFavorites)
                }
            }
        }
        presenter.loadHillforts(showFavorites)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        if (!showFavorites) {
            menu?.getItem(0)?.setIcon(ContextCompat.getDrawable(this, android.R.drawable.star_big_off))
        } else {
            menu?.getItem(0)?.setIcon(ContextCompat.getDrawable(this, android.R.drawable.star_big_on))
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.item_add -> presenter.doAddHillfort()

            R.id.item_map -> presenter.doShowHillfortsMap()

            R.id.item_logout -> presenter.doLogout()

            R.id.item_settings -> presenter.doShowSettings()

            R.id.item_favorites -> {
                    if (!showFavorites) {
                        item.setIcon(ContextCompat.getDrawable(this, android.R.drawable.star_big_on))
                        showFavorites = true
                    } else {
                        item.setIcon(ContextCompat.getDrawable(this, android.R.drawable.star_big_off))
                        showFavorites = false
                    }
                presenter.loadHillforts(showFavorites)
            }
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

            R.id.nav_logout -> presenter.doLogout()
        }
        return false
    }

    override fun onHillfortClick(hillfort: HillfortModel) {
        presenter.doEditHillfort(hillfort)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        presenter.loadHillforts(showFavorites)
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun showHillforts(hillforts: List<HillfortModel> ) {
        recyclerView.adapter = HillfortAdapter(hillforts, this, app)
        recyclerView.adapter?.notifyDataSetChanged()
    }

    override fun onDestroy() {
        presenter.doLogout()
        super.onDestroy()
    }

    public override fun onResume() {
        async(UI) {
            showHillforts(app.hillforts.findAll())
        }
        super.onResume()

    }
}