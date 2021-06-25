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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cz.vitlabuda.icanteenextractor.FoodMenu;
import cz.vitlabuda.icanteenextractor.ICanteenExtractorException;
import cz.vitlabuda.icanteenjidelnicek.adapters.DayListViewPagerAdapter;

public class FoodMenuActivity extends AppCompatActivity {

    private static final String TAG = FoodMenuActivity.class.getSimpleName();

    public static final String EXTRA_CANTEEN = "cz.vitlabuda.icanteenjidelnicek.FoodMenuActivity.EXTRA_CANTEEN";
    private static final String INSTANCE_STATE_FOOD_MENU = "cz.vitlabuda.icanteenjidelnicek.FoodMenuActivity.INSTANCE_STATE_FOOD_MENU";

    private final SimpleDateFormat TABLAYOUT_DATE_FORMATTER = new SimpleDateFormat("EEE d.M.", Locale.getDefault());

    private int canteenID;
    private URL canteenURL;
    private FoodMenu foodMenu = null;

    private TabLayoutMediator tabLayoutMediator = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_menu);


        CanteenList.Canteen intentCanteen = (CanteenList.Canteen) getIntent().getSerializableExtra(EXTRA_CANTEEN);
        this.canteenID = intentCanteen.getId();
        this.canteenURL = intentCanteen.getUrl();
        setTitle(intentCanteen.getName());

        if(savedInstanceState != null)
            foodMenu = (FoodMenu) savedInstanceState.getSerializable(INSTANCE_STATE_FOOD_MENU);


        if(savedInstanceState == null && foodMenu == null) // on activity's first creation (on activity start)
            refreshFoodMenu(true);
        else
            refreshFoodMenuView(false);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable(INSTANCE_STATE_FOOD_MENU, foodMenu);

        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.food_menu_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.food_menu_menu_refresh)
            refreshFoodMenu(false);

        return super.onOptionsItemSelected(item);
    }

    private void refreshFoodMenu(boolean allowCache) {
        ProgressDialog progressDialog = Auxiliaries.generateProgressDialog(this, R.string.fetching_food_menu_progress_dialog_message);
        progressDialog.show();

        new Thread(() -> {
            Log.d(TAG, "refreshFoodMenu: Fetching food menu from URL " + canteenURL + "...");
            
            FoodMenu newFoodMenu;
            try {
                FoodMenuFetcher foodMenuFetcher = new FoodMenuFetcher(FoodMenuActivity.this, canteenID, canteenURL);
                newFoodMenu = foodMenuFetcher.fetchFoodMenu(allowCache);

            } catch (ICanteenExtractorException e) {
                Log.d(TAG, "refreshFoodMenu: Failed to fetch food menu: " + e.getMessage());
                runOnUiThread(() -> showFoodMenuFetchErrorDialog(e.getMessage()));
                return;

            } finally {
                runOnUiThread(progressDialog::dismiss);
            }

            Log.d(TAG, "refreshFoodMenu: Food menu was successfully fetched!");
            runOnUiThread(() -> {
                foodMenu = newFoodMenu;
                refreshFoodMenuView(true);
            });
        }).start();
    }

    private void refreshFoodMenuView(boolean newFoodMenuFetched) {
        if(foodMenu == null)
            return;

        Log.d(TAG, "refreshFoodMenuView: Refreshing food menu view... (newFoodMenuFetched = " + newFoodMenuFetched + ")");


        DayListViewPagerAdapter adapter = new DayListViewPagerAdapter(foodMenu);

        ViewPager2 dayListViewPager = findViewById(R.id.day_list_viewpager);
        dayListViewPager.setAdapter(adapter);

        if(newFoodMenuFetched)
            dayListViewPager.post(() -> dayListViewPager.setCurrentItem(getCurrentDayPositionInFoodMenu()));


        if(tabLayoutMediator != null)
            tabLayoutMediator.detach();

        TabLayout tabLayout = findViewById(R.id.day_list_tablayout);
        tabLayoutMediator = new TabLayoutMediator(tabLayout, dayListViewPager, (tab, position) -> {
            String formattedDate = TABLAYOUT_DATE_FORMATTER.format(adapter.getDateOfDay(position));
            tab.setText(formattedDate);
        });
        tabLayoutMediator.attach();
    }

    private void showFoodMenuFetchErrorDialog(String errorMessage) {
        String message = String.format(getString(R.string.food_menu_fetch_error_dialog_message), errorMessage);

        new AlertDialog.Builder(this)
                .setTitle(R.string.food_menu_fetch_error_dialog_title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    // Returns 0 (= the first day) if today's date is not found in the food menu.
    private int getCurrentDayPositionInFoodMenu() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String todayDateString = formatter.format(new Date());

        for(int i = 0; i < foodMenu.getDays().size(); i++) {
            if(formatter.format(foodMenu.getDays().get(i).getDate()).equals(todayDateString))
                return i;
        }

        return 0;
    }

    public void orderFoodFABClicked(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(canteenURL.toString()));
        startActivity(intent);
    }
}