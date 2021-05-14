package cz.vitlabuda.icanteenjidelnicek;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public final class FavoriteCanteenManager {
    private static final String TAG = FavoriteCanteenManager.class.getSimpleName();
    private static final String FAVORITE_CANTEEN_PREFERENCE_KEY = "cz.vitlabuda.icanteenjidelnicek.FavoriteCanteenManager.FAVORITE_CANTEEN_PREFERENCE_KEY";

    private final Context context;

    public FavoriteCanteenManager(Context context) {
        this.context = context;
    }

    public int getFavoriteCanteenID() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        return preferences.getInt(FAVORITE_CANTEEN_PREFERENCE_KEY, -1);
    }

    public void setFavoriteCanteenID(int favoriteCanteenID) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();

        editor.putInt(FAVORITE_CANTEEN_PREFERENCE_KEY, favoriteCanteenID);
        editor.commit();

        Log.d(TAG, "setFavoriteCanteenID: Favorite canteen ID set to " + favoriteCanteenID);
    }

    public boolean isCurrentFavoriteCanteenID(int compareTo) {
        return (getFavoriteCanteenID() == compareTo);
    }
}
