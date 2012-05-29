/*
 * Copyright (C) 2012 CyanogenMod
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.cyanogenmod;

import android.content.ContentResolver;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

public class LockscreenInterface extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {
    private static final String TAG = "LockscreenInterface";

    private static final String KEY_LOCKSCREEN_ALIGNMENT = "lockscreen_alignment";
    private static final String LOCKSCREEN_COLOR = "lockscreen_color";
    public static final String KEY_WEATHER_PREF = "lockscreen_weather";
    public static final String KEY_CALENDAR_PREF = "lockscreen_calendar";

    private Preference mColor;
    private Preference mWeatherPref;
    private ListPreference mLockscreenAlignment;
    private Preference mCalendarPref;

    ContentResolver mResolver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mResolver = getActivity().getContentResolver();

        addPreferencesFromResource(R.xml.lockscreen_interface_settings);
        mWeatherPref = (Preference) findPreference(KEY_WEATHER_PREF);
        mColor = (Preference) findPreference(LOCKSCREEN_COLOR);
        mLockscreenAlignment = (ListPreference) findPreference(KEY_LOCKSCREEN_ALIGNMENT);
        int lockscreenAlignment = Settings.System.getInt(getActivity().getApplicationContext()
                .getContentResolver(), Settings.System.LOCKSCREEN_ALIGNMENT, 0);
        mLockscreenAlignment.setValue(String.valueOf(lockscreenAlignment));
        updateLockscreenAlignmentSummary();
        mLockscreenAlignment.setOnPreferenceChangeListener(this);
        mCalendarPref = (Preference) findPreference(KEY_CALENDAR_PREF);

        if (!Utils.isScreenLarge(getResources())) {
            mLockscreenAlignment.setEnabled(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateState();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void updateState() {
        // Set the weather description text
        if (mWeatherPref != null) {
            boolean weatherEnabled = Settings.System.getInt(mResolver,
                    Settings.System.LOCKSCREEN_WEATHER, 0) == 1;
            if (weatherEnabled) {
                mWeatherPref.setSummary(R.string.lockscreen_weather_enabled);
            } else {
                mWeatherPref.setSummary(R.string.lockscreen_weather_summary);
            }
        }

        // Set the calendar description text
        if (mCalendarPref != null) {
            boolean weatherEnabled = Settings.System.getInt(mResolver,
                    Settings.System.LOCKSCREEN_CALENDAR, 0) == 1;
            if (weatherEnabled) {
                mCalendarPref.setSummary(R.string.lockscreen_calendar_enabled);
            } else {
                mCalendarPref.setSummary(R.string.lockscreen_calendar_summary);
            }
        }
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        final String key = preference.getKey();
        if (preference == mLockscreenAlignment) {
            int alignment = Integer.valueOf((String) objValue);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.LOCKSCREEN_ALIGNMENT, alignment);
            updateLockscreenAlignmentSummary();
        }
        return true;
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;
        if (preference == mColor) {
            ColorPickerDialog cp = new ColorPickerDialog(getActivity(),
                    mColorListener, Settings.System.getInt(getActivity()
                    .getApplicationContext()
                    .getContentResolver(), Settings.System.LOCKSCREEN_COLOR, 0x70000000));
            cp.setDefaultColor(0x70000000);
            cp.show();
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    ColorPickerDialog.OnColorChangedListener mColorListener =
        new ColorPickerDialog.OnColorChangedListener() {
            public void colorChanged(int color) {
                Settings.System.putInt(getContentResolver(),
                        Settings.System.LOCKSCREEN_COLOR, color);
            }
            public void colorUpdate(int color) {
            }
    };

    private void updateLockscreenAlignmentSummary() {
        // Update summary message with current value
        int currentAlignment = Settings.System.getInt(getActivity().getApplicationContext()
                .getContentResolver(), Settings.System.LOCKSCREEN_ALIGNMENT, 0);
        final CharSequence[] entries = mLockscreenAlignment.getEntries();
        final CharSequence[] values = mLockscreenAlignment.getEntryValues();
        int best = 0;
        for (int i = 0; i < values.length; i++) {
            int alignment = Integer.valueOf(values[i].toString());
            if (currentAlignment >= alignment) {
                best = i;
            }
        }
        mLockscreenAlignment.setSummary(entries[best]);
    }
}
