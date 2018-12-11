package de.p72b.bht.wp12.http.googleapi.geocode;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import de.p72b.bht.wp12.AppUtils;

public class AddressResponse {

    public static final String LOCALITY = "locality";
    public static final String COUNTRY = "country";

    @SerializedName("results")
    private List<Result> mResults;

    public List<Result> getResults() {
        return mResults;
    }

    public static class Result {

        @SerializedName("formatted_address")
        private String mFormattedAddress;

        @SerializedName("geometry")
        private Geometry mGeometry;

        @SerializedName("address_components")
        private List<AddressComponent> mAddressComponents;

        public String getFormattedAddress() {
            return AppUtils.getFormattedAddress(mFormattedAddress);
        }

        public LatLng getLatLng() {
            return mGeometry.getLocation().getLatLng();
        }

        public List<AddressComponent> getAddressComponents() {
            return mAddressComponents;
        }

        @Nullable
        public String findBy(@NonNull String typeToFind) {
            for (AddressComponent component : mAddressComponents) {
                List<String> types = component.getTypes();
                for (String type : types) {
                    if (typeToFind.equals(type)) {
                        return component.getLongName();
                    }
                }
            }
            return null;
        }
    }

}
