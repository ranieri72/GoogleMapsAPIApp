package com.example.mtz_5555_transp.mymapapplication.Util;

import android.app.Dialog;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

/**
 * Created by mtz-5555-transp on 14/08/17.
 */

public class PlayServicesUtils {

    public final static int REQUEST_CODE_ERRO_PLAY_SERVICES = 9000;

    public static boolean googlePlayServicesAvailable(FragmentActivity fragmentActivity) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(fragmentActivity);

        if (ConnectionResult.SUCCESS == resultCode) {
            return true;
        } else {
            exibirMensagemErro(fragmentActivity, resultCode);
            return false;
        }
    }

    private static void exibirMensagemErro(FragmentActivity fragmentActivity, int resultCode) {
        Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(resultCode, fragmentActivity, REQUEST_CODE_ERRO_PLAY_SERVICES);

        if (errorDialog != null) {
            MessageDialogFragment erroFragment = new MessageDialogFragment();
            erroFragment.setDialog(errorDialog);
            erroFragment.show(fragmentActivity.getFragmentManager(), "DIALOG_ERRO_PLAY_SERVICES");
        }
    }
}
