package com.Dialogues;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;
import android.widget.ImageView;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

import com.algorythmsteam.algorythms.R;
import com.handlers.AnimationHandler;
import com.handlers.ResourceResolver;

public class QuickSortScanDialog extends AlgorhythmsDialogFragment implements View.OnClickListener {
    public static final String TAG = "QuickSortScanDialog";
    private static final String BACKGROUND_VIEW_ID = "background_view_id";


    private enum ScanState {
        INITIALIZING,
        STATE_WAITING_FIRST_CARD,
        STATE_WAITING_SECOND_CARD,
        STATE_DONE
    }

    private View root;
    private TextView dialogTitle, pivotCardText;
    private ImageView nfcIcon, pivotCardIcon, secondCardIcon,
            moveIndicatorIcon, leftCupIcon, rightCupIcon;
    private ScanState state;
    private String cardsType;
    private int pivotCardNumber, secondCarNumber;
    private boolean isAnimInit;

    public QuickSortScanDialog() {
        //required empty constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        state = ScanState.INITIALIZING;
        isAnimInit = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.dialog_quick_sort_scan, container);
        super.setBackgroundView();
        root.setOnClickListener(this);
        dialogTitle = (TextView) root.findViewById(R.id.dialog_quick_sort_title);
        pivotCardText = (TextView) root.findViewById(R.id.dialog_quick_sort_pivot_card_text);
        nfcIcon = (ImageView) root.findViewById(R.id.dialog_quick_sort_scan_nfc_icon);
        pivotCardIcon = (ImageView) root.findViewById(R.id.dialog_quick_sort_pivot_card_icon);
        leftCupIcon = (ImageView) root.findViewById(R.id.dialog_quick_sort_left_cup);
        rightCupIcon = (ImageView) root.findViewById(R.id.dialog_quick_sort_right_cup);
        secondCardIcon = (ImageView) root.findViewById(R.id.dialog_quick_sort_second_card_icon);
        pivotCardIcon.setRotationY(180); //used by the animation system for some nice effects later on
        secondCardIcon.setRotationY(180); //used by the animation system for some nice effects later on

        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/coopbl.TTF");
        dialogTitle.setTypeface(tf);
        pivotCardText.setTypeface(tf);

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
        AnimatorSet leftCupPopIn = AnimationHandler.generatePopInAnimation(leftCupIcon, 0, 1, 500, 200, new OvershootInterpolator());
        AnimatorSet pivotCardPopIn = AnimationHandler.generatePopInAnimation(pivotCardIcon, 0, 1, 500, 320, new OvershootInterpolator());
        AnimatorSet rightCupPopIn = AnimationHandler.generatePopInAnimation(rightCupIcon, 0, 1, 500, 440, new OvershootInterpolator());
        AnimatorSet as = new AnimatorSet();
        as.playTogether(nfcPopIn, titlePopIn, leftCupPopIn, pivotCardPopIn, rightCupPopIn);
        as.start();

