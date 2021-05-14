package cz.vitlabuda.icanteenjidelnicek;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;

import java.net.MalformedURLException;
import java.net.URL;

import cz.vitlabuda.icanteenextractor.FoodMenu;
import cz.vitlabuda.icanteenextractor.ICanteenExtractor;
import cz.vitlabuda.icanteenextractor.ICanteenExtractorException;
import cz.vitlabuda.icanteenjidelnicek.adapters.CanteenListRecyclerAdapter;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private CanteenListRecyclerAdapter canteenListRecyclerAdapter = null;
    private SnackbarMaker snackbarMaker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        snackbarMaker = new SnackbarMaker(findViewById(android.R.id.content));

        initializeWidgets();

        if(savedInstanceState == null) // on activity's first creation (on activity start)
            showFavoriteCanteenIfPossible();
    }

    private void initializeWidgets() {
        this.canteenListRecyclerAdapter = new CanteenListRecyclerAdapter(this, new CanteenListRecyclerAdapter.CanteenRecyclerActionListener() {
            @Override
            public void onClick(CanteenListRecyclerAdapter adapter, CanteenList.Canteen canteen, int position) {
                goToFoodMenuActivity(canteen);
            }

            @Override
            public void onSwipe(CanteenListRecyclerAdapter adapter, CanteenList.Canteen canteen, int position) {
                String message = String.format(getString(R.string.remove_canteen_dialog_message), canteen.getName());

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.remove_canteen_dialog_title)
                        .setMessage(message)
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> adapter.removeCanteen(position))
                        .setNegativeButton(android.R.string.no, null)
                        .setOnDismissListener(dialog -> adapter.canteenListMightHaveBeenChanged())
                        .show();
            }

            @Override
            public void onSetAsFavoriteClick(CanteenListRecyclerAdapter adapter, CanteenList.Canteen canteen, int position) {
                FavoriteCanteenManager favoriteCanteenManager = new FavoriteCanteenManager(MainActivity.this);
                boolean isCurrentFavoriteCanteenID = favoriteCanteenManager.isCurrentFavoriteCanteenID(canteen.getId());
                favoriteCanteenManager.setFavoriteCanteenID(isCurrentFavoriteCanteenID ? -1 : canteen.getId());

                adapter.canteenListMightHaveBeenChanged();

                if(!isCurrentFavoriteCanteenID)
                    snackbarMaker.make(String.format(getString(R.string.canteen_was_set_as_favorite), canteen.getName()), Snackbar.LENGTH_LONG);
            }
        });

        RecyclerView canteenListRecyclerView = findViewById(R.id.canteen_list_recyclerview);
        canteenListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        canteenListRecyclerView.setAdapter(canteenListRecyclerAdapter);
    }

    private void showFavoriteCanteenIfPossible() {
        int favoriteCanteenID = new FavoriteCanteenManager(this).getFavoriteCanteenID();

        CanteenList.Canteen favoriteCanteen;
        try {
            favoriteCanteen = new CanteenList(this).getCanteenById(favoriteCanteenID);
        } catch (CanteenList.CanteenNotFoundException e) {
            return;
        }

        Log.d(TAG, String.format("showFavoriteCanteenIfPossible: The canteen with ID %d (%s) is set as favorite, automatically showing it...", favoriteCanteen.getId(), favoriteCanteen.getName()));
        goToFoodMenuActivity(favoriteCanteen);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.main_menu_add_canteen)
            showAddCanteenDialog();
        else if(id == R.id.main_menu_about)
            goToAboutActivity();

        return super.onOptionsItemSelected(item);
    }

    private void showAddCanteenDialog() {
        if(canteenListRecyclerAdapter == null)
            return;

        View view = getLayoutInflater().inflate(R.layout.dialog_add_canteen, null, false);
        EditText nameEditText = view.findViewById(R.id.dialog_add_canteen_name);
        EditText urlEditText = view.findViewById(R.id.dialog_add_canteen_url);

        Button selectButton = view.findViewById(R.id.dialog_add_canteen_select);
        selectButton.setOnClickListener(v -> showCanteenSelectionDialog(nameEditText, urlEditText));

        new AlertDialog.Builder(this)
                .setTitle(R.string.add_canteen_dialog_title)
                .setView(view)
                .setPositiveButton(R.string.add_canteen_dialog_add, (dialog, which) -> {
                    String name = nameEditText.getText().toString().trim();
                    String url = urlEditText.getText().toString().trim();

                    validateAndAddCanteen(name, url);
                })
                .setNegativeButton(R.string.add_canteen_dialog_cancel, null)
                .show();
    }

    private void showCanteenSelectionDialog(EditText nameEditText, EditText urlEditText) {
        String[] dialogItems = App.PREDEFINED_CANTEEN_MAP.keySet().toArray(new String[0]);

        new AlertDialog.Builder(this)
                .setTitle(R.string.select_canteen_dialog_title)
                .setItems(dialogItems, (dialog, which) -> {
                    String selectedName = dialogItems[which];

                    nameEditText.setText(selectedName);
                    urlEditText.setText(App.PREDEFINED_CANTEEN_MAP.get(selectedName));
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void validateAndAddCanteen(String name, String url) {
        if(name.isEmpty()) {
            showAddCanteenErrorDialog(R.string.add_canteen_error_dialog_empty_name);
            return;
        }

        if(url.isEmpty()) {
            showAddCanteenErrorDialog(R.string.add_canteen_error_dialog_empty_url);
            return;
        }

        URL urlObject;
        try {
            urlObject = new URL(url);
        } catch (MalformedURLException e) {
            showAddCanteenErrorDialog(R.string.add_canteen_error_dialog_invalid_url);
            return;
        }


        ProgressDialog progressDialog = Auxiliaries.generateProgressDialog(this, R.string.validating_canteen_progress_dialog_message);
        progressDialog.show();

        new Thread(() -> {
            Log.d(TAG, "validateAndAddCanteen: Validating canteen -> fetching " + urlObject.toString() + "...");
            
            byte[] serializedFoodMenu;
            try {
                ICanteenExtractor iCanteenExtractor = new ICanteenExtractor();
                iCanteenExtractor.setTimeoutMilliseconds(App.CANTEEN_WEBSITE_CONNECTION_TIMEOUT);

                FoodMenu foodMenu = iCanteenExtractor.extract(urlObject);
                serializedFoodMenu = Auxiliaries.serializeFoodMenuObject(foodMenu);

            } catch (ICanteenExtractorException e) {
                Log.d(TAG, "validateAndAddCanteen: Canteen validation failed: " + e.getMessage());
                runOnUiThread(() -> {
                    String message = String.format(getString(R.string.add_canteen_error_dialog_extractor_error), e.getMessage());
                    showAddCanteenErrorDialog(message);
                });
                return;

            } finally {
                runOnUiThread(progressDialog::dismiss);
            }

            Log.d(TAG, "validateAndAddCanteen: Canteen validation succeeded, adding it to the canteen list...");
            runOnUiThread(() -> {
                if(canteenListRecyclerAdapter == null)
                    return;

                canteenListRecyclerAdapter.addCanteen(name, url, serializedFoodMenu);
            });
        }).start();
    }

    private void showAddCanteenErrorDialog(int messageResource) {
        showAddCanteenErrorDialog(getString(messageResource));
    }

    private void showAddCanteenErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.add_canteen_dialog_title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private void goToFoodMenuActivity(CanteenList.Canteen canteen) {
        Intent intent = new Intent(this, FoodMenuActivity.class);
        intent.putExtra(FoodMenuActivity.EXTRA_CANTEEN, canteen);

        startActivity(intent);
    }

    private void goToAboutActivity() {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    public void addCanteenButtonClicked(View view) {
        showAddCanteenDialog();
    }
}
