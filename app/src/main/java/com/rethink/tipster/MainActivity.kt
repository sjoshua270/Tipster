package com.rethink.tipster

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crash.FirebaseCrash

class MainActivity : AppCompatActivity() {
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        // Don't collect info if this is a debug build
        FirebaseCrash.setCrashCollectionEnabled(!BuildConfig.DEBUG)
        if (BuildConfig.DEBUG) {
            Toast.makeText(this,
                           "Debug mode active!",
                           Toast.LENGTH_LONG).show()
        }

        val valuesFrag: Fragment = ValuesFragment()
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN,
                                   null)
        addFragment(valuesFrag, R.id.frag_container)
    }
}

inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
    val fragmentTransaction = beginTransaction()
    fragmentTransaction.func()
    fragmentTransaction.commit()
}

fun AppCompatActivity.addFragment(fragment: Fragment, frameId: Int) {
    supportFragmentManager.inTransaction {
        add(frameId,
            fragment)
    }
}
