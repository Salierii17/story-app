package com.example.storyapp.ui.settings

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.storyapp.R
import com.example.storyapp.ViewModelFactory
import com.example.storyapp.ui.auth.AuthActivity
import com.example.storyapp.ui.auth.AuthViewModel
import kotlinx.coroutines.launch
import java.util.Locale

class SettingsFragment : PreferenceFragmentCompat() {

    private lateinit var authViewModel: AuthViewModel
    private lateinit var languageViewModel: LanguageViewModel
    private var loadingDialog: AlertDialog? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val factory = ViewModelFactory.getInstance(requireActivity())
        authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]
        languageViewModel = ViewModelProvider(this, factory)[LanguageViewModel::class.java]


        val languagePreference = findPreference<ListPreference>("language")
        languagePreference?.setOnPreferenceChangeListener { _, newValue ->
            val languageCode = newValue.toString()

            showDialog()

            lifecycleScope.launch {
                languageViewModel.saveLanguage(languageCode)
            }
            updateLanguage(languageCode)

            true
        }

        val logoutPreference = findPreference<Preference>("logout")
        logoutPreference?.setOnPreferenceClickListener {
            logout()
            true
        }
    }

    private fun updateLanguage(languageCode: String) {
        Handler(Looper.getMainLooper()).postDelayed({
            setLocale(languageCode)
            hideDialog()
        }, 1000)
    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        requireActivity().recreate()
    }

    private fun showDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage(getString(R.string.updating_language))
            .setCancelable(false)

        loadingDialog = builder.create()
        loadingDialog?.show()
    }

    private fun hideDialog() {
        loadingDialog?.dismiss()
    }


    private fun logout() {
        authViewModel.logout()

        val intent = Intent(requireContext(), AuthActivity::class.java)
        startActivity(intent)
        requireActivity().finishAffinity()
    }
}