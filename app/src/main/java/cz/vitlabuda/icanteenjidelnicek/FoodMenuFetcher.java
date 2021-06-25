package cz.vitlabuda.icanteenjidelnicek;

/*
SPDX-License-Identifier: BSD-3-Clause

Copyright (c) 2021 Vít Labuda. All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
following conditions are met:
 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following
    disclaimer.
 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
    following disclaimer in the documentation and/or other materials provided with the distribution.
 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote
    products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

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
