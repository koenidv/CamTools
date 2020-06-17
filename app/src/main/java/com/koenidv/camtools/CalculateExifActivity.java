package com.koenidv.camtools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.asksira.bsimagepicker.BSImagePicker;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import it.sephiroth.android.library.exif2.ExifInterface;
import it.sephiroth.android.library.exif2.ExifTag;

public class CalculateExifActivity extends AppCompatActivity implements BSImagePicker.OnSingleImageSelectedListener {

    final private static String TAG = "EXIF reader";
    private Snackbar mTimerSnackBar;
    private TextView mSnackBarText;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateGUI(intent); // or whatever method used to update your GUI fields
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate_exif);
        SharedPreferences prefs = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);

        BSImagePicker imagePicker = new BSImagePicker.Builder("com.koenidv.camtools.fileprovider")
                .hideCameraTile()
                .build();

        if (!prefs.getString("last_image", "").equals("")) {
            try {
                //Glide.with(this)
                //        .load(prefs.getString("last_image", ""))
                //        .into((ImageView) findViewById(R.id.imageView));
                onSingleImageSelected(Uri.parse(prefs.getString("last_image", "")), "saved");
            } catch (Exception e) {
                e.printStackTrace();
                imagePicker.show(getSupportFragmentManager(), "imagepicker");
            }
        } else {
            imagePicker.show(getSupportFragmentManager(), "imagepicker");
        }

        findViewById(R.id.pickImageButton).setOnClickListener(v -> {
            imagePicker.show(getSupportFragmentManager(), "imagepicker");
        });
    }

    @Override
    public void onSingleImageSelected(Uri uri, String tag) {
        //Do something with your Uri
        ImageView image = findViewById(R.id.imageView);
        SharedPreferences.Editor prefsEdit = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE).edit();
        prefsEdit.putString("last_image", uri.toString()).apply();
        Glide.with(this).load(uri).into(image);


        if (Build.VERSION.SDK_INT >= 24) {
            InputStream in = null;
            try {
                in = getContentResolver().openInputStream(uri);
                ExifInterface exif = new ExifInterface();
                exif.readExif(in, ExifInterface.Options.OPTION_ALL);

                List<ExifTag> all_tags = exif.getAllTags();

                StringBuilder out = new StringBuilder();

                if (all_tags != null) {
                    for (ExifTag exiftag : all_tags) {
                        if (exiftag.getValueAsString(null) != null) {
                            switch (exiftag.getTagId()) {
                                case 271:
                                    out.append("Make : ");
                                    break;
                                case 272:
                                    out.append("Model : ");
                                    break;
                                case 305:
                                    out.append("Software : ");
                                    break;
                                case 306:
                                    out.append("Date : ");
                                    break;
                            }
                            out.append(exiftag.getValueAsString());
                            out.append("<br/>");
//                            Toast.makeText(this, exiftag.getValueAsString(), Toast.LENGTH_SHORT).show();
                        }
                    }
//                } else {
//                    Toast.makeText(this, "all_tags == null", Toast.LENGTH_LONG).show();
                }

                ((TextView) findViewById(R.id.textView)).setText(Html.fromHtml(out.toString()));

            } catch (IOException ioe) {
                ioe.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException ignored) {
                    }
                }
            }
        }
    }

    @Override
    public void onStop() {
        try {
            unregisterReceiver(receiver);
        } catch (Exception e) {
            // Receiver was probably already stopped in onPause()
        }
        super.onStop();
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ModuleManager mModuleManager = new ModuleManager();
            mModuleManager.showHistory(CalculateExifActivity.this);
            return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_calculator, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_add_shortcut:
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    ShortcutManager mShortcutManager = getSystemService(ShortcutManager.class);
                    assert mShortcutManager != null;
                    if (mShortcutManager.isRequestPinShortcutSupported()) {

                        ShortcutInfo pinShortcutInfo =
                                new ShortcutInfo.Builder(CalculateExifActivity.this, "tools_cropfactor")
                                        .setShortLabel(getString(R.string.shortcut_exif))
                                        .setIcon(Icon.createWithResource(getBaseContext(), R.mipmap.shortcut_cropfactor)) //todo
                                        .setIntent(new Intent().setAction(Intent.ACTION_VIEW).setClass(getApplicationContext(), CalculateCropfactorActivity.class))
                                        .build();

                        mShortcutManager.requestPinShortcut(pinShortcutInfo, null);
                    }
                }
                break;
            case R.id.action_settings:
                startActivity(new Intent(CalculateExifActivity.this, SettingsActivity.class));
                break;
            case R.id.action_help:

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
        Log.i(TAG, "Unregistered broacast receiver");
    }

    @Override
    protected void onResume() {
        registerReceiver(receiver, new IntentFilter(TimerService.ACTION));
        Log.i(TAG, "Registered broacast receiver");
        super.onResume();
    }

    private void updateGUI(Intent intent) {
        if (intent.getExtras() != null) {
            ModuleManager mModuleManager = new ModuleManager();
            long millisUntilFinished = intent.getLongExtra("remaining", 0);
            int maxTime = intent.getIntExtra("max", 0);
            String name = intent.getStringExtra("name");

            if (intent.getBooleanExtra("dismiss", false)) {
                mTimerSnackBar.dismiss();
            } else if (millisUntilFinished == 0) {
                mTimerSnackBar.dismiss();
                Snackbar.make(findViewById(R.id.rootView), String.format(getString(R.string.timer_finished), name), Snackbar.LENGTH_LONG)
                        .setAction(R.string.okay, v -> mTimerSnackBar.dismiss())
                        .show();
            } else {
                if (mTimerSnackBar == null || !mTimerSnackBar.isShown()) {
                    mTimerSnackBar = Snackbar.make(findViewById(R.id.rootView),
                            String.format(getString(R.string.timer_text), name, mModuleManager.convertMilliseconds(millisUntilFinished)),
                            Snackbar.LENGTH_INDEFINITE);
                    mTimerSnackBar
                            .setAction(R.string.show, v -> {
                                TimerSheet sheet = new TimerSheet();
                                sheet.startTime = maxTime / 1000;
                                sheet.tagName = name;
                                sheet.show(getSupportFragmentManager(), "timer");
                            })
                            .show();
                    mSnackBarText = mTimerSnackBar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
                } else {
                    mSnackBarText.setText(String.format(getString(R.string.timer_text), name, mModuleManager.convertMilliseconds(millisUntilFinished)));
                }
            }
        }
    }
}
