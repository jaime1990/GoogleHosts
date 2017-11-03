package com.jeffreymor.googlehosts;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

import com.stericson.RootShell.RootShell;
import com.stericson.RootShell.execution.Command;
import com.stericson.RootShell.execution.Shell;
import com.stericson.RootTools.RootTools;

/**
 * Created by Mor on 2017/6/10.
 */

public class RebootDialogFragment extends DialogFragment implements DialogInterface.OnClickListener, DialogInterface.OnCancelListener {
    public static RebootDialogFragment newInstance() {

        Bundle args = new Bundle();
        RebootDialogFragment fragment = new RebootDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getContext())
                .setTitle("Warning")
                .setMessage("Are you sure to reboot?")
                .setPositiveButton("Yes", this)
                .setNegativeButton("No", this)
                .setCancelable(true)
                .setOnDismissListener(this)
                .create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_NEGATIVE:
                break;
            case DialogInterface.BUTTON_POSITIVE:
                try {
                    Shell shell = RootShell.getShell(true);
                    RootTools.runShellCommand(shell, new Command(0, "reboot"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
        getActivity().finish();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        getActivity().finish();
    }
}
