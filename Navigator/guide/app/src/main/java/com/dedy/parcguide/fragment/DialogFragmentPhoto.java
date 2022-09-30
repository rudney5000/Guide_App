package com.dedy.parcguide.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.dedy.parcguide.R;
import com.dedy.parcguide.about.Photo;
import com.dedy.parcguide.util.ImageOptionsBuilder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by dedy ngueko on 20/3/2020.
 */
public class DialogFragmentPhoto extends DialogFragment {

    public static final String TAG = DialogFragmentPhoto.class.getSimpleName();

    private Photo mPhoto;
    private DisplayImageOptions mDisplayImageOptions;
    private ImageLoader mImageLoader;

    public static DialogFragmentPhoto newInstance(Photo photo) {

        DialogFragmentPhoto dialogFragmentPhoto = new DialogFragmentPhoto();

        dialogFragmentPhoto.mPhoto = photo;

        return dialogFragmentPhoto;
    }

    @Override
    public void onStart() {
        super.onStart();

        // safety check
        if (getDialog() == null) {
            return;
        }

        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        // set the animations to use on showing and hiding the dialog
        getDialog().getWindow().setWindowAnimations(
                R.style.dialog_animation_fade);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.black_transparent_2);

        this.mDisplayImageOptions = ImageOptionsBuilder.buildGeneralImageOptions(true, 0, false);
        this.mImageLoader = ImageLoader.getInstance();

        return inflater.inflate(R.layout.fra_dialog_photo, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ImageView imageView = (ImageView) getView().findViewById(R.id.fra_dialog_photo);

        imageView.getLayoutParams().width = getResources().getDisplayMetrics().widthPixels;

        if (mPhoto.getPath().startsWith("http")) {
            mImageLoader.displayImage(mPhoto.getPath(), imageView, mDisplayImageOptions);
        } else {
            imageView.setImageDrawable(getResources()
                    .getDrawable(mPhoto.getDrawableId(getActivity())));
        }

        getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
