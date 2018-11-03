package de.p72b.bht.wp12.location;

import android.support.annotation.NonNull;

public interface ISettingsClientResultListener {
    void onSuccess();

    void onFailure(@NonNull final String message);
}
