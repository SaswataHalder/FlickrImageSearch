package com.flickr.imagesearchapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.flickr.imagesearchapp.ui.fragments.GalleryFragment
import com.flickr.imagesearchapp.ui.fragments.SearchFragment
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        navView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        if (savedInstanceState == null) {
            toolbar.title = "Image Gallery"
            supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment_main, GalleryFragment()).commit()
        }
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                toolbar.title = "Image Gallery"
                supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment_main, GalleryFragment()).commit()
            }
            R.id.nav_search -> {
                toolbar.title = "Search"
                supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment_main, SearchFragment()).commit()
            }
            else -> println("Give a proper input")
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}