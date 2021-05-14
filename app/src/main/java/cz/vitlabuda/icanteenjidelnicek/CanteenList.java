package cz.vitlabuda.icanteenjidelnicek;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public final class CanteenList {
    public static final class CanteenNotFoundException extends AppRuntimeException {
        public CanteenNotFoundException(String message) {
            super(message);
        }
    }

    private static final class CanteenListDBHelper extends SQLiteOpenHelper {
        private static final class CanteenListEntry implements BaseColumns {
            public static final String TABLE_NAME = "canteenList";
            public static final String COLUMN_NAME = "name";
            public static final String COLUMN_URL = "url";
            public static final String COLUMN_CACHE = "cache";
        }

        public static final String DATABASE_NAME = "canteenList.db";
        public static final int DATABASE_VERSION = 1;

        public CanteenListDBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            final String SQL_COMMAND = String.format(
                    "CREATE TABLE %s (%s INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, %s TEXT NOT NULL, %s TEXT NOT NULL, %s BLOB NOT NULL);",
                    CanteenListEntry.TABLE_NAME, CanteenListEntry._ID, CanteenListEntry.COLUMN_NAME, CanteenListEntry.COLUMN_URL, CanteenListEntry.COLUMN_CACHE
            );

            db.execSQL(SQL_COMMAND);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            throw new AppRuntimeException("The canteen list database isn't supposed to be upgraded!");
        }
    }

    public static final class Canteen implements Serializable {
        private final int id;
        private final String name;
        private final URL url;
        private final byte[] cache;

        public Canteen(int id, String name, String url, byte[] cache) throws MalformedURLException {
            this.id = id;
            this.name = name;
            this.url = new URL(url);
            this.cache = cache;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public URL getUrl() {
            return url;
        }

        public byte[] getCache() {
            return cache;
        }
    }

    private static final String TAG = CanteenList.class.getSimpleName();

    private final Context context;
    private final Canteen[] canteens;

    public CanteenList(Context context) {
        final String ORDER_BY = String.format("%s ASC", CanteenListDBHelper.CanteenListEntry.COLUMN_NAME);

        ArrayList<Canteen> canteensArrayList = new ArrayList<>();
        SQLiteDatabase database = new CanteenListDBHelper(context).getReadableDatabase();

        try(Cursor cursor = database.query(CanteenListDBHelper.CanteenListEntry.TABLE_NAME, null, null, null, null, null, ORDER_BY)) {
            while(cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(CanteenListDBHelper.CanteenListEntry._ID));
                String name = cursor.getString(cursor.getColumnIndex(CanteenListDBHelper.CanteenListEntry.COLUMN_NAME));
                String url = cursor.getString(cursor.getColumnIndex(CanteenListDBHelper.CanteenListEntry.COLUMN_URL));
                byte[] cache = cursor.getBlob(cursor.getColumnIndex(CanteenListDBHelper.CanteenListEntry.COLUMN_CACHE));

                Canteen canteen;
                try {
                    canteen = new Canteen(id, name, url, cache);
                } catch (MalformedURLException e) {
                    // This shouldn't ever happen due to the app's architecture.
                    throw new AppRuntimeException("The database contains a malformed URL!");
                }

                canteensArrayList.add(canteen);
            }
        }

        this.context = context;
        this.canteens = canteensArrayList.toArray(new Canteen[0]);
    }

    public Canteen[] getCanteens() {
        return canteens;
    }

    public Canteen getCanteenById(int id) {
        for(Canteen canteen : canteens) {
            if(canteen.getId() == id)
                return canteen;
        }

        // This shouldn't ever happen due to the app's architecture.
        throw new CanteenNotFoundException("The canteen with ID " + id + " wasn't found in the database!");
    }

    // Called only from MainActivity - the whole canteen list needs to be returned.
    public CanteenList addCanteen(String name, String url, byte[] initialCache) {
        String canonicalURL;
        try {
            canonicalURL = new URL(url).toString();
        } catch (MalformedURLException e) {
            // This shouldn't ever happen due to the app's architecture.
            throw new AppRuntimeException("The added canteen's URL is invalid!");
        }

        Log.d(TAG, String.format("addCanteen: Adding a new canteen with name %s and URL %s to the canteen list...", name, canonicalURL));

        ContentValues contentValues = new ContentValues();
        contentValues.put(CanteenListDBHelper.CanteenListEntry.COLUMN_NAME, name);
        contentValues.put(CanteenListDBHelper.CanteenListEntry.COLUMN_URL, canonicalURL);
        contentValues.put(CanteenListDBHelper.CanteenListEntry.COLUMN_CACHE, initialCache);

        SQLiteDatabase database = new CanteenListDBHelper(context).getWritableDatabase();
        database.insert(CanteenListDBHelper.CanteenListEntry.TABLE_NAME, null, contentValues);

        return new CanteenList(context);
    }

    // Called only from MainActivity - the whole canteen list needs to be returned.
    public CanteenList removeCanteen(int id) {
        Log.d(TAG, "removeCanteen: Removing canteen with ID " + id + " from the canteen list...");
        
        SQLiteDatabase database = new CanteenListDBHelper(context).getWritableDatabase();
        database.delete(CanteenListDBHelper.CanteenListEntry.TABLE_NAME,
                CanteenListDBHelper.CanteenListEntry._ID + " = ?",
                new String[] { String.valueOf(id) });

        return new CanteenList(context);
    }

    // Called only from FoodMenuActivity - nothing needs to be returned.
    public void modifyCanteenCache(int id, byte[] newCache) {
        Log.d(TAG, "modifyCanteenCache: Modifying the cache of canteen with ID " + id + "...");

        ContentValues contentValues = new ContentValues();
        contentValues.put(CanteenListDBHelper.CanteenListEntry.COLUMN_CACHE, newCache);

        SQLiteDatabase database = new CanteenListDBHelper(context).getWritableDatabase();
        database.update(CanteenListDBHelper.CanteenListEntry.TABLE_NAME, contentValues,
                CanteenListDBHelper.CanteenListEntry._ID + " = ?",
                new String[] { String.valueOf(id) });
    }
}
