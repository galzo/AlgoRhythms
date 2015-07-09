package com.algorythmsteam.algorythms;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.handlers.NdefReader;
import com.handlers.ResourceResolver;

import java.util.ArrayList;


public class AlgoryhmsMainActivity extends ActionBarActivity implements QRScanFragment.QRScanCallback {
    public static final String TAG = "AlgoryhmsMainActivity";
    public static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    public static final String MIME_TEXT_PLAIN = "text/plain";
    private AlgorhythmsFragment _currFrag;
    private NfcAdapter nfcAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_algoryhms_main);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
        }

        else if (!nfcAdapter.isEnabled()) {
            Toast.makeText(this, "nfc is disabled!", Toast.LENGTH_LONG).show();
        } else if(handleIntent(getIntent())) {
            return;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        AlgoryhmsMainFragment frag = new AlgoryhmsMainFragment();
        GameIntroFragment frag = GameIntroFragment.newInstance(ResourceResolver.QUICK_SORT);
        _currFrag = frag;
        fragmentTransaction.add(R.id.main_activity_fragment_container, frag, AlgoryhmsMainFragment.TAG);
        fragmentTransaction.commit();
    }

    private boolean handleIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            String type = intent.getType();
            if (MIME_TEXT_PLAIN.equals(type)) {
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                NdefReader reader = new NdefReader(this);
                reader.execute(tag);
                return true;
            }

            else {
                Log.d(TAG, "Wrong mime type: " + type);
                return false;
            }
        }

        return false;
    }

    public void handleNfcResult(String result) {
        String res = null;

        //lets first validate that the read result is of the format of Algorythms NFC text
        //(I.E - Algorhythms#X where X is the value of the card) - if its not, then do not handle it
        if (result != null && result.trim().length() != 0) {
            Log.i(TAG, "got scan result: " + result);
            String[] splitRes = result.split("#", 2);
            if (splitRes.length == 2 && splitRes[0].equals("algorhythms")) {
                res = splitRes[1].trim();
                Log.i(TAG, "first part of tag is: " + splitRes[0]);
                Log.i(TAG, "second part of tag is: " + splitRes[1]);
            }
        }

        //if the nfc scan came after application was opened, let the current active fragment handle it
        if (_currFrag != null) {
            if (_currFrag.handleNfcScan(res)) {
                return;
            }
        }

        //otherwise - let the activity handle the scan result
        //if the scan result is non-relevant or defected, simply launch the mainScreen
        if (res == null || res.length() == 0 || !ResourceResolver.isValidGameId(res)) {
            launchFragment(new AlgoryhmsMainFragment(), AlgoryhmsMainFragment.TAG, null, null);
        }

        //otherwise - lets launch the relevant game intro screen
        else {
            launchFragment(GameIntroFragment.newInstance(res), GameIntroFragment.TAG, null, null);
        }
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

    //as for now - onActivityResult is used only to retrieve scanning results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult scanningResult = IntentIntegrator
                .parseActivityResult(requestCode, resultCode, data);

        if (scanningResult == null || scanningResult.getContents() == null) {
            launchFragment(new AlgoryhmsMainFragment(), AlgoryhmsMainFragment.TAG, null, null);
            Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT).show();
            return;
        }

        String scanContents = scanningResult.getContents();
        launchFragment(GameIntroFragment.newInstance(scanContents), GameIntroFragment.TAG, null, null);
    }

    public float convertDpToPixel(float dp) {
        Resources resources = getApplicationContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * (metrics.densityDpi / 160f);
    }

    public void launchFragment(AlgorhythmsFragment frag, String tag, Integer enterAnim, Integer exitAnim) {
        _currFrag = frag;

        if (frag != null) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            if (enterAnim != null && exitAnim != null) {
                ft.setCustomAnimations(enterAnim, exitAnim);
            }

            ft.replace(R.id.main_activity_fragment_container, frag, tag);
            ft.commit();
        }

        else {
            Log.e(TAG, "launchFragment: no fragment type was specified");
        }
    }

    @Override
    public void launchQRScanner() {
        IntentIntegrator scanIntegrator = new IntentIntegrator(AlgoryhmsMainActivity.this);
        scanIntegrator.initiateScan();
    }

    @Override
    public void onBackPressed() {
        if (_currFrag != null && _currFrag.isVisible()) {
            if (_currFrag.handleBackPress()) {
                return;
            }
        }

        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
            setupForegroundDispatch(this, nfcAdapter);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null) {
            stopForegroundDispatch(this, nfcAdapter);
        }
    }

    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent =
                PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};

        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType(MIME_TEXT_PLAIN);
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("Check your mime type.");
        }

        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }

    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        /**
         * This method gets called, when a new Intent gets associated with the current activity instance.
         * Instead of creating a new activity, onNewIntent will be called. For more information have a look
         * at the documentation.
         *
         * In our case this method gets called, when the user attaches a Tag to the device.
         */
        if (nfcAdapter != null && nfcAdapter.isEnabled()) {
            handleIntent(intent);
        }
    }

    public AlgorhythmsFragment getCurrFrag() {
        return _currFrag;
    }
}
