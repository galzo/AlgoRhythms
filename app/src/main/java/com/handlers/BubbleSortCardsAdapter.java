package com.handlers;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;

import com.algorythmsteam.algorythms.R;

import java.util.ArrayList;

public class BubbleSortCardsAdapter extends BaseAdapter {
    public static final String TAG = "BubbleSortCardsAdapter";


    ArrayList<Integer> cardNumbers;
    boolean[] hiddenCards; //cards with true value are hidden (flipped over)
    String cardsType;

    ViewGroup cardsHolder;
    HorizontalScrollView cardsScroller;
    LayoutInflater linearInflater;

    public BubbleSortCardsAdapter(Activity activity, String cardsType, ArrayList<Integer> cardNumbers,
                                  ViewGroup cardsHolder, HorizontalScrollView cardsScroller) {
        this.linearInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.cardsType = cardsType;
        this.cardNumbers = cardNumbers;
        this.cardsHolder = cardsHolder;
        this.cardsScroller = cardsScroller;
        this.cardsScroller.setSmoothScrollingEnabled(true);
        hiddenCards = new boolean[10];
    }

    public void initCardViews() {
        cardsHolder.removeAllViews();
        for (int i = 0; i < cardNumbers.size(); i++) {
            View card = getView(i, null, cardsHolder);
            cardsHolder.addView(card);
        }
    }

    public void initCardView(int position) {
        View card = getView(position, null, cardsHolder);
        cardsHolder.addView(card);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                cardsScroller.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        }, 500);
    }


    public void showAllCards() {
        for (int i = 0; i < cardNumbers.size(); i++) {
            if (hiddenCards[i]) {
                flipCard(i, (i * 200));
            }
        }
    }

    public void hideAllCards() {
        for (int i = 0; i < cardNumbers.size(); i++) {
            if (!hiddenCards[i]) {
                flipCard(i, (i * 200));
            }
        }
    }

    public void flipCard(final int position, int delay) {
        if (position < 0 || position >= cardNumbers.size()) {
            return;
        }

        final ImageView card = (ImageView) cardsHolder.getChildAt(position).findViewById(R.id.cards_holder_card_image);
        int cardNumber = cardNumbers.get(position);

        //if the card is hidden - then we need to flip it to front
        boolean shouldRevealCard = (hiddenCards[position]);
        final int rotationStart = (shouldRevealCard)? 180 : 0;
        final int rotationEnd = Math.abs((rotationStart - 180));
        final int cardFace = (shouldRevealCard)? ResourceResolver.resolveCardImage(cardsType, cardNumber) : ResourceResolver.resolveBlankCardImage(cardsType);
        hiddenCards[position] = !(hiddenCards[position]);

        final ObjectAnimator cardHalfRotation = AnimationHandler.generateAnimation(card, "rotationY", rotationStart, 90, 300, delay, null);
        cardHalfRotation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                card.setImageResource(cardFace);
                final ObjectAnimator cardFullRotation = AnimationHandler.generateAnimation(card, "rotationY", 90, rotationEnd, 300, 0, null);
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

    @Override
    public int getCount() {
        return cardNumbers.size();
    }

    @Override
    public Object getItem(int position) {
        return cardNumbers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return cardNumbers.get(position); //the id is the card's number
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        // recycling the view:
        if (v == null) {
            v = linearInflater.inflate(R.layout.cards_holder_card_item, parent, false);
        }

        final ImageView cardIcon = (ImageView) v.findViewById(R.id.cards_holder_card_image);
        int cardNumber = cardNumbers.get(position);
        int cardResource = (hiddenCards[position])? ResourceResolver.resolveBlankCardImage(cardsType) : ResourceResolver.resolveCardImage(cardsType, cardNumber);
        cardIcon.setImageResource(cardResource);

        return v;
    }
}
