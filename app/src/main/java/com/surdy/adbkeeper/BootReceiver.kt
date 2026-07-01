package com.surdy.adbkeeper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * Re-enables wireless debugging as early as possible after a reboot, and (re)schedules the
 * periodic keep-alive worker. Handles both the normal and locked (direct-boot) variants.
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.i(AdbEnabler.TAG, "BootReceiver action=${intent.action}")
        AdbEnabler.enable(context, "boot:${intent.action}")
        KeepWorker.schedule(context)
    }
}
