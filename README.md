# ADB Keeper

ADB Keeper is a small Android TV app that keeps **Wireless debugging** enabled across
reboots. It was created for a Chromecast with Google TV 4K (`sabrina`), where Android
resets `Settings.Global.adb_wifi_enabled` to `0` during every boot.

Once installed and granted `WRITE_SECURE_SETTINGS`, the app:

- enables `adb_enabled` and `adb_wifi_enabled` when launched;
- re-enables them after `BOOT_COMPLETED` or `LOCKED_BOOT_COMPLETED`; and
- reapplies them every 15 minutes as a safeguard against the settings being cleared.

This restores the wireless ADB TLS listener for an already-paired host without requiring
root access, TV interaction, or a new pairing code. The TLS port can change after each
boot, so clients should discover it through ADB mDNS instead of hardcoding a port.

## Requirements

- Android 8.0 (API 26) or newer
- An existing ADB connection for installation and the one-time permission grant
- JDK 17 and Android SDK 34 to build from source

The app is tested on Chromecast with Google TV 4K (`sabrina`, API 34). Behavior may vary
on other Android or Google TV devices.

## Build

```bash
./gradlew :app:assembleDebug
```

The APK is written to:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Install

Replace `<device>` with the device's current ADB serial or address:

```bash
adb -s <device> install -r app/build/outputs/apk/debug/app-debug.apk
adb -s <device> shell pm grant \
  com.surdy.adbkeeper android.permission.WRITE_SECURE_SETTINGS
adb -s <device> shell monkey -p com.surdy.adbkeeper 1
```

Launch the app at least once after installation. Android does not deliver boot broadcasts
to an app that has never been launched or is force-stopped.

After a reboot, an already-paired host should reconnect automatically through mDNS. To
inspect or connect to the advertised endpoint manually:

```bash
adb mdns services
adb connect <device-ip>:<advertised-tls-port>
```

## Verify

```bash
adb -s <device> shell settings get global adb_enabled
adb -s <device> shell settings get global adb_wifi_enabled
adb -s <device> logcat -s AdbKeeper
```

Both settings should report `1`. A boot log should show `adb_wifi_enabled` changing from
`0` to `1` if the device reset it.

## Disable or uninstall

Uninstall the app before turning Wireless debugging off, otherwise its periodic worker
may enable the setting again:

```bash
adb -s <device> uninstall com.surdy.adbkeeper
```

Then disable Wireless debugging from Android's developer options.

## Security

Wireless ADB grants substantial control over the device. Use ADB Keeper only on devices
and networks you trust, keep the list of paired hosts restricted, and disable or uninstall
the app when persistent remote debugging is no longer needed.

## License

[MIT](LICENSE)
