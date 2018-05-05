package com.test.bank.weight.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.test.bank.R;

/**
 * Created by 55 on 2017/11/6.
 */

public class ProgressDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.ProgressDialog);
        dialog.setContentView(R.layout.dialog_progress);
        return dialog;
    }
}
