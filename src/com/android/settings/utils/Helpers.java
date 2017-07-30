package com.android.settings.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.UserHandle;

import com.android.settings.R;

public class Helpers {
    // avoids hardcoding the tag
    private static final String TAG = Thread.currentThread().getStackTrace()[1].getClassName();

    public Helpers() {
        // dummy constructor
    }

    public static void restartSystemUI(Context context) {
        new RestartSystemUITask().execute(context);
    }

    public static void showSystemUIrestartDialog(Activity a) {
        showSystemUIrestartDialog(a, a.getResources().getString(R.string.systemui_restart_title), a.getResources().getString(R.string.systemui_restart_message));
    }

    public static void showSystemUIrestartDialog(Activity a, String dialogTitle, String dialogMessage) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(a);
        builder.setTitle(dialogTitle);
        builder.setMessage(dialogMessage);
        builder.setPositiveButton(R.string.print_restart,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        RestartSystemUITask task = new RestartSystemUITask() {
                            private ProgressDialog dialog;

                            @Override
                            protected void onPreExecute() {
                                super.onPreExecute();
                                dialog = new ProgressDialog(a);
                                dialog.setMessage(a.getResources().getString(R.string.restarting_ui));
                                dialog.setCancelable(false);
                                dialog.setIndeterminate(true);
                                dialog.show();
                            }

                            @Override
                            protected Void doInBackground(Context... params) {
                                // Give the user a second to see the dialog
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    // Ignore
                                }

                                // Restart the UI
                                super.doInBackground(params);
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void param) {
                                super.onPostExecute(param);
                                dialog.dismiss();
                            }
                        };
                        task.execute(a);
                    }
                });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.show();
    }

    private static class RestartSystemUITask extends AsyncTask<Context, Void, Void> {
        private Context mContext;
        private String INTENT_RESTART_SYSTEMUI = "restart_systemui";

        @Override
        protected Void doInBackground(Context... params) {
            try {
                if (params.length > 0) {
                    mContext = params[0].getApplicationContext();
                } else {
                    throw new Exception("Called RestartSystemUITask without context");
                }
                mContext.sendBroadcastAsUser(new Intent(INTENT_RESTART_SYSTEMUI), new UserHandle(UserHandle.USER_ALL));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
