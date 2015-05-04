package com.handlers;

import com.algorythmsteam.algorythms.R;

public class ResourceResolver {
    public static final String BUBBLE_SORT = "bubble_sort";
    public static final String BUCKET_SORT = "bucket_sort";

    public static final String CATEGORY_SORTING_ALG = "Sorting Algorithm";
    public static final int UNDEFINED_RESOURCE = -1;

    public static int resolveVideoResource(String gameId) {
        if (gameId == null) {
            return UNDEFINED_RESOURCE;
        }

        if (gameId.equals(BUBBLE_SORT)) {
            return R.raw.bubble_sort;
        }

        return UNDEFINED_RESOURCE;
    }

    public static String resolveGameName(String gameId) {
        if (gameId == null || !gameId.contains("_")) {
            return null;
        }

        return capitalizeString(gameId.replace("_", " "));
    }

    public static String resolveGameCategory(String gameID) {
        if (gameID == null) {
            return null;
        }

        if (gameID.equals(BUBBLE_SORT) || gameID.equals(BUCKET_SORT)) {
            return CATEGORY_SORTING_ALG;
        }

        return null;
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
}
