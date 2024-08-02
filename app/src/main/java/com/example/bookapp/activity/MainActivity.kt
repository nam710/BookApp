package com.example.bookapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.bookapp.fragment.AboutAppFragment
import com.example.bookapp.fragment.DashboardFragment
import com.example.bookapp.dataStore.DataStoreInstance
import com.example.bookapp.fragment.FavouritesFragment
import com.example.bookapp.fragment.ProfileFragment
import com.example.bookapp.R
import com.example.bookapp.database.BookDatabase
import com.example.bookapp.databinding.ActivityMainBinding
import com.example.bookapp.repository.Repository
import com.example.bookapp.viewModel.DescViewModel


import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    var previousMenuItem: MenuItem? = null
    lateinit var dataStoreInstance: DataStoreInstance




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpToolBar()
        dataStoreInstance = DataStoreInstance(this)

        intent = Intent(this@MainActivity, LoginActivity::class.java)










        //creating object of the predefined hamburger icon that toggles drawer layout in and out
        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this@MainActivity,
            binding.drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )






        //adding listener to drawer layout to listen to events of hamburger icon
        binding.drawerLayout.addDrawerListener(actionBarDrawerToggle)
        //to synchronize the transition of hamburger icon in to a back icon
        actionBarDrawerToggle.syncState()

        openDashboardFragment()

        binding.navigationView.setNavigationItemSelectedListener {
            if(previousMenuItem!=null){
                previousMenuItem?.isChecked = false
            }
            it.isCheckable = true
            it.isChecked = true
            previousMenuItem = it
            when(it.itemId){
                R.id.dashboard -> {
                    openDashboardFragment()
                    binding.drawerLayout.closeDrawers()
                }
                R.id.favourites -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, FavouritesFragment())
                        .commit()

                    supportActionBar?.title = "Favourites"
                    binding.drawerLayout.closeDrawers()
                }
                R.id.profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, ProfileFragment())
                        .commit()

                    supportActionBar?.title = "Profile"
                    binding.drawerLayout.closeDrawers()
                }
                R.id.aboutApp -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, AboutAppFragment())
                        .commit()

                    supportActionBar?.title = "About App"
                    binding.drawerLayout.closeDrawers()
                }
                R.id.logout -> {
                    CoroutineScope(Dispatchers.IO).launch{
                        dataStoreInstance.storeLoginState(false)
                    }
                    startActivity(intent)
                    finish()

                }
            }
            //since this is a lambda function we need to explicitly
            // tell that this return statement is for the navigationItemSelectedListener
            return@setNavigationItemSelectedListener true
        }
    }


    fun setUpToolBar(){
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Toolbar Title"
        //enabling default home button and changing its functionality
        supportActionBar?.setHomeButtonEnabled(true)
        //to display the button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    //to add functionality to hamburger icon we created this method
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //to store id of item selected
        val id = item.itemId
        // checking if id is that of hamburger icon
        // android.R.id.home is the id of home(default actionbar icon)
        // i.e hamburger icon
        if(id == android.R.id.home){
            //GravityCompat.START tells the drawerLayout to always open from left side
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    fun openDashboardFragment(){
        val fragment = DashboardFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame,fragment)
        transaction.commit()
        supportActionBar?.title = "Dashboard"
        binding.navigationView.setCheckedItem(R.id.dashboard)
    }

    override fun onBackPressed() {
        val frag = supportFragmentManager.findFragmentById(R.id.frame)
        when(frag){
            !is DashboardFragment -> openDashboardFragment()
            else -> super.onBackPressed()
        }
    }
}