package com.example.mtz_5555_transp.mymapapplication.Util;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

/**
 * Created by mtz-5555-transp on 14/08/17.
 */

public class MessageDialogFragment extends DialogFragment {

    private Dialog mDialog;

    public MessageDialogFragment() {
        super();
        mDialog = null;
        setRetainInstance(true);
    }

    public void setDialog(Dialog dialog) {
        mDialog = dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return mDialog;
    }
}
