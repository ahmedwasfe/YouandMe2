package com.ahmet.postphotos.UI.Account.Settings;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ahmet.postphotos.R;
import com.ahmet.postphotos.UI.Main.Home.MainActivity;

import java.util.Locale;

public class LanguageSettings extends AppCompatActivity {

    private Toolbar mToolbar;
    private ListView mListLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_settings);

        mToolbar = findViewById(R.id.toolbar_language_settings);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.language_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mListLanguage = findViewById(R.id.list_change_language);


        String [] language = getResources().getStringArray(R.array.language);
        ArrayAdapter<String> adapter = new ArrayAdapter(this,R.layout.raw_status,R.id.text_select_status,language);
        mListLanguage.setAdapter(adapter);

        mListLanguage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

//                Toast.makeText(SettingsItems.this, adapterView.getItemAtPosition(position)+"", Toast.LENGTH_SHORT).show();
//                changeLanguage(adapterView.getItemAtPosition(position).toString());

//                String language = adapterView.getItemAtPosition(position).toString();
//                Toast.makeText(SettingsItems.this, language, Toast.LENGTH_SHORT).show();

                if (position == 0){
                    changeLanguage("ar");
                }else if (position == 1){
                    changeLanguage("en");
                }else if (position == 2){
                    changeLanguage("fr");
                }else if (position == 3){
                    changeLanguage("tu");
                }else if (position == 4){
                    changeLanguage("es");
                }else if (position == 5){
                    changeLanguage("it");
                }

                startActivity(new Intent(LanguageSettings.this, Settings.class));
                finish();
            }
        });

    }


    private void changeLanguage(String language){

        Locale myLocale = new Locale(language);

        DisplayMetrics display = getResources().getDisplayMetrics();
        Configuration config = getResources().getConfiguration();

        config.locale = myLocale;
        getResources().updateConfiguration(config, display);
        Intent myIntent = new Intent(this,MainActivity.class);
        startActivity(myIntent);
    }



    /*
     * Arabic = "ar"
     * English = "en"
     * Hindi - "hi"
     * French = "fr"
     * Italiano = "it"
     * Greeman = "de"
     * Espnish = "es"
     * Japanes = "ja"
     * Korean = "ko"
     * Dutch = "nl"
     * Poriugueses = "pt"
     * Russian = "ru"
     * Chinese "zh"
     *
     * */
}
