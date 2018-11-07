/*
 * Copyright (C) 2017-2018 The Dirty Unicorns Project
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

package com.android.settings.display;

import android.app.Fragment;
import android.content.Context;
import android.os.UserHandle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.support.v7.preference.PreferenceScreen;

import com.android.settings.core.PreferenceControllerMixin;

import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnResume;

import com.android.settings.display.ThemePicker;

import com.android.internal.util.viper.Utils;

public class SystemThemePreferenceController extends AbstractPreferenceController
        implements PreferenceControllerMixin, LifecycleObserver, OnResume {

    private static final String SYSTEM_THEME = "system_theme_style";
    private static final String SUBS_PACKAGE = "projekt.substratum";

    private static final int MY_USER_ID = UserHandle.myUserId();

    private final Fragment mParent;
    private Preference mSystemThemeStyle;

    public SystemThemePreferenceController(Context context, Lifecycle lifecycle, Fragment parent) {
        super(context);
        mParent = parent;
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        mSystemThemeStyle  = (Preference) screen.findPreference(SYSTEM_THEME);
        if (!Utils.isPackageInstalled(mContext, SUBS_PACKAGE)) {
            mSystemThemeStyle.setEnabled(true);
        } else {
            mSystemThemeStyle.setEnabled(false);
        }
    }

    @Override
    public void onResume() {
        updateEnableState();
        updateSummary();
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public String getPreferenceKey() {
        return SYSTEM_THEME;
    }

    public void updateEnableState() {
        if (mSystemThemeStyle == null) {
            return;
        }

        mSystemThemeStyle.setOnPreferenceClickListener(
            new OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                   if (!Utils.isPackageInstalled(mContext, SUBS_PACKAGE)) {
                        ThemePicker.show(mParent);
                        return true;
                   } else {
                        return false;
                   }
                }
            });
    }

    public void updateSummary() {
        if (mSystemThemeStyle != null) {
            if (!Utils.isPackageInstalled(mContext, SUBS_PACKAGE)) {
                mSystemThemeStyle.setSummary(mContext.getString(
                        com.android.settings.R.string.theme_picker_summary));
            } else {
                mSystemThemeStyle.setSummary(mContext.getString(
                        com.android.settings.R.string.disable_themes_installed_title));
            }
        }
    }
}