package com.jeffreymor.googlehosts;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Mor on 2017/7/11.
 */

public class AboutDialogPreference extends DialogPreference {
    public AboutDialogPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public AboutDialogPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AboutDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AboutDialogPreference(Context context) {
        super(context);
    }


    @Override
    protected View onCreateDialogView() {

        setDialogMessage(Html.fromHtml(getContext().getResources().getString(R.string.app_github)));
        return super.onCreateDialogView();
    }

    @Override
    protected void showDialog(Bundle state) {
        super.showDialog(state);
        AlertDialog dialog = (AlertDialog) getDialog();
//        dialog.setCancelable(false);
        Button nButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        nButton.setClickable(false);
        nButton.setVisibility(View.GONE);
        TextView view = (TextView) dialog.findViewById(android.R.id.message);//找到dialog中唯一一个textview，在普通dialog中textview的id为android.R.id.message
        view.setMovementMethod(LinkMovementMethod.getInstance());//设置超链接点击


    }
}
