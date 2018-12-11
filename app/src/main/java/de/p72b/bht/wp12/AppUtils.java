package de.p72b.bht.wp12;

public class AppUtils {

    public static String getFormattedAddress(String formattedAddress) {
        String[] split = formattedAddress.split(",");
        if (split.length == 0) {
            return formattedAddress;
        }
        return split[0];
    }
}
