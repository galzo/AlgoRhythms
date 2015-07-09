package com.handlers;

import android.graphics.Color;
import android.nfc.Tag;
import android.util.Log;

import com.algorythmsteam.algorythms.R;

public class ResourceResolver {
    public static final String BUBBLE_SORT = "bubble_sort";
    public static final String BUCKET_SORT = "bucket_sort";
    public static final String TREASURE_GAME = "treasure_game";
    public static final String QUICK_SORT = "quick_sort";
    public static final String COUNTING_SORT = "counting_sort";
    public static final String BINARY_SEARCH = "binary_search";
    public static final String BST_CONSTRUCTION = "bst_construction";
    public static final String BST_SEARCH = "bst_search";

    public static final String CATEGORY_SORTING_ALG = "Sorting Algorithm";
    public static final String CATEGORY_GRAPH_ALG = "Graph Algorithm";
    public static final String CATEGORY_SEARCH_ALG = "Search Algorithm";
    public static final String CATEGORY_RANDOM_ALG = "Random Based Algorithm";

    public static final String CARD_TYPE_NUMBERS = "numbers";
    public static final String CARD_TYPE_SHAPES = "shapes";

    public static final int UNDEFINED_RESOURCE = -1;

    public static boolean isCardTypeValid(String cardType) {
        if (cardType == null) {
            return false;
        }

        return cardType.equals(CARD_TYPE_NUMBERS) || cardType.equals(CARD_TYPE_SHAPES);
    }

    public static int resolveBlankCardImage(String cardType) {
        if (cardType == null) {
            return R.drawable.blank_card_grey;
        }

        if (cardType.equals(CARD_TYPE_NUMBERS)) {
            return R.drawable.blank_card_red;
        }

        if (cardType.equals(CARD_TYPE_SHAPES)) {
            return R.drawable.blank_card_blue;
        }

        return R.drawable.blank_card_grey;
    }

    public static int resolveCardImage(String cardType, int cardNumber) {
        if (cardType == null) {
            return UNDEFINED_RESOURCE;
        }

        if (cardType.equals(CARD_TYPE_NUMBERS)) {
            switch (cardNumber) {
                case 1:
                    return R.drawable.number_1;

                case 2:
                    return R.drawable.number_2;

                case 3:
                    return R.drawable.number_3;

                case 4:
                    return R.drawable.number_4;

                case 5:
                    return R.drawable.number_5;

                case 6:
                    return R.drawable.number_6;

                case 7:
                    return R.drawable.number_7;

                case 8:
                    return R.drawable.number_8;

                case 9:
                    return R.drawable.number_9;

                case 10:
                    return R.drawable.number_10;

                default:
                    return UNDEFINED_RESOURCE;
            }
        } else if (cardType.equals(CARD_TYPE_SHAPES)) {
            switch (cardNumber) {
                case 1:
                    return R.drawable.shape_1;

                case 2:
                    return R.drawable.shape_2;

                case 3:
                    return R.drawable.shape_3;

                case 4:
                    return R.drawable.shape_4;

                case 5:
                    return R.drawable.shape_5;

                case 6:
                    return R.drawable.shape_6;

                case 7:
                    return R.drawable.shape_7;

                case 8:
                    return R.drawable.shape_8;

                case 9:
                    return R.drawable.shape_9;

                case 10:
                    return R.drawable.shape_10;

                default:
                    return UNDEFINED_RESOURCE;
            }
        }

        return UNDEFINED_RESOURCE;
    }

    public static int resolveVideoResource(String gameId) {
        if (gameId == null) {
            return UNDEFINED_RESOURCE;
        }

        if (gameId.equals(BUBBLE_SORT)) {
            return R.raw.bubble_sort;
        }

        if (gameId.equals(COUNTING_SORT)) {
            return R.raw.counting_sort;
        }

        if (gameId.equals(QUICK_SORT)) {
            return R.raw.quick_sort;
        }

        return UNDEFINED_RESOURCE;
    }

    public static int resolveGameNameImage(String gameId) {
        if (gameId == null) {
            return UNDEFINED_RESOURCE;
        }

        if (gameId.equals(BUBBLE_SORT)) {
            return R.drawable.bubble_sort_title;
        }

        if (gameId.equals(QUICK_SORT)) {
            return R.drawable.quick_sort_title;
        }

        if (gameId.equals(TREASURE_GAME)) {
            return R.drawable.treasure_game_title;
        }

        if (gameId.equals(COUNTING_SORT)) {
            return R.drawable.counting_sort_title;
        }

        if (gameId.equals(BINARY_SEARCH)) {
            return R.drawable.binary_search_title;
        }

        if (gameId.equals(BST_CONSTRUCTION)) {
            return R.drawable.bst_construction_title;
        }

        if (gameId.equals(BST_SEARCH)) {
            return R.drawable.bst_search_title;
        }


        return UNDEFINED_RESOURCE;
    }

    public static int resolveGameTypeColor(String gameId) {
        if (gameId == null) {
            return UNDEFINED_RESOURCE;
        }

        if (gameId.equals(BUBBLE_SORT)) {
            return R.color.algorythms_red;
        }

        if (gameId.equals(QUICK_SORT)) {
            return R.color.algorythms_blue;
        }

        if (gameId.equals(TREASURE_GAME)) {
            return R.color.algorythms_red; //TODO: get color resource for this
        }

        return UNDEFINED_RESOURCE;
    }

    public static String resolveGameCategory(String gameID) {
        if (gameID == null) {
            return null;
        }

        if (gameID.equals(BUBBLE_SORT) || gameID.equals(BUCKET_SORT) || gameID.equals(QUICK_SORT)
                || gameID.equals(COUNTING_SORT)) {
            return CATEGORY_SORTING_ALG;
        }

        if (gameID.equals(TREASURE_GAME)) {
            return CATEGORY_RANDOM_ALG;
        }

        if (gameID.equals(BST_SEARCH) || gameID.equals(BST_CONSTRUCTION)) {
            return CATEGORY_GRAPH_ALG;
        }

        if (gameID.equals(BINARY_SEARCH)) {
            return CATEGORY_SEARCH_ALG;
        }

        return null;
    }

    public static boolean isValidGameId(String gameId) {
        int gameResource = ResourceResolver.resolveGameNameImage(gameId);
        return (gameResource != ResourceResolver.UNDEFINED_RESOURCE);
    }


    public static String capitalizeString(String string) {
        char[] chars = string.toLowerCase().toCharArray();
        boolean found = false;
        for (int i = 0; i < chars.length; i++) {
            if (!found && Character.isLetter(chars[i])) {
                chars[i] = Character.toUpperCase(chars[i]);
                found = true;
            } else if (Character.isWhitespace(chars[i]) || chars[i]=='.'
                    || chars[i]=='\'') { // You can add other chars here
                found = false;
            }
        }
        return String.valueOf(chars);
    }

    /**
     * This method return -1 in case the card number is illegal (i.e - not a number
     * or is out of numbers range (1 - 10)
     */
    public static int convertCardNumberToInt(String cardNumber) {
        int number = UNDEFINED_RESOURCE;
        try {
            int num = Integer.parseInt(cardNumber);
            if (num >= 1 && num <= 10) {
                number = num;
            }

        } catch (NumberFormatException e) {
            Log.e("ResourceResolver", "card with wrong number value was scanned");
        }

        return number;
    }
}
