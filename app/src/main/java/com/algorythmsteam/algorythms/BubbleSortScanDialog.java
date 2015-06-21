package com.algorythmsteam.algorythms;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;
import android.widget.ImageView;

import com.handlers.AnimationHandler;


public class BubbleSortScanDialog extends DialogFragment {
    public static final String TAG = "BubbleSortScanDialog";
    private static final String BACKGROUND_VIEW_ID = "background_view_id";
    private View root, backgroundView;
    private TextView dialogTitle;
    private ImageView nfcIcon, leftCardIcon, rightCardIcon, moveIndicatorButton;

    public BubbleSortScanDialog() {
        //required empty constructor
    }

    public static BubbleSortScanDialog newInstance(int backgroundViewId) {
        BubbleSortScanDialog dialog = new BubbleSortScanDialog();
        Bundle args = new Bundle();
        args.putInt(BACKGROUND_VIEW_ID, backgroundViewId);
        dialog.setArguments(args);
        dialog.setStyle(DialogFragment.STYLE_NO_FRAME, 0);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            int backgroundViewId = getArguments().getInt(BACKGROUND_VIEW_ID);
            backgroundView = getActivity().findViewById(backgroundViewId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.dialog_bubble_sort_scan, container);
        if (backgroundView != null) {
            backgroundView.setAlpha(0);
            backgroundView.setVisibility(View.VISIBLE);
        }

        dialogTitle = (TextView) root.findViewById(R.id.dialog_bubble_sort_title);
        nfcIcon = (ImageView) root.findViewById(R.id.dialog_bubble_sort_scan_nfc_icon);
        leftCardIcon = (ImageView) root.findViewById(R.id.dialog_bubble_sort_left_card_icon);
        rightCardIcon = (ImageView) root.findViewById(R.id.dialog_bubble_sort_right_card_icon);
        moveIndicatorButton = (ImageView) root.findViewById(R.id.dialog_bubble_sort_move_indicator_icon);

        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/coopbl.TTF");
        dialogTitle.setTypeface(tf);

        return root;
    }

    @Override public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams windowParams = window.getAttributes();
        windowParams.dimAmount = 0.00f;
        windowParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(windowParams);

        if (backgroundView != null && backgroundView.getAlpha() == 0) {
            ObjectAnimator backgroundFadeIn = AnimationHandler.generateAlphaAnimation(backgroundView, 0, 0.9f, 400, 30, new AccelerateDecelerateInterpolator());
            backgroundFadeIn.start();
        }
    }

    @Override
    public void onDestroy() {
        super.onStop();
        if (backgroundView != null) {
            ObjectAnimator backgroundFadeOut = AnimationHandler.generateAlphaAnimation(backgroundView, 0.9f, 0, 400, 30, new AccelerateDecelerateInterpolator());
            backgroundFadeOut.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    backgroundView.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            backgroundFadeOut.start();
        }
    }
}
