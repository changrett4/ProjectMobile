package com.example.helloworld

import android.os.Bundle
import android.preference.PreferenceFragment
import com.google.firebase.auth.FirebaseAuth

class SettingsFragment : PreferenceFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.main_preference)

    }
}