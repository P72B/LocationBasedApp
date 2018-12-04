package de.p72b.bht.wp12.Service;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Map;

import de.p72b.bht.wp12.App;
import de.p72b.bht.wp12.http.WebService;

public final class AppServices {

    public static final String WEB = "WEB";
    public static final String SETTINGS = "SETTINGS";

    private final static Map<String, Object> SERVICES = new HashMap<>();

    static {
        SERVICES.put(WEB, new WebService());
        SERVICES.put(SETTINGS, new Settings(App.getInstance()));
    }

    public AppServices() {
        // do nothing hide this
    }

    @SuppressWarnings("unchecked")
    public static <T> T getService(@Service String service) {
        return (T) SERVICES.get(service);
    }

    @StringDef({SETTINGS, WEB})
    @Retention(RetentionPolicy.SOURCE)
    @interface Service {
    }
}
