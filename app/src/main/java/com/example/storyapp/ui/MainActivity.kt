package com.example.storyapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.ui.auth.AuthActivity
import com.example.storyapp.ui.auth.AuthViewModel
import com.example.storyapp.ui.maps.MapsActivity
import com.example.storyapp.ui.settings.LanguageViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val authViewModel: AuthViewModel by viewModels()
    private val languageViewModel: LanguageViewModel by viewModels()

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        languageViewModel.language.observe(this) { languageCode ->
            setLocale(languageCode)
        }

        lifecycleScope.launch {
            val savedLanguage = languageViewModel.loadLanguage().toString()
            setLocale(savedLanguage)
        }

        lifecycleScope.launch {
            authViewModel.isUserLoggedIn.collectLatest { isLoggedIn ->
                when (isLoggedIn) {
                    true -> setupUI()
                    false -> navigateToAuthActivity()
                    null -> {}
                }
            }
        }

        setContentView(binding.root)
    }

    private fun navigateToAuthActivity() {
        val intent = Intent(this@MainActivity, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_maps -> {
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
                true
            }

            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun setupUI() {
        val navView: BottomNavigationView = binding.navView

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        navController = navHostFragment.navController
        navView.setupWithNavController(navController)
    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}