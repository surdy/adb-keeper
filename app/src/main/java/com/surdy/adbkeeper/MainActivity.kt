package com.surdy.adbkeeper

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView

/**
 * Trivial launchable activity. Android TV requires the app be launched at least once
 * before BOOT_COMPLETED will be delivered, and a launchable (LEANBACK) activity is what
 * makes the app show up in the Sideload Launcher. On launch it re-applies the flags and
 * schedules the periodic keep-alive worker.
 */
class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val summary = AdbEnabler.enable(this, "launch")
        KeepWorker.schedule(this)

        val text = TextView(this).apply {
            gravity = Gravity.CENTER
            textSize = 18f
            setPadding(48, 48, 48, 48)
            text = "ADB Keeper active\n\n$summary\n\n" +
                "Keeps wireless debugging on across reboot/sleep.\n" +
                "You can close this screen."
        }
        setContentView(text)
    }

    override fun onResume() {
        super.onResume()
        AdbEnabler.enable(this, "resume")
    }
}
