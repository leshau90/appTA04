package com.tap.ilman.ta04;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Pengaturan extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

    }
}
