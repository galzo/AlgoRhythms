package com.algorythmsteam.algorythms;

import android.support.v4.app.Fragment;

/**
 * Base class for all fragments in the Algorythms game application
 */
public abstract class AlgorhythmsFragment extends Fragment {
    public abstract boolean handleBackPress();
    public abstract boolean handleNfcScan(String res);
}
