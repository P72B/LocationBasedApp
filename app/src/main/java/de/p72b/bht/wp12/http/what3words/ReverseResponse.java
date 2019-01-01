package de.p72b.bht.wp12.http.what3words;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class ReverseResponse {
    @SerializedName("words")
    private String mWords;

    @Nullable
    public String getWords() {
        return mWords;
    }
}
