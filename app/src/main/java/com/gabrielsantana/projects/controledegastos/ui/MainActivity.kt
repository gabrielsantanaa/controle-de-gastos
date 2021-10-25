package com.gabrielsantana.projects.controledegastos.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.gabrielsantana.projects.controledegastos.R
import com.gabrielsantana.projects.controledegastos.databinding.ActivityMainBinding
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import dagger.hilt.android.AndroidEntryPoint
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), KeyboardVisibilityEventListener {

    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

    private var _binding: ActivityMainBinding? = null
    private val binding
        get() = _binding!!

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initNavHost()
        setupBottomNav()

        KeyboardVisibilityEvent.setEventListener(this, this)

    }

    private fun setupBottomNav() {
        binding.bottomNav.setupWithNavController(navController)
        setBottomNavCorners()
    }

    private fun setBottomNavCorners() {
        val radius = resources.getDimension(R.dimen.bottom_nav_corners)
        val customShape = ShapeAppearanceModel.Builder()
            .setAllCorners(CornerFamily.ROUNDED, radius)
            .build()
        (binding.bottomNav.background as MaterialShapeDrawable)
            .shapeAppearanceModel = customShape
    }

    private fun initNavHost() {
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onVisibilityChanged(isOpen: Boolean) {
        binding.bottomNav.visibility = if (isOpen) View.GONE else View.VISIBLE
    }

}

