package com.gabrielsantana.projects.controledegastos.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.gabrielsantana.projects.controledegastos.R
import com.gabrielsantana.projects.controledegastos.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import java.security.AccessControlContext

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    private var _binding: ActivityMainBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        initNavHost()
        initAppBarConfiguration()
        setupBottomNav()

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.fragment_dashboard -> {
                    setupToolbarForDashboard()
                }
                R.id.fragment_transaction_history -> {
                    setupToolbarForTransactionHistory()
                }
                R.id.fragment_add_transaction -> {
                    setupToolbarForAddTransaction()
                }
            }
        }
    }

    private fun setupToolbarForAddTransaction() {

    }

    private fun setupToolbarForTransactionHistory() {

    }

    private fun setupToolbarForDashboard() {

    }

    private fun initAppBarConfiguration() {
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.fragment_dashboard, R.id.fragment_transaction_history,  R.id.fragment_add_transaction)
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    private fun setupBottomNav() {
        binding.bottomNav.setupWithNavController(navController)
    }

    private fun initNavHost() {
        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}

