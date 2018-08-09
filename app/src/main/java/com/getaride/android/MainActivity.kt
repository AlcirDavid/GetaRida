package com.getaride.android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController

class MainActivity : AppCompatActivity() {

//    val androidNetworkObserver :NetworkObserver by inject()

    override fun onSupportNavigateUp()
            = findNavController(R.id.nav_host_fragment).navigateUp()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
//            supportFragmentManager.beginTransaction()
//                    .replace(R.id.container, MainFragment.newInstance())
////                    .replace(R.id.container, NewServiceAreaFragment.newInstance())
////                    .replace(R.id.container, MoviesFragment.newInstance())
//                    .commitNow()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
//        androidNetworkObserver.tryToUnregisterReceivers()
    }

}
