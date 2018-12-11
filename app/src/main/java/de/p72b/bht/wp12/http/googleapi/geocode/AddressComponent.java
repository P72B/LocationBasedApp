package de.p72b.bht.wp12.http.googleapi.geocode;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AddressComponent {

    public final static String POSTAL_CODE = "postal_code";
    public final static String POSTAL_COUNTRY = "country";
    public final static String POSTAL_LOCALITY = "locality";
    public final static String POSTAL_ROUTE = "route";
    public final static String POSTAL_STREET_NUMBER = "street_number";

    @SerializedName("long_name")
    private String longName;

    @SerializedName("short_name")
    private String shortName;

    @SerializedName("types")
    private List<String> types;

    public List<String> getTypes() {
        return types;
    }

    public String getShortName() {
        return shortName;
    }

    public String getLongName() {
        return longName;
    }
}
