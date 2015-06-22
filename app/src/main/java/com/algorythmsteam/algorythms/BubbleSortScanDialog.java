package com.algorythmsteam.algorythms;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.handlers.AnimationHandler;
import com.handlers.ResourceResolver;


public class BubbleSortScanDialog extends AlgorhythmsDialogFragment implements View.OnClickListener {
    public static final String TAG = "BubbleSortScanDialog";
    private static final String BACKGROUND_VIEW_ID = "background_view_id";

    private enum ScanState {
        INITIALIZING,
        STATE_WAITING_FIRST_CARD,
        STATE_WAITING_SECOND_CARD,
        STATE_DONE
    }

    private View root;
    private TextView dialogTitle;
    private ImageView nfcIcon, leftCardIcon, rightCardIcon, moveIndicatorButton;
    private boolean isAnimInit;
    private ScanState state;
    private String cardsType;
    private int leftCardNumber, rightCardNumber;

    public BubbleSortScanDialog() {
        //required empty constructor
    }

    /**
     * Parses the nfc scan result, returning null if its invalid
     */
    private Bundle parseScanResult(String res) {
        Bundle bundle = new Bundle();

        //the scan result is defected or illegal, do not handle it
        if (res == null || res.length() == 0) {
            return null;
        }

        String[] result = res.split("#");
        if (result.length != 2) {
            return null;
        }

        String cardType = result[0];
        if (!ResourceResolver.isCardTypeValid(cardType)) {
            return null;
        }

        String cardNumberStr = result[1];
        int cardNumber = ResourceResolver.convertCardNumberToInt(cardNumberStr);
        if (cardNumber == ResourceResolver.UNDEFINED_RESOURCE) {
            return null;
        }

        int cardImageResource = ResourceResolver.resolveCardImage(cardType, cardNumber);
        if (cardImageResource == ResourceResolver.UNDEFINED_RESOURCE) {
            return null;
        }

        bundle.putString("card_type", cardType);
        bundle.putInt("card_number", cardNumber);
        bundle.putInt("card_image_res", cardImageResource);
        return bundle;
    }

    @Override
    public void handleNfcScan(String res) {
        //dialog is not yet ready to receive any scan
        if (state == ScanState.INITIALIZING) {
            return;
        }

        Bundle scanResult = parseScanResult(res);
        if (scanResult == null) {
            Toast.makeText(getActivity(), "Please scan a game card", Toast.LENGTH_LONG).show();
            return;
        }

        if (state == ScanState.STATE_WAITING_FIRST_CARD) {
            state = ScanState.INITIALIZING; //to avoid multiple scans
            cardsType = scanResult.getString("card_type");
            leftCardNumber = scanResult.getInt("card_number");
            int cardImageRes = scanResult.getInt("card_image_res");
            showCard(cardImageRes, leftCardIcon);
            ObjectAnimator titleFadeOut = AnimationHandler.generateAlphaAnimation(dialogTitle, 1, 0, 300, 20, null);
            titleFadeOut.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    dialogTitle.setText("Now scan the right opened card");
                    ObjectAnimator titleFadeIn = AnimationHandler.generateAlphaAnimation(dialogTitle, 0, 1, 300, 20, null);
                    titleFadeIn.start();
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            titleFadeOut.start();
        }

        if (state == ScanState.STATE_WAITING_SECOND_CARD) {
            //first lets check that we're scanning a card of the same type as the previous card
            String cardType = scanResult.getString("card_type");
            if (!cardType.equals(cardsType)) {
                Toast.makeText(getActivity(), "Cards type mismatch", Toast.LENGTH_LONG).show();
                return;
            }

            state = ScanState.INITIALIZING; //to avoid multiple scans
            rightCardNumber = scanResult.getInt("card_number");
            int cardImageRes = scanResult.getInt("card_image_res");
            showCard(cardImageRes, rightCardIcon);
        }
    }

    private void showCard(int cardImageRes, ImageView cardToShow) {
        if (cardImageRes == ResourceResolver.UNDEFINED_RESOURCE || cardToShow == null) {
            return;
        }

        if (cardToShow.getId() == R.id.dialog_bubble_sort_left_card_icon) {
            cardToShow.setImageResource(cardImageRes);
            AnimatorSet cardPopIn = AnimationHandler.generatePopInAnimation(cardToShow, 0, 1, 500, 20, new OvershootInterpolator());
            cardPopIn.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    state = ScanState.STATE_WAITING_SECOND_CARD;
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            cardPopIn.start();
        } else if (cardToShow.getId() == R.id.dialog_bubble_sort_right_card_icon) {
            cardToShow.setImageResource(cardImageRes);
            cardToShow.setVisibility(View.VISIBLE);
        }
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
        state = ScanState.INITIALIZING;
        isAnimInit = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.dialog_bubble_sort_scan, container);
        super.setBackgroundView();

        dialogTitle = (TextView) root.findViewById(R.id.dialog_bubble_sort_title);
        nfcIcon = (ImageView) root.findViewById(R.id.dialog_bubble_sort_scan_nfc_icon);
        leftCardIcon = (ImageView) root.findViewById(R.id.dialog_bubble_sort_left_card_icon);
        rightCardIcon = (ImageView) root.findViewById(R.id.dialog_bubble_sort_right_card_icon);
        moveIndicatorButton = (ImageView) root.findViewById(R.id.dialog_bubble_sort_move_indicator_icon);

        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/coopbl.TTF");
        dialogTitle.setTypeface(tf);
        root.setOnClickListener(this);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isAnimInit) {
            return;
        }

        AnimatorSet nfcPopIn = AnimationHandler.generatePopInAnimation(nfcIcon, 0, 1, 500, 0, new OvershootInterpolator());
        AnimatorSet titlePopIn = AnimationHandler.generatePopInAnimation(dialogTitle, 0, 1, 500, 120, new OvershootInterpolator());
        AnimatorSet as = new AnimatorSet();
        as.playTogether(nfcPopIn, titlePopIn);
        as.start();

        isAnimInit = true;
        state = ScanState.STATE_WAITING_FIRST_CARD;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == root.getId()) {
            dismiss();
        }
    }
}
