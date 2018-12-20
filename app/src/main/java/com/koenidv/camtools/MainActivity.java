package com.koenidv.camtools;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private AHBottomNavigation mNavigation;

    protected ActionBar mActionBar;

    private boolean noUpdate = false;

    private final static String TAG = "MainActivity";

    @SuppressWarnings("RedundantCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final SharedPreferences prefs = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") final SharedPreferences.Editor prefsEdit = prefs.edit();


        if (prefs.getBoolean("system_darkmode", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        mNavigation = findViewById(R.id.navigation);
        mActionBar = getSupportActionBar();


        //ViewPager - Pages
        //
        mViewPager = findViewById(R.id.viewPager);

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);


        //Navigation - Bottom bar
        AHBottomNavigationItem nav_sky = new AHBottomNavigationItem(R.string.title_sky_short, R.drawable.ic_sun, R.color.tab_sky);
        AHBottomNavigationItem nav_exposure = new AHBottomNavigationItem(R.string.title_exposure_short, R.drawable.ic_exposure, R.color.tab_exposure);
        AHBottomNavigationItem nav_focus = new AHBottomNavigationItem(R.string.title_focus_short, R.drawable.ic_focus, R.color.tab_focus);
        AHBottomNavigationItem nav_tools = new AHBottomNavigationItem(R.string.title_tools_short, R.drawable.ic_tools, R.color.colorAccent);

        //tmNavigation.addItem(nav_sky);
        mNavigation.addItem(nav_exposure);
        mNavigation.addItem(nav_focus);
        mNavigation.addItem(nav_tools);

        mNavigation.setDefaultBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mNavigation.setAccentColor(getResources().getColor(R.color.colorAccent));
        mNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        mNavigation.setElevation(10f);

        mViewPager.setCurrentItem(prefs.getInt("lastTab", 0));
        mNavigation.setCurrentItem(prefs.getInt("lastTab", 0));

        mNavigation.setOnTabSelectedListener((position, wasSelected) -> {
            mViewPager.setCurrentItem(position);
            return true;
        });


        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()

        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(final int position) {
                prefsEdit.putInt("lastTab", position).apply();
                mNavigation.setCurrentItem(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel timerChannel = new NotificationChannel(getString(R.string.channel_timer), getString(R.string.channel_timer_name), NotificationManager.IMPORTANCE_LOW);
            timerChannel.setDescription(getString(R.string.channel_timer_description));
            timerChannel.setGroup(getString(R.string.group_timers));
            NotificationChannel timerFinishedChannel = new NotificationChannel(getString(R.string.channel_timer_finished), getString(R.string.channel_timer_finished_name), NotificationManager.IMPORTANCE_HIGH);
            timerFinishedChannel.setDescription(getString(R.string.channel_timer_finished_description));
            timerFinishedChannel.setGroup(getString(R.string.group_timers));


            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannelGroup(new NotificationChannelGroup(getString(R.string.group_timers), getString(R.string.group_timers_name)));
            notificationManager.createNotificationChannel(timerChannel);
            notificationManager.createNotificationChannel(timerFinishedChannel);
        }

    }


    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            //                   // getItem is called to instantiate the fragment for the given page.
            // default           // Return a PlaceholderFragment (defined as a static inner class below).
            //                   return PlaceholderFragment.newInstance(position + 1);
            position += 1; //TODO: Remove when skyfragment is available
            switch (position) {
                case 0:
                    return new SkyFragment();
                case 1:
                    return new ExposureFragment();
                case 2:
                    return new FocusFragment();
                case 3:
                    return new ToolsFragment();
                default:
                    return new SkyFragment();
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3; //TODO: 4 skyfragment is available
        }

        @Override
        public CharSequence getPageTitle(int position) {
            position += 1; //TODO: Remove when skyfragment is available
            switch (position) {
                case 0:
                    return getString(R.string.title_sky_long);
                case 1:
                    return getString(R.string.title_exposure_long);
                case 2:
                    return getString(R.string.title_focus_long);
                case 3:
                    return getString(R.string.title_tools_long);
            }
            return null;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_default, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_search:
                break;
            case R.id.action_settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
            case R.id.action_help:
                //startActivity(new Intent(MainActivity.this, FragmentActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ModuleManager mModuleManager = new ModuleManager();
            mModuleManager.showHistory(MainActivity.this);
            return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateGUI(intent); // or whatever method used to update your GUI fields
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(TimerService.ACTION));
        Log.i(TAG, "Registered broacast receiver");
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
        Log.i(TAG, "Unregistered broacast receiver");
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

    Snackbar mTimerSnackbar;
    TextView mSnackBarText;

    private void updateGUI(Intent intent) {
        if (intent.getExtras() != null) {
            ModuleManager mModuleManager = new ModuleManager();
            long millisUntilFinished = intent.getLongExtra("remaining", 0);
            int maxTime = intent.getIntExtra("max", 0);
            String name = intent.getStringExtra("name");

            if (intent.getBooleanExtra("dismiss", false)) {
                mTimerSnackbar.dismiss();
            } else if (millisUntilFinished == 0) {
                mTimerSnackbar.dismiss();
                Snackbar.make(findViewById(R.id.snackbarView), String.format(getString(R.string.timer_finished), name), Snackbar.LENGTH_LONG)
                        .setAction(R.string.okay, v -> mTimerSnackbar.dismiss())
                        .show();
            } else {
                if (mTimerSnackbar == null || !mTimerSnackbar.isShown()) {
                    mTimerSnackbar = Snackbar.make(findViewById(R.id.snackbarView),
                            String.format(getString(R.string.timer_text), name, mModuleManager.convertMilliseconds(millisUntilFinished)),
                            Snackbar.LENGTH_INDEFINITE);
                    mTimerSnackbar
                            .setAction(R.string.show, v -> {
                                TimerSheet sheet = new TimerSheet();
                                sheet.startTime = maxTime / 1000;
                                sheet.tagName = name;
                                sheet.show(getSupportFragmentManager(), "timer");
                            })
                            .setBehavior(new BaseTransientBottomBar.Behavior() {
                                @Override
                                public boolean canSwipeDismissView(View child) {
                                    return false;
                                }
                            })
                            .show();
                    mSnackBarText = mTimerSnackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
                } else {
                    mSnackBarText.setText(String.format(getString(R.string.timer_text), name, mModuleManager.convertMilliseconds(millisUntilFinished)));
                }
            }
        }
    }
}
