package com.jeffreymor.googlehosts;

import android.app.NotificationManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class DialogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
        FragmentManager fragmentManager = getSupportFragmentManager();
        RebootDialogFragment fragment = RebootDialogFragment.newInstance();
//        fragment.setCancelable(false);
        fragment.show(fragmentManager, "DialogFragment");
    }
}
