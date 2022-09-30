package com.dedy.parcguide.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.dedy.parcguide.R;

/**
 * Created by dedy ngueko on 20/3/2020.
 */
public class DialogFragmentLoading extends DialogFragment {

    /**
     * The Constant TAG.
     */
    public static final String TAG = DialogFragmentLoading.class
            .getSimpleName();

    public static DialogFragmentLoading newInstance() {
        DialogFragmentLoading fragment = new DialogFragmentLoading();

        return fragment;
    }


    private View parentView;

    /* (non-Javadoc)
         * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
         */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawableResource(
                android.R.color.transparent);

        parentView = inflater.inflate(R.layout.fra_dialog_loading,
                container, false);

        setCancelable(false);

        return parentView;
    }
}
