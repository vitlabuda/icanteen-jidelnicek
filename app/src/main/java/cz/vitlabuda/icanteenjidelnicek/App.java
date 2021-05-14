package cz.vitlabuda.icanteenjidelnicek;

import android.app.Application;

import java.util.TreeMap;

public class App extends Application {
    public static final int CANTEEN_WEBSITE_CONNECTION_TIMEOUT = 5000; // in milliseconds

    // TreeMap is always sorted by key (= canteen name) alphabetically.
    public static final TreeMap<String, String> PREDEFINED_CANTEEN_MAP;

    static {
        PREDEFINED_CANTEEN_MAP = new TreeMap<>();

        PREDEFINED_CANTEEN_MAP.put("Gymnázium a OA Svitavy", "https://strav.nasejidelna.cz/0051/login");
        PREDEFINED_CANTEEN_MAP.put("Internátní školní jídelna Svitavy", "https://strav.nasejidelna.cz/0057/login");
        PREDEFINED_CANTEEN_MAP.put("ZŠ Felberova Svitavy", "http://82.117.143.136:8082/faces/login.jsp");
        PREDEFINED_CANTEEN_MAP.put("ZŠ náměstí Míru Svitavy", "https://strav.nasejidelna.cz/0057/login");
        PREDEFINED_CANTEEN_MAP.put("ZŠ Riegrova Svitavy", "https://strav.nasejidelna.cz/0057/login");
        PREDEFINED_CANTEEN_MAP.put("ZŠ Sokolovská Svitavy", "https://strav.nasejidelna.cz/0051/login");
        PREDEFINED_CANTEEN_MAP.put("ZŠ T. G. Masaryka Svitavy", "https://strav.nasejidelna.cz/0120/login");
    }
}
