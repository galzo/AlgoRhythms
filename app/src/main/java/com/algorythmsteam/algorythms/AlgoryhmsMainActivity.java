package com.algorythmsteam.algorythms;

import android.content.res.Resources;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


public class AlgoryhmsMainActivity extends ActionBarActivity
        implements AlgoryhmsMainFragment.MainActivityCallback {
    public static final String TAG = "AlgoryhmsMainActivity";
    public static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    private Fragment _currFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_algoryhms_main);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        AlgoryhmsMainFragment frag = new AlgoryhmsMainFragment();
        _currFrag = frag;
        fragmentTransaction.add(R.id.main_activity_fragment_container, frag, AlgoryhmsMainFragment.TAG);
        fragmentTransaction.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_algoryhms_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void initiateScan() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                IntentIntegrator scanIntegrator = new IntentIntegrator(AlgoryhmsMainActivity.this);
                scanIntegrator.initiateScan();
            }
        }, 200);
    }

    @Override
    public void openBuzzer() {
        BuzzerFragment bf = BuzzerFragment.newInstance();
        _currFrag = bf;
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.anim.enter, R.anim.exit);
        ft.replace(R.id.main_activity_fragment_container, bf, BuzzerFragment.TAG);
        ft.commit();
    }

    //as for now - onActivityResult is used only to retrieve scanning results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult scanningResult = IntentIntegrator
                .parseActivityResult(requestCode, resultCode, data);

        if (scanningResult == null || scanningResult.getContents() == null) {
            if (_currFrag instanceof AlgoryhmsMainFragment) {
                ((AlgoryhmsMainFragment) _currFrag).setIsAnimInit(false);
                getSupportFragmentManager()
                        .beginTransaction()
                        .detach(_currFrag)
                        .attach(_currFrag)
                        .commit();
            }
            
            Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT).show();
            return;
        }

        FragmentManager fm = getSupportFragmentManager();
        String scanContents = scanningResult.getContents();
        GameDescriptionFragment gdf =
                GameDescriptionFragment.newInstance(scanContents);
        _currFrag = gdf;
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.main_activity_fragment_container, gdf, GameDescriptionFragment.TAG);
        ft.commit();
    }

    public float convertDpToPixel(float dp){
        Resources resources = getApplicationContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    @Override
    public void onBackPressed() {
        if (_currFrag.isVisible() && !(_currFrag instanceof AlgoryhmsMainFragment)) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            AlgoryhmsMainFragment amf = new AlgoryhmsMainFragment();
            _currFrag = amf;
            fragmentTransaction.setCustomAnimations(R.anim.enter_reverse, R.anim.exit_reverse);
            fragmentTransaction.replace(R.id.main_activity_fragment_container, amf, AlgoryhmsMainFragment.TAG);
            fragmentTransaction.commit();
            return;
        }

        super.onBackPressed();
    }
}
