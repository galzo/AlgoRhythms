package com.algorythmsteam.algorythms;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class GameIntroFragment extends Fragment {
    public interface GameLauncherCallback {
        public void launchGame(String type);
        public void launchGameInstructions(String type);
    }

    // the fragment initialization parameters, e.g. the game type
    private static final String ARG_GAME_TYPE = "game_type";
    private String gameType;
    private GameLauncherCallback callback;

    public static GameIntroFragment newInstance(String gameType) {
        GameIntroFragment fragment = new GameIntroFragment();
        Bundle args = new Bundle();
        args.putString(ARG_GAME_TYPE, gameType);
        fragment.setArguments(args);
        return fragment;
    }

    public GameIntroFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            gameType = getArguments().getString(ARG_GAME_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game_intro, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            callback = (GameLauncherCallback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement GameLauncherCallback");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }
}
