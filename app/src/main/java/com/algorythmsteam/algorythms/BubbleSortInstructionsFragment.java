package com.algorythmsteam.algorythms;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.content.res.AssetFileDescriptor;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.handlers.AnimationHandler;
import com.handlers.BubbleSortCardsAdapter;
import com.handlers.ResourceResolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;


public class BubbleSortInstructionsFragment extends AlgorhythmsFragment implements View.OnClickListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String TAG = "BSInstructionFrag";
    private MediaPlayer mediaPlayer;

    private enum InstructionStage {
        BETWEEN_STAGES,
        START,
        STAGE1_A,
        STAGE1_B,
        STAGE1_C,
        SCAN_STAGE1,
        STAGE2_A,
        STAGE2_C, STAGE2_D, STAGE2_E, STAGE2_F, STAGE2_G, STAGE2_B
    }

    private View root;
    private TextView text1, text2, text3;
    private ImageButton backButton, nfcButton, nextButton;
    private ImageView bubbleSortTitle, phoneIcon, disabledNextButton,
            background, likeButton, dislikeButton;
    private ViewGroup extraContentHolder, buttonsHolder, nextButtonContainer;
    private LinearLayout cardsHolder;
    private HorizontalScrollView cardsScroller;

    private String cardsType;
    private int cardsNum, largestCard, leftOpenedCardNumber, rightOpenedCardNumber;
    private View leftOpenedCard, rightOpenedCard;
    private ArrayList<Integer> cardsArray;
    private boolean[] scannedCards;
    private InstructionStage stage;
    private BubbleSortCardsAdapter cardsAdapter;

    private AnimatorSet phoneAnimator;
    private Animator cardsHolderPopInAnim;

    public static BubbleSortInstructionsFragment newInstance() {
        BubbleSortInstructionsFragment fragment = new BubbleSortInstructionsFragment();
        return fragment;
    }

    public BubbleSortInstructionsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cardsArray = new ArrayList<Integer>();
        scannedCards = new boolean[10];
        stage = InstructionStage.START;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_bubble_sort_instructions, container, false);
        text1 = (TextView) root.findViewById(R.id.bubble_sort_instructions_text1);
        text2 = (TextView) root.findViewById(R.id.bubble_sort_instructions_text2);
        text3 = (TextView) root.findViewById(R.id.bubble_sort_instructions_text3);
        backButton = (ImageButton) root.findViewById(R.id.bubble_sort_instructions_back_button);
        nfcButton = (ImageButton) root.findViewById(R.id.bubble_sort_instructions_nfc_button);
        nextButton = (ImageButton) root.findViewById(R.id.bubble_sort_instructions_next_button);
        nextButtonContainer = (ViewGroup) root.findViewById(R.id.bubble_sort_instructions_next_button_container);
        likeButton = (ImageView) root.findViewById(R.id.bubble_sort_instructions_like_button);
        dislikeButton = (ImageView) root.findViewById(R.id.bubble_sort_instructions_dislike_button);
        disabledNextButton = (ImageView) root.findViewById(R.id.bubble_sort_instructions_next_disabled);
        bubbleSortTitle = (ImageView) root.findViewById(R.id.bubble_sort_instructions_title);
        phoneIcon = (ImageView) root.findViewById(R.id.bubble_sort_instructions_phone_icon);
        background = (ImageView) root.findViewById(R.id.bubble_sort_instructions_background);
        cardsHolder = (LinearLayout) root.findViewById(R.id.bubble_sort_instructions_cards_holder);
        cardsScroller = (HorizontalScrollView) root.findViewById(R.id.bubble_sort_instructions_cards_scroller);
        extraContentHolder = (ViewGroup) root.findViewById(R.id.bubble_sort_instructions_extra_content_holder);
        buttonsHolder = (ViewGroup) root.findViewById(R.id.bubble_sort_instructions_buttons_holder);

        LayoutTransition transitioner = cardsHolder.getLayoutTransition();
        cardsHolderPopInAnim = transitioner.getAnimator(LayoutTransition.APPEARING);

        transitioner.setAnimator(LayoutTransition.APPEARING, null);

        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/coopbl.TTF");
        text1.setTypeface(tf);
        text2.setTypeface(tf);
        text3.setTypeface(tf);

        backButton.setOnClickListener(this);
        nfcButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        likeButton.setOnClickListener(this);
        dislikeButton.setOnClickListener(this);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (stage == InstructionStage.START) {
            startStage1A();
        }
    }

    private void startStage1A() {
        stage = InstructionStage.STAGE1_A;
        ObjectAnimator backgroundFadeIn = AnimationHandler.generateAlphaAnimation(background, 0, 0.4f, 3000, 0, new DecelerateInterpolator());
        ObjectAnimator backgroundResizeX = AnimationHandler.generateAnimation(background, "scaleX", 1, 2f, 100000, 0, new DecelerateInterpolator());
        ObjectAnimator backgroundResizeY = AnimationHandler.generateAnimation(background, "scaleY", 1, 2f, 100000, 0, new DecelerateInterpolator());
        AnimatorSet backgroundAnim = new AnimatorSet();
        backgroundAnim.playTogether(backgroundFadeIn, backgroundResizeX, backgroundResizeY);
        backgroundAnim.setStartDelay(300);

        text1.setText("Instructions");
        text3.setText("Lean how to play Bubble-Sort step-by-step");

        popInItem(bubbleSortTitle, 1000);
        popInItem(text1, 1200);
        popInItem(text3, 1400);
        popInItem(backButton, 1600);
        popInItem(nextButton, 1800);
        backgroundAnim.start();
    }

    private void startStage1B() {
        stage = InstructionStage.STAGE1_B;
        popOutItem(text3, 100, null);
        replaceText(text1, "Pick a deck of cards (Red or Blue) and shuffle them", 250);
        text2.setText("Lay them face-down in a row");
        popInItem(text2, 750);

        ViewGroup blankCard1 = (ViewGroup) getLayoutInflater(null).inflate(R.layout.cards_holder_card_item, null);
        ViewGroup blankCard2 = (ViewGroup) getLayoutInflater(null).inflate(R.layout.cards_holder_card_item, null);
        ViewGroup blankCard3 = (ViewGroup) getLayoutInflater(null).inflate(R.layout.cards_holder_card_item, null);
        ViewGroup blankCard4 = (ViewGroup) getLayoutInflater(null).inflate(R.layout.cards_holder_card_item, null);
        blankCard1.setAlpha(0);
        blankCard2.setAlpha(0);
        blankCard3.setAlpha(0);
        blankCard4.setAlpha(0);

        cardsHolder.addView(blankCard1);
        cardsHolder.addView(blankCard2);
        cardsHolder.addView(blankCard3);
        cardsHolder.addView(blankCard4);

        popInItem(blankCard1, 900);
        popInItem(blankCard2, 1050);
        popInItem(blankCard3, 1200);
        popInItem(blankCard4, 1350);
    }

    private void startStage1C() {
        stage = InstructionStage.STAGE1_C;
        replaceText(text1, "Now Scan the cards one-by-one from left to right", 100);
        replaceText(text2, "Hit the NFC Button when ready!", 350);
        runPhoneIconAnimation();
        nfcButton.setVisibility(View.VISIBLE);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                disabledNextButton.setVisibility(View.VISIBLE);
                nextButton.setOnClickListener(null);
            }
        }, 100);
    }

    private void startScanStage1() {
        stage = InstructionStage.BETWEEN_STAGES;
        phoneAnimator.cancel();
        if (phoneIcon.getAlpha() != 0) {
            popOutItem(phoneIcon, 300, null);
        }

        popOutItem(text2, 300, null);
        replaceText(text1, "Please scan the first card", 550);
        nfcButton.setVisibility(View.GONE);

        for (int i = 0; i < cardsHolder.getChildCount(); i++) {
            View card = cardsHolder.getChildAt(i);
            //if we're at the last card
            if (i == cardsHolder.getChildCount() - 1) {
                popOutItem(card, 200 * i, new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        stage = InstructionStage.SCAN_STAGE1;
                        cardsHolder.removeAllViews();
                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params.gravity = Gravity.NO_GRAVITY;
                        cardsHolder.setLayoutParams(params);
                        cardsHolder.getLayoutTransition().setAnimator(LayoutTransition.APPEARING, cardsHolderPopInAnim);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
            }

            else {
                popOutItem(card, 200*i, null);
            }
        }
    }

    private void startStage2A() {
        stage = InstructionStage.STAGE2_A;
        nfcButton.setVisibility(View.GONE);
        cardsAdapter.hideAllCards();
        cardsScroller.fullScroll(HorizontalScrollView.FOCUS_LEFT);
        popOutItem(text2, 100, null);
        replaceText(text1, "Ok! lets start playing! click the next button", 300);
    }

    private void startStage2B() {
        stage = InstructionStage.STAGE2_B;
        replaceText(text1, "Now the player should flip over the leftmost closed card", 100);
        text2.setText("Shhh! don't tell them what to do!");
        popInItem(text2, 700);

        cardsScroller.fullScroll(HorizontalScrollView.FOCUS_LEFT);
        cardsAdapter.flipCard(0, 1200);
        leftOpenedCardNumber = cardsArray.get(0);
        leftOpenedCard = cardsHolder.getChildAt(0);
    }

    private void startStage2C() {
        stage = InstructionStage.STAGE2_C;
        enableLikeSession("If The player flipped over the leftmost unopened card, press the like button",
                "If They try to do anything else, press the unlike button");
    }

    private void startStage2D() {
        stage = InstructionStage.STAGE2_D;
        cardsAdapter.flipCard(1, 2000);
        rightOpenedCardNumber = cardsArray.get(1);
        rightOpenedCard = cardsHolder.getChildAt(1);
        disableLikeSession("Now The player should flip over the card to the right of the open one",
                "Shhhh! Don't tell them what to do");
    }

    private void startStage2E() {
        stage = InstructionStage.STAGE2_E;
        enableLikeSession("If They flipped over the card to the right of the open one, press the like button",
                "If They try to do anything else, press the unlike button");
    }

    private void startStage2F() {
        stage = InstructionStage.STAGE2_F;
        String swapMsg;
        boolean shouldSwap = (leftOpenedCardNumber > rightOpenedCardNumber);
        swapMsg = (shouldSwap)? "Cards should be swapped!" : "Cards are OK, no need to swap them.";
        disableLikeSession(swapMsg + " Now let The player decide whether or not to swap them", "Shhh! let them figure out themselves");

        if (shouldSwap) {
            ObjectAnimator leftCardSlide = AnimationHandler.generateXAnimation(leftOpenedCard, 0, leftOpenedCard.getWidth(), 300, 0, null);
            ObjectAnimator rightCardSlide = AnimationHandler.generateXAnimation(rightOpenedCard, 0, -rightOpenedCard.getWidth(), 300, 0, null);
            AnimatorSet as = new AnimatorSet();
            as.setStartDelay(2500);
            as.playTogether(leftCardSlide, rightCardSlide);
            as.start();
        }
    }

    private void startStage2G() {
        stage = InstructionStage.STAGE2_G;
        boolean shouldSwap = (leftOpenedCardNumber > rightOpenedCardNumber);
        String msg1 = (shouldSwap)? "If The player swapped the cards, press the like button" : "If The player did not swap the cards, press the like button";
        String msg2 = "If They try to do anything else, press the unlike button";
        enableLikeSession(msg1, msg2);

        if (shouldSwap) {
            LayoutTransition transitioner = cardsHolder.getLayoutTransition();
            transitioner.setAnimator(LayoutTransition.APPEARING, null);
            transitioner.setAnimator(LayoutTransition.DISAPPEARING, null);
            cardsHolder.setLayoutTransition(transitioner);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    cardsArray.set(0, rightOpenedCardNumber);
                    cardsArray.set(1, leftOpenedCardNumber);
                    cardsAdapter.initCardViews();
                }
            }, 1000);
        }
    }

    private void disableLikeSession(final String firstText, final String secondText) {
        popOutItem(text2, 300, new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                replaceText(text1, firstText, 200);
                text2.setText(secondText);
                popInItem(text2, 700);
                likeButton.setVisibility(View.GONE);
                dislikeButton.setVisibility(View.GONE);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        backButton.setVisibility(View.VISIBLE);
                        nextButtonContainer.setVisibility(View.VISIBLE);
                    }
                }, 300);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void enableLikeSession(final String firstText, final String secondText) {
        popOutItem(text2, 100, new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                replaceText(text1, firstText, 0);
                text2.setText(secondText);
                popInItem(text2, 500);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        backButton.setVisibility(View.GONE);
        nextButtonContainer.setVisibility(View.GONE);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                likeButton.setVisibility(View.VISIBLE);
                dislikeButton.setVisibility(View.VISIBLE);
            }
        }, 200);
    }

    private void runPhoneIconAnimation() {
        popInItem(phoneIcon, 1000);
        float toMove = (phoneIcon.getWidth() + (phoneIcon.getWidth()/2));
        phoneAnimator = new AnimatorSet();
        ObjectAnimator phoneSlide1 = AnimationHandler.generateXAnimation(phoneIcon, 0, toMove, 600, 1600, new AccelerateDecelerateInterpolator());
        ObjectAnimator phoneSlide2 = AnimationHandler.generateXAnimation(phoneIcon, toMove, toMove*2, 600, 400, new AccelerateDecelerateInterpolator());
        ObjectAnimator phoneSlide3 = AnimationHandler.generateXAnimation(phoneIcon, toMove*2, toMove*3, 600, 400, new AccelerateDecelerateInterpolator());
        ObjectAnimator phoneSlide4 = AnimationHandler.generateXAnimation(phoneIcon, toMove*3, 0, 1200, 400, new AnticipateOvershootInterpolator(1));
        final AnimatorSet phonePopOut = AnimationHandler.generatePopOutAnimation(phoneIcon, 1, 0, 500, 200, new AnticipateInterpolator(1));
        phoneAnimator.playSequentially(phoneSlide1, phoneSlide2, phoneSlide3, phoneSlide4, phonePopOut);
        phoneAnimator.start();
    }

    private void handleNextSlide() {
        switch (stage) {
            case STAGE1_A:
                startStage1B();
                break;

            case STAGE1_B:
                startStage1C();
                break;

            case SCAN_STAGE1:
                startStage2A();
                break;

            case STAGE2_A:
                startStage2B();
                break;

            case STAGE2_B:
                startStage2C();
                break;

            case STAGE2_C:
                startStage2D();
                break;

            case STAGE2_D:
                startStage2E();
                break;

            case STAGE2_E:
                startStage2F();
                break;

            case STAGE2_F:
                startStage2G();
                break;

            case STAGE2_G:
                Toast.makeText(getActivity(), "Full instructions coming soon!", Toast.LENGTH_LONG).show();
                break;

            default:
                break;
        }
    }

    private void handleScanStage() {
        switch (stage) {
            case STAGE1_C:
                startScanStage1();
                break;

            case SCAN_STAGE1:
                Random r = new Random();
                int Low = 1;
                int High = 10;
                int randNum = r.nextInt(High-Low) + Low;
                Bundle cardData = new Bundle();
                cardData.putString("card_type", ResourceResolver.CARD_TYPE_NUMBERS);
                cardData.putInt("card_number", randNum);
                addCard(cardData);

                break;

            default:
                break;
        }
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

        bundle.putString("card_type", cardType);
        bundle.putInt("card_number", cardNumber);
        return bundle;
    }

    private void addCard(Bundle cardData) {
        String cardType = cardData.getString("card_type");
        int cardNumber = cardData.getInt("card_number");

        if (cardsType == null) {
            cardsType = cardType;
        } else if (!cardType.equals(cardsType)) {
            Toast.makeText(getActivity(), "Please scan a card of matching type", Toast.LENGTH_LONG).show();
            return;
        }

        if (scannedCards[cardNumber-1]) {
            Toast.makeText(getActivity(), "You already scanned that card!", Toast.LENGTH_LONG).show();
            return;
        }

        scannedCards[cardNumber-1] = true;

        //we didnt initialize the cardsAdapter yet
        if (cardsAdapter == null) {
            cardsAdapter = new BubbleSortCardsAdapter(getActivity(), cardsType, cardsArray, cardsHolder, cardsScroller);
        }

        cardsArray.add(cardNumber);
        if (cardsArray.size() == 1) {
            replaceText(text1, "Scanned one card", 150);
        }

        else {
            replaceText(text1, "Scanned " + cardsArray.size() + " cards", 150);
        }

        if (cardsArray.size() == 4) {
            text2.setText("Press the next button when you're done scanning");
            popInItem(text2, 650);
            nextButton.setOnClickListener(this);
            disabledNextButton.setVisibility(View.GONE);
        }

        cardsAdapter.initCardView(cardsArray.size() - 1);
    }

    private void popOutItem(View item, int delay, Animator.AnimatorListener listener) {
        AnimatorSet popOutAnim = AnimationHandler.generatePopOutAnimation(item, 1, 0, 500, delay, new AnticipateInterpolator(1));

        if (listener != null) {
            popOutAnim.addListener(listener);
        }

        popOutAnim.start();
    }

    private void popInItem(View item, int delay) {
        AnimatorSet popInAnim = AnimationHandler.generatePopInAnimation(item, 0, 1, 500, delay, new OvershootInterpolator(1));
        popInAnim.start();
    }

    private void replaceText(final TextView textView, final String newText, int delay) {
        final ObjectAnimator fadeOutAnim = AnimationHandler.generateAlphaAnimation(textView, 1, 0, 300, delay, null);
        fadeOutAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                textView.setText(newText);
                popInItem(textView, 0);
                fadeOutAnim.removeAllListeners();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        fadeOutAnim.start();
    }

    @Override
    public boolean handleBackPress() {
        //TODO: handle this properly
        GameIntroFragment gif = GameIntroFragment.newInstance(ResourceResolver.BUBBLE_SORT);
        AlgoryhmsMainActivity activity = (AlgoryhmsMainActivity) getActivity();
        activity.launchFragment(gif, GameIntroFragment.TAG, R.anim.enter_reverse, R.anim.exit_reverse);
        return true;
    }

    @Override
    public boolean handleNfcScan(String res) {
        if (stage != InstructionStage.SCAN_STAGE1) {
            return true;
        }

        Bundle scanResult = parseScanResult(res);
        if (scanResult == null) {
            Toast.makeText(getActivity(), "Please scan a valid game card", Toast.LENGTH_LONG).show();
            return true;
        }

        addCard(scanResult);
        return true;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bubble_sort_instructions_back_button:
                handleBackPress();
                break;

            case R.id.bubble_sort_instructions_next_button:
                handleNextSlide();
                break;

            case R.id.bubble_sort_instructions_nfc_button:
                handleScanStage();
                break;

            case R.id.bubble_sort_instructions_like_button:
                handleSoundButtonClick(likeButton);
                break;

            case R.id.bubble_sort_instructions_dislike_button:
                handleSoundButtonClick(dislikeButton);
                break;

            default:
                break;
        }
    }

    private void handleSoundButtonClick(final View clickedButton) {
        clickedButton.setOnClickListener(null); //to avoid bugs related to multiple fast clicks
        final boolean isLikeButtonClicked = (clickedButton.getId() == R.id.bubble_sort_instructions_like_button);

        ObjectAnimator likeXScaleIn = AnimationHandler.generateXScaleAnimation(clickedButton, 1, 0.5f, 200, 0, null);
        final ObjectAnimator likeYScaleIn = AnimationHandler.generateYScaleAnimation(clickedButton, 1, 0.5f, 200, 0, null);
        likeYScaleIn.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                clickedButton.setOnClickListener(BubbleSortInstructionsFragment.this);
                ObjectAnimator likeXScaleOut = AnimationHandler.generateXScaleAnimation(clickedButton, 0.5f, 1, 400, 0, new OvershootInterpolator());
                ObjectAnimator likeYScaleOut = AnimationHandler.generateYScaleAnimation(clickedButton, 0.5f, 1, 400, 0, new OvershootInterpolator());
                AnimatorSet likeScaleOut = new AnimatorSet();
                likeScaleOut.playTogether(likeXScaleOut, likeYScaleOut);
                likeScaleOut.start();
                likeYScaleIn.removeAllListeners();
                if (isLikeButtonClicked) {
                    handleNextSlide();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        AnimatorSet likeScaleIn = new AnimatorSet();
        likeScaleIn.playTogether(likeXScaleIn, likeYScaleIn);
        likeScaleIn.start();

        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }

        String playSound;
        if (isLikeButtonClicked) {
            Random r = new Random();
            int Low = 1;
            int High = 3;
            int randNum = r.nextInt(High-Low) + Low;
            playSound = (randNum == 1)? "correct1.mp3" : "correct2.mp3";
        } else {
            playSound = "wrong.mp3";
        }

        try {
            mediaPlayer.reset();
            AssetFileDescriptor afd;
            afd = getActivity().getAssets().openFd(playSound);
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    if (mp == mediaPlayer) {
                        mediaPlayer.start();
                    }
                }
            });
            mediaPlayer.prepare();
        }

        catch (IOException e) {
            mediaPlayer = null;
            Log.e(TAG, "error loading the media player");
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
