package com.handlers;

import com.algorythmsteam.algorythms.R;

public class ResourceResolver {
    public static final String BUBBLE_SORT = "bubble_sort";
    public static final String BUCKET_SORT = "bucket_sort";
    public static final String QUICK_SORT = "quick_sort";
    public static final String COUNTING_SORT = "counting_sort";
    public static final String BINARY_SEARCH = "binary_search";
    public static final String BST_CONSTRUCTION = "bst_constructions";
    public static final String BST_SEARCH = "bst_search";

    public static final String CATEGORY_SORTING_ALG = "Sorting Algorithm";
    public static final String CATEGORY_GRAPH_ALG = "Graph Algorithm";
    public static final String CATEGORY_SEARCH_ALG = "Search Algorithm";

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

    public static int resolveGameNameImage(String gameId) {
        if (gameId == null) {
            return UNDEFINED_RESOURCE;
        }

        if (gameId.equals(BUBBLE_SORT)) {
            return R.drawable.bubble_sort_title;
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

        if (gameID.equals(BST_SEARCH) || gameID.equals(BST_CONSTRUCTION)) {
            return CATEGORY_GRAPH_ALG;
        }

        if (gameID.equals(BINARY_SEARCH)) {
            return CATEGORY_SEARCH_ALG;
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
