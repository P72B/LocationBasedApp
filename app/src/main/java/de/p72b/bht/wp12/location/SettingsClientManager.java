package de.p72b.bht.wp12.location;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.support.annotation.NonNull;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

class SettingsClientManager {

    static final int REQUEST_CODE_SETTINGS = 30;
    private final Activity mActivity;
    private final List<ISettingsClientResultListener> mPendingListenerList = new ArrayList<>();

    SettingsClientManager(Activity activity) {
        mActivity = activity;
    }

    void checkIfDeviceLocationSettingFulfillRequestRequirements(
            final boolean shouldRequestSettingsChange, @NonNull final LocationRequest locationRequest,
            @NonNull final ISettingsClientResultListener listener) {
        final LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        final SettingsClient client = LocationServices.getSettingsClient(mActivity);
        final Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnFailureListener(mActivity, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                final int statusCode = ((ApiException) e).getStatusCode();
                if (statusCode != LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                    listener.onFailure("Settings resolution is not fulfilled");
                    return;
                }

                if (!shouldRequestSettingsChange) {
                    listener.onFailure("Location settings aren't met");
                    return;
                }

                // Location settings are not satisfied, but this can be fixed by showing the user a dialog
                try {
                    mPendingListenerList.add(listener);
                    // Show the dialog by calling startResolutionForResult(), and check the result in onActivityResult()
                    final ResolvableApiException resolvable = (ResolvableApiException) e;
                    resolvable.startResolutionForResult(mActivity, REQUEST_CODE_SETTINGS);
                } catch (IntentSender.SendIntentException sendEx) {
                    // Ignore the error
                    listener.onFailure("Send Intent to change location settings failed");
                }
            }
        });
        task.addOnSuccessListener(mActivity, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                listener.onSuccess();
            }
        });
    }

    void onActivityResult(int resultCode, Intent data) {
        switch (resultCode) {
            case Activity.RESULT_OK:
                callPendingListener(true);
                break;
            case Activity.RESULT_CANCELED:
                callPendingListener(false);
                break;
            default:
                // do nothing
        }
    }

    private void callPendingListener(boolean isSucceeded) {
        for (ISettingsClientResultListener listener : mPendingListenerList) {
            if (isSucceeded) {
                listener.onSuccess();
            } else {
                listener.onFailure("Settings change request canceled by user");
            }
        }
        mPendingListenerList.clear();
    }
}
