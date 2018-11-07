/*
 * Copyright (C) 2018 The Dirty Unicorns Project
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

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.om.IOverlayManager;
import android.os.Bundle;
import android.os.ServiceManager;
import android.os.RemoteException;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.android.internal.logging.nano.MetricsProto;
import com.android.internal.statusbar.ThemeAccentUtils;

import com.android.settings.R;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settingslib.drawer.SettingsDrawerActivity;

public class ThemePicker extends InstrumentedDialogFragment implements OnClickListener {

    private static final String TAG_THEME_PICKER = "theme_picker";

    private View mView;

    private IOverlayManager mOverlayManager;
    private int mCurrentUserId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOverlayManager = IOverlayManager.Stub.asInterface(
                ServiceManager.getService(Context.OVERLAY_SERVICE));
        mCurrentUserId = ActivityManager.getCurrentUser();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mView = LayoutInflater.from(getActivity()).inflate(R.layout.theme_picker, null);

        if (mView != null) {
            initView();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(mView)
                .setNegativeButton(R.string.cancel, this)
                .setNeutralButton(R.string.theme_picker_auto, this)
                .setCancelable(false);

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    private void initView() {

        Button userThemeOne = mView.findViewById(R.id.userThemeOne);
        setTheme("1", userThemeOne);

        Button userThemeTwo = mView.findViewById(R.id.userThemeTwo);
        setTheme("2", userThemeTwo);

        Button userThemeThree = mView.findViewById(R.id.userThemeThree);
        setTheme("3", userThemeThree);

        Button userThemeFour = mView.findViewById(R.id.userThemeFour);
        setTheme("4", userThemeFour);

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        ContentResolver resolver = getActivity().getContentResolver();

        if (which == AlertDialog.BUTTON_NEGATIVE) {
            dismiss();
        }
        if (which == AlertDialog.BUTTON_NEUTRAL) {
            Settings.System.putIntForUser(resolver,
                    Settings.System.SYSTEM_THEME, 0, mCurrentUserId);
            dismiss();
        }
    }

    public static void show(Fragment parent) {
        if (!parent.isAdded()) return;

        final ThemePicker dialog = new ThemePicker();
        dialog.setTargetFragment(parent, 0);
        dialog.show(parent.getFragmentManager(), TAG_THEME_PICKER);
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.VENOM;
    }

    private void setTheme(final String theme, final Button buttonTheme) {
        final ContentResolver resolver = getActivity().getContentResolver();
        if (buttonTheme != null) {
            buttonTheme.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Settings.System.putIntForUser(resolver,
                            Settings.System.SYSTEM_THEME, Integer.parseInt(theme), mCurrentUserId);
                    dismiss();
                }
            });
        }
    }
}
