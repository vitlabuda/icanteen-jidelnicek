package cz.vitlabuda.icanteenjidelnicek;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;

import cz.vitlabuda.icanteenextractor.FoodMenu;
import cz.vitlabuda.icanteenextractor.ICanteenExtractor;
import cz.vitlabuda.icanteenextractor.ICanteenExtractorException;

public final class FoodMenuFetcher {
    
    private static final String TAG = FoodMenuFetcher.class.getSimpleName();

    private final Context context;
    private final int canteenID;
    private final URL canteenURL;

    public FoodMenuFetcher(Context context, int canteenID, URL canteenURL) {
        this.context = context;
        this.canteenID = canteenID;
        this.canteenURL = canteenURL;
    }

    public FoodMenu fetchFoodMenu(boolean allowCache) throws ICanteenExtractorException {
        Log.d(TAG, "fetchFoodMenu: Fetching food menu... (allowCache = " + allowCache + ")");
        
        FoodMenu foodMenu;
        try {
            ICanteenExtractor iCanteenExtractor = new ICanteenExtractor();
            iCanteenExtractor.setTimeoutMilliseconds(App.CANTEEN_WEBSITE_CONNECTION_TIMEOUT);
            foodMenu = iCanteenExtractor.extract(canteenURL);

            saveFoodMenuToCache(foodMenu);

        } catch (ICanteenExtractorException e) {
            if(!allowCache)
                throw e;

            Log.d(TAG, "fetchFoodMenu: Failed to fetch food menu from the website (" + e.getMessage() + "), loading it from cache instead (if allowed)...");
            
            foodMenu = loadFoodMenuFromCache();
            if(foodMenu == null)
                throw e;
        }

        return foodMenu;
    }

    @Nullable
    private FoodMenu loadFoodMenuFromCache() {
        CanteenList.Canteen canteen = new CanteenList(context).getCanteenById(canteenID);

        byte[] cache = canteen.getCache();
        if(cache == null)
            return null;

        try (
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(cache);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)
        ) {
            return (FoodMenu) objectInputStream.readObject();

        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            return null;
        }
    }

    private void saveFoodMenuToCache(FoodMenu foodMenu) {
        byte[] newCache = Auxiliaries.serializeFoodMenuObject(foodMenu);

        CanteenList canteenList = new CanteenList(context);
        canteenList.modifyCanteenCache(canteenID, newCache);
    }
}
