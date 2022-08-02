package com.pandacorp.timeui.settings;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.pandacorp.timeui.R;

public class SettingsActivity extends AppCompatActivity {
    private final String TAG = "MyLogs";

    private SharedPreferences sp;
    private SharedPreferences.Editor edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w(TAG, "SettingsActivity.onCreate");

        MySettings mySettings = new MySettings(this);
        mySettings.start();

        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
        sp = getSharedPreferences("sp", MODE_PRIVATE);
        edit = sp.edit();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //Метод обработки нажатия на кнопку home.
                Log.w(TAG, "onOptionsItemSelected: home button is pressed");
                finish();
                return super.onOptionsItemSelected(item);
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

        String language;
        String theme;

        SharedPreferences sp;

        ListPreference themes_listPreference;
        ListPreference languages_listPreference;
        Preference version_Preference;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            sp = PreferenceManager.getDefaultSharedPreferences(getContext());

            themes_listPreference = findPreference("Themes");
            themes_listPreference.setOnPreferenceChangeListener(this);

            languages_listPreference = findPreference("Languages");
            languages_listPreference.setOnPreferenceChangeListener(this);

            version_Preference = findPreference("Version");

            String version_name;
            try {
                version_name = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0).versionName;
                version_Preference.setTitle(getResources().getString(R.string.version) + " " + version_name);
                //Тут происходит добавление загаловка в виде версии к пункту настроек.

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            final String TAG = "MyLogs";

            language = sp.getString("Languages", "");
            theme = sp.getString("Themes", "");

            getActivity().recreate();

            return true;
        }


    }


}