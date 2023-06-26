package com.pandacorp.timeui.presentation.utils

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Parcelable
import androidx.fragment.app.Fragment
import com.pandacorp.timeui.presentation.di.app.App
import com.pandacorp.timeui.presentation.ui.MainActivity

val Fragment.app get() = (requireActivity().application as App)

val Fragment.fragulaNavController get() = (requireActivity() as MainActivity).fragulaNavController!!

/**
 * A compatibility wrapper around Intent's `getParcelableExtra()` method that allows
 * developers to get a Parcelable extra from an Intent object regardless of the version of
 * Android running on the device.
 *
 * @param name The name of the extra to retrieve.
 * @param clazz The class of the extra to retrieve.
 * @return The Parcelable extra with the specified name and class, or null if it does not exist.
 */
inline fun <reified T : Parcelable> Intent.getParcelableExtraSupport(name: String, clazz: Class<T>): T? {
    val extra = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) getParcelableExtra(name, clazz)
    else @Suppress("DEPRECATION") getParcelableExtra(name) as? T
    if (extra is T) return extra
    return null
}

/**
 * A compatibility wrapper around PackageManager's `getPackageInfo()` method that allows
 * developers to use either the old flag-based API or the new enum-based API depending on the
 * version of Android running on the device.
 *
 * @param packageName The name of the package for which to retrieve package information.
 * @param flags Additional flags to control the behavior of the method.
 * @return A PackageInfo object containing information about the specified package.
 */
fun PackageManager.getPackageInfoCompat(packageName: String, flags: Int = 0): PackageInfo =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(flags.toLong()))
    } else {
        @Suppress("DEPRECATION") getPackageInfo(packageName, flags)
    }