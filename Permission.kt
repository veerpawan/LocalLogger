package com.amvac.amvacrfid.utils.logfile
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.app.ActivityCompat

object Permission {

    /**
     * Check if version is marshmallow and above. Used in deciding to ask runtime permission
     */

    private fun shouldAskPermission(): Boolean {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
    }

    /**
     * permission is already granted or not
     */
    fun shouldAskPermission(context: Context, permission: String): Boolean {

        if (shouldAskPermission()) {

            val permissionResult = ActivityCompat.checkSelfPermission(context, permission)

            if (permissionResult != PackageManager.PERMISSION_GRANTED) {
                return true
            }
        }
        return false
    }
}