package com.koenidv.camtools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

public class MainActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private AHBottomNavigation mNavigation;
    private FloatingActionButton mFab;

    protected ActionBar mActionBar;

    private boolean noUpdate = false;

    @SuppressWarnings("RedundantCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final SharedPreferences prefs = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") final SharedPreferences.Editor prefsEdit = prefs.edit();

        FirebaseApp.initializeApp(getApplicationContext());
        final FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);
        mFirebaseRemoteConfig.fetch(mFirebaseRemoteConfig.getLong("config_cache"))
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Fetch Succeeded",
                                    Toast.LENGTH_SHORT).show();

                            // After config data is successfully fetched, it must be activated before newly fetched
                            // values are returned.
                            mFirebaseRemoteConfig.activateFetched();
                        } else {
                            Toast.makeText(MainActivity.this, "Fetch Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });


        mFab = findViewById(R.id.mainFab);
        mNavigation = findViewById(R.id.navigation);
        mActionBar = getSupportActionBar();

        //Darkmode
        if (prefs.getBoolean("darkmode", false)) {
            mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(prefs.getString("custom_dark_darker", getString((int) R.color.background_dark_darker)))));
            mNavigation.setBackgroundColor(getResources().getColor(R.color.background_dark_darker));
            mNavigation.setAccentColor(getResources().getColor(R.color.background_light_lighter));
        } else {
            mActionBar.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            mActionBar.setTitle(Html.fromHtml("<font color=\"gray\">" + mActionBar.getTitle() + "</font>"));
            setOverflowButtonColor(this, Color.GREEN);
        }


        //ViewPager - Pages
        //
        mViewPager = findViewById(R.id.viewPager);

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);


        //Navigation - Bottom bar
        AHBottomNavigationItem nav_sky = new AHBottomNavigationItem(R.string.title_sky_short, R.drawable.ic_sun, R.color.tab_sky);
        AHBottomNavigationItem nav_exposure = new AHBottomNavigationItem(R.string.title_exposure_short, R.drawable.ic_density, R.color.tab_exposure);
        AHBottomNavigationItem nav_focus = new AHBottomNavigationItem(R.string.title_focus_short, R.drawable.ic_focus, R.color.tab_focus);

        //nav_sky.setColor(getResources().getColor(R.color.tab_sky));
        //nav_exposure.setColor(getResources().getColor(R.color.tab_exposure));
        //nav_focus.setColor(getResources().getColor(R.color.tab_focus));
        //nav_settings.setColor(getResources().getColor(R.color.tab_settings));

        mNavigation.addItem(nav_sky);
        mNavigation.addItem(nav_exposure);
        mNavigation.addItem(nav_focus);
        //ToDO: Settings in overflow menu
        // mNavigation.addItem(nav_settings);

        //Default color
        if (prefs.getBoolean("darkmode", false)) {
            mNavigation.setDefaultBackgroundColor(getResources().getColor(R.color.background_dark_normal));
            mNavigation.setAccentColor(getResources().getColor(R.color.background_light_lighter));
        } else {
            mNavigation.setDefaultBackgroundColor(Color.WHITE);
            mNavigation.setAccentColor(getResources().getColor(R.color.colorAccent));
        }

        mNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        mNavigation.setElevation(10f);


        mViewPager.setCurrentItem(prefs.getInt("lastTab", 0));
        mNavigation.setCurrentItem(prefs.getInt("lastTab", 0));

        //Runnable r = new Runnable() {
        //    @Override
        //    public void run() {
        //        boolean t = mNavigation.isShown();
        //        MainActivity.this.refreshFrame();
        //    }
        //};
//
        //Handler mHandler = new Handler();
        //mHandler.postDelayed(r, 100);

        mNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener()

        {
            @SuppressWarnings("RedundantCast")
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                if (!noUpdate) {
                    mViewPager.setCurrentItem(position);
                    //invalidateOptionsMenu();
                }
                return true;
            }
        });


        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CustomColorsActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.nothing);
            }
        });


        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()

        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(final int position) {
                prefsEdit.putInt("lastTab", position).apply();

                Runnable r = new Runnable() {
                    @SuppressWarnings("RedundantCast")
                    @Override
                    public void run() {
                        if (mNavigation.getCurrentItem() != position) {
                            noUpdate = true;
                            mNavigation.setCurrentItem(position);
                            noUpdate = false;
                            invalidateOptionsMenu();
                        }
                    }
                };

                Handler mHandler = new Handler();
                mHandler.postDelayed(r, 1);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
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
            //TODO: Customizability
            switch (position) {
                case 0:
                    return new SkyFragment();
                case 1:
                    return new ExposureFragment();
                case 2:
                    return new FocusFragment();
                default:
                    return new SkyFragment();
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.title_sky_long);
                case 1:
                    return getString(R.string.title_exposure_long);
                case 2:
                    return getString(R.string.title_focus_long);
                case 3:
                    return getString(R.string.title_settings_long);
            }
            return null;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_default, menu);
        return true;

        //mViewPager = findViewById(R.id.viewPager);
        //@formatter:off
        /*switch (mViewPager.getCurrentItem()) {
            case 0: getMenuInflater().inflate(R.menu.menu_sun, menu);return true; //Sun tab menu
            case 1: getMenuInflater().inflate(R.menu.menu_stars, menu);return true; //Stars tab menu
            case 2: getMenuInflater().inflate(R.menu.menu_focus, menu);return true; //Focus tab menu
            case 3: getMenuInflater().inflate(R.menu.menu_nd, menu);return true; //ND tab menu
            case 4: getMenuInflater().inflate(R.menu.menu_settings, menu);return true; //Settings tab menu
        }*/
        // Add invalidateOptionsMenu(); in onTabSelected if tab-specific menus are needed again.
        //@formatter:on
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_help:
                //startActivity(new Intent(MainActivity.this, FragmentActivity.class));
                break;
            case R.id.action_settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
            case R.id.action_switch_advancedSettings:
                @SuppressLint("CommitPrefEdits") SharedPreferences.Editor prefsEditor = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE).edit();
                prefsEditor.putBoolean("advancedSettings", !getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE).getBoolean("advancedSettings", false)).apply();
                MainActivity.this.recreate();
                break;
            case R.id.action_focus_calculate:
                Intent fcBrowserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_focus_more_calculate)));
                startActivity(fcBrowserIntent);
                break;
            case R.id.action_focus_help:
                Intent fhBrowserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_focus_help)));
                startActivity(fhBrowserIntent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public static void setOverflowButtonColor(final Activity activity, final int color) {
        /*final String overflowDescription = activity.getString(R.string.overflow_description);
        final ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        final ViewTreeObserver viewTreeObserver = decorView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                final ArrayList<View> outViews = new ArrayList<>();
                decorView.findViewsWithText(outViews, overflowDescription, View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
                if (outViews.isEmpty()) {
                    return;
                }
                AppCompatImageView overflow = (AppCompatImageView) outViews.get(0);
                overflow.setColorFilter(color);
                */
    }
}