        isAnimInit = true;
        state = ScanState.STATE_WAITING_FIRST_CARD;
    }

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


    public static QuickSortScanDialog newInstance(int backgroundViewID) {
        QuickSortScanDialog dialog = new QuickSortScanDialog();
        Bundle args = new Bundle();
        args.putInt(BACKGROUND_VIEW_ID, backgroundViewID);
        dialog.setArguments(args);
        dialog.setStyle(DialogFragment.STYLE_NO_FRAME, 0);
        return dialog;
    }

    @Override
    public void handleNfcScan(String res) {
        //dialog is not yet ready to receive any scan
        if (state == ScanState.INITIALIZING || state == ScanState.STATE_DONE) {
            return;
        }

        Bundle scanResult = parseScanResult(res);
        if (scanResult == null) {
            Toast.makeText(getActivity(), "Please scan a game card", Toast.LENGTH_LONG).show();
            return;
        }

        if (state == ScanState.STATE_WAITING_FIRST_CARD) {
            state = ScanState.INITIALIZING; //to avoid redundant scanning bugs
            cardsType = scanResult.getString("card_type");
            pivotCardNumber = scanResult.getInt("card_number");
            int cardImageRes = scanResult.getInt("card_image_res");
            handleCardTransition(cardImageRes, pivotCardIcon);
            return;
        }

        if (state == ScanState.STATE_WAITING_SECOND_CARD) {
            state = ScanState.INITIALIZING; //to avoid redundant scanning bugs
            String cardType = scanResult.getString("card_type");
            if (!cardType.equals(cardsType)) {
                Toast.makeText(getActivity(), "Cards type mismatch", Toast.LENGTH_LONG).show();
                state = ScanState.STATE_WAITING_SECOND_CARD;
                return;
            }

            int cardNumber = scanResult.getInt("card_number");
            if (cardNumber == pivotCardNumber) {
                Toast.makeText(getActivity(), "You scanned the same card twice!", Toast.LENGTH_LONG).show();
                state = ScanState.STATE_WAITING_SECOND_CARD;
                return;
            }

            state = ScanState.STATE_DONE;
            secondCarNumber = cardNumber;
            int cardImageRes = scanResult.getInt("card_image_res");
            handleCardTransition(cardImageRes, secondCardIcon);
            handleCardsComparison();
        }
    }

    private void handleCardTransition(final int cardImageRes, final ImageView cardToShow) {
        if (cardImageRes == ResourceResolver.UNDEFINED_RESOURCE || cardToShow == null) {
            return;
        }

        if (cardToShow.getId() == R.id.dialog_quick_sort_pivot_card_icon) {
            final ObjectAnimator cardHalfRotation = AnimationHandler.generateAnimation(pivotCardIcon, "rotationY", 180, 90, 300, 0, null);
            cardHalfRotation.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    cardToShow.setImageResource(cardImageRes);
                    ObjectAnimator cardFullRotation = AnimationHandler.generateAnimation(pivotCardIcon, "rotationY", 90, 0, 400, 0, new OvershootInterpolator());
                    cardFullRotation.start();
                    cardHalfRotation.removeAllListeners();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

            int secondCardColor = ResourceResolver.resolveBlankCardImage(cardsType);
            secondCardIcon.setImageResource(secondCardColor);
            final AnimatorSet secondCardPopIn = AnimationHandler.generatePopInAnimation(secondCardIcon, 0, 1, 700, 700, new OvershootInterpolator());
            secondCardPopIn.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    state = ScanState.STATE_WAITING_SECOND_CARD;
                    secondCardPopIn.removeAllListeners();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

            final ObjectAnimator titleFadeOut = AnimationHandler.generateAlphaAnimation(dialogTitle, 1, 0, 300, 20, null);
            titleFadeOut.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    dialogTitle.setText("now draw a card from the deck and scan it");
                    dialogTitle.setTextColor(getResources().getColor(R.color.algorythms_red));
                    ObjectAnimator titleFadeIn = AnimationHandler.generateAlphaAnimation(dialogTitle, 0, 1, 300, 20, null);
                    titleFadeIn.start();
                    titleFadeOut.removeAllListeners();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

            AnimatorSet as = new AnimatorSet();
            as.playTogether(cardHalfRotation, titleFadeOut, secondCardPopIn);
            as.start();
        }

        else if (cardToShow.getId() == R.id.dialog_quick_sort_second_card_icon) {
            final ObjectAnimator cardHalfRotation = AnimationHandler.generateAnimation(secondCardIcon, "rotationY", 180, 90, 300, 0, null);
            cardHalfRotation.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    cardToShow.setImageResource(cardImageRes);
                    ObjectAnimator cardFullRotation = AnimationHandler.generateAnimation(secondCardIcon, "rotationY", 90, 0, 400, 0, new OvershootInterpolator());
                    cardFullRotation.start();
                    cardHalfRotation.removeAllListeners();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            cardHalfRotation.start();
        }
    }

    private void handleCardsComparison() {
        final String titleMessage;
        boolean isSecondCardBigger = (secondCarNumber >= pivotCardNumber);

        if (isSecondCardBigger) {
            titleMessage = "Place the card in the cup to the right of the pivot card";
        } else {
            titleMessage = "Place the card in the cup to the left of the pivot card";
        }

        final ObjectAnimator titleFadeOut = AnimationHandler.generateAlphaAnimation(dialogTitle, 1, 0, 300, 20, null);
        titleFadeOut.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                dialogTitle.setText(titleMessage);
                dialogTitle.setTextColor(getResources().getColor(R.color.algorythms_blue));
                ObjectAnimator titleFadeIn = AnimationHandler.generateAlphaAnimation(dialogTitle, 0, 1, 300, 20, null);
                titleFadeIn.start();
                titleFadeOut.removeAllListeners();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        titleFadeOut.start();

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int[] pivotCardLocation = new int[2];
        pivotCardIcon.getLocationOnScreen(pivotCardLocation);
        int[] secondCardLocation = new int[2];
        secondCardIcon.getLocationOnScreen(secondCardLocation);
        int xToMove = (width / 4) + (secondCardIcon.getWidth() / 10);
        xToMove = (isSecondCardBigger)? xToMove : -xToMove;
        float yToMove = (secondCardLocation[1] - pivotCardLocation[1]);
        ObjectAnimator xCardMove = AnimationHandler.generateXAnimation(secondCardIcon, 0, xToMove, 600, 0, new AccelerateDecelerateInterpolator());
        final ObjectAnimator yCardMove = AnimationHandler.generateYAnimation(secondCardIcon, 0, -yToMove, 600, 0, new AccelerateDecelerateInterpolator());
        yCardMove.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                moveIndicatorIcon = (ImageView) root.findViewById(R.id.dialog_quick_sort_move_indicator_icon);
                AnimatorSet indicatorButtonPopIn = AnimationHandler.generatePopInAnimation(moveIndicatorIcon, 0, 1, 500, 1300, new OvershootInterpolator(1));
                ObjectAnimator xResize = AnimationHandler.generateXScaleAnimation(secondCardIcon, 1, 0.6f, 1200, 0, new BounceInterpolator());
                ObjectAnimator yResize = AnimationHandler.generateYScaleAnimation(secondCardIcon, 1, 0.6f, 1200, 0, new BounceInterpolator());
                AnimatorSet as = new AnimatorSet();
                as.setStartDelay(200);
                as.playTogether(xResize, yResize, indicatorButtonPopIn);
                as.start();

                yCardMove.removeAllListeners();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        AnimatorSet as = new AnimatorSet();
        as.playTogether(xCardMove, yCardMove);
        as.setStartDelay(1200);
        as.start();
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
}
