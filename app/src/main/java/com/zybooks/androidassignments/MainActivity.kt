package com.zybooks.androidassignments

import com.zybooks.androidassignments.ui.MainFragment
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Load MainFragment as the default fragment
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MainFragment())  // 'fragment_container' is the ID of your fragment container in activity_main.xml
                .commit()
        }
    }
}
