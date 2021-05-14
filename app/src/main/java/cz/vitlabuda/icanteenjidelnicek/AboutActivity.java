package cz.vitlabuda.icanteenjidelnicek;

import androidx.annotation.NonNull;
import androidx.annotation.RawRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.TextView;

import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        initializeWidgets();
    }

    private void initializeWidgets() {
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        TextView versionTextView = findViewById(R.id.version_textview);
        versionTextView.setText(String.format(getString(R.string.version), BuildConfig.VERSION_NAME));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.about_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home)
            onBackPressed();
        else if(id == R.id.about_menu_app_license)
            showDialogWithRawResourceMessage(R.string.app_license, R.raw.app_license);
        else if(id == R.id.about_menu_open_source_licenses)
            showOpenSourceLicensesActivity();
        else if(id == R.id.about_menu_privacy_policy)
            showDialogWithRawResourceMessage(R.string.privacy_policy, R.raw.privacy_policy);

        return super.onOptionsItemSelected(item);
    }

    private void showDialogWithRawResourceMessage(@StringRes int titleStringResource, @RawRes int messageRawResource) {
        new AlertDialog.Builder(this)
                .setTitle(titleStringResource)
                .setMessage(Auxiliaries.readRawResourceToString(this, messageRawResource))
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private void showOpenSourceLicensesActivity() {
        OssLicensesMenuActivity.setActivityTitle(getString(R.string.open_source_licenses));

        Intent intent = new Intent(this, OssLicensesMenuActivity.class);
        startActivity(intent);
    }

    // Easter egg :-)
    public void onAppIconImageViewClicked(View view) {
        RotateAnimation animation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(1000);

        view.startAnimation(animation);
    }
}