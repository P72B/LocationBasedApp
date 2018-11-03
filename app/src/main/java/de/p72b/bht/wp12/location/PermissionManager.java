package de.p72b.bht.wp12.location;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;

import de.p72b.bht.wp12.Service.Settings;

class PermissionManager {

    private static final int REQUEST_CODE_PERMISSION = 25;
    private final Activity mActivity;
    private Settings mSettings;

    PermissionManager(@NonNull final Activity activity, @NonNull final Settings settings) {
        mActivity = activity;
        mSettings = settings;
    }

    boolean hasPermissionIfNotRequest(@NonNull final String permission) {
        final int permissionState = getPermissionStatus(permission);
        if (PermissionChecker.PERMISSION_GRANTED != permissionState) {
            requestPermission(permission, permissionState, REQUEST_CODE_PERMISSION);
            return false;
        }
        return true;
    }

    private int getPermissionStatus(@NonNull final String manifestPermissionName) {
        if (ContextCompat.checkSelfPermission(mActivity, manifestPermissionName) ==
                PackageManager.PERMISSION_GRANTED) {
            return PermissionChecker.PERMISSION_GRANTED;
        }

        if (isFirstLocationPermissionRequest(manifestPermissionName)) {
            return PermissionChecker.PERMISSION_DENIED;
        }

        if(ActivityCompat.shouldShowRequestPermissionRationale(mActivity, manifestPermissionName)){
            return PermissionChecker.PERMISSION_DENIED;
        }
        return PermissionChecker.PERMISSION_DENIED_APP_OP;
    }

    private void requestPermission(@NonNull final String permission, int permissionState,
                                   int requestCode) {
        switch (permissionState) {
            case PermissionChecker.PERMISSION_DENIED:
                ActivityCompat.requestPermissions(mActivity,
                        new String[]{permission},
                        requestCode);
                break;
            case PermissionChecker.PERMISSION_DENIED_APP_OP:
                // TODO: decide to show something for the user again
                break;
            case PermissionChecker.PERMISSION_GRANTED:
                // don't request a given permission
                break;
        }
    }

    private boolean isFirstLocationPermissionRequest(@NonNull final String manifestPermissionName) {
        return !mSettings.readFomPreferences(manifestPermissionName);
    }
}
