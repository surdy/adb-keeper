package com.surdy.adbkeeper

import android.content.Context
import android.provider.Settings
import android.util.Log

/**
 * Writes the Global secure settings that keep ADB-over-Wi-Fi (wireless debugging) on.
 *
 * Requires WRITE_SECURE_SETTINGS, granted once via:
 *   adb shell pm grant com.surdy.adbkeeper android.permission.WRITE_SECURE_SETTINGS
 */
object AdbEnabler {

    const val TAG = "AdbKeeper"

    /** Returns a short human-readable summary of what happened, for logging. */
    fun enable(context: Context, source: String): String {
        val resolver = context.contentResolver

        val beforeAdb = Settings.Global.getInt(resolver, "adb_enabled", -1)
        val beforeWifi = Settings.Global.getInt(resolver, "adb_wifi_enabled", -1)

        var wroteAdb = false
        var wroteWifi = false
        var error: String? = null
        try {
            wroteAdb = Settings.Global.putInt(resolver, "adb_enabled", 1)
            wroteWifi = Settings.Global.putInt(resolver, "adb_wifi_enabled", 1)
        } catch (e: SecurityException) {
            // Happens if WRITE_SECURE_SETTINGS was never granted.
            error = e.javaClass.simpleName + ": " + e.message
        }

        val afterAdb = Settings.Global.getInt(resolver, "adb_enabled", -1)
        val afterWifi = Settings.Global.getInt(resolver, "adb_wifi_enabled", -1)

        val summary = "source=$source " +
            "adb_enabled $beforeAdb->$afterAdb (put=$wroteAdb) " +
            "adb_wifi_enabled $beforeWifi->$afterWifi (put=$wroteWifi)" +
            (error?.let { " ERROR=$it" } ?: "")

        if (error != null) {
            Log.e(TAG, summary)
        } else {
            Log.i(TAG, summary)
        }
        return summary
    }
}
