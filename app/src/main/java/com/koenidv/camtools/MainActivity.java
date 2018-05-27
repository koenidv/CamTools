package com.koenidv.camtools;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.OvershootInterpolator;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private AHBottomNavigation mNavigation;
    private FloatingActionButton mFab;

    protected ActionBar mActionBar;

    private int mActionBarColorFrom = 0;
    private boolean noUpdate = false;

    @SuppressWarnings("RedundantCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SharedPreferences prefs = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") final SharedPreferences.Editor prefsEdit = prefs.edit();


        //
        mFab = findViewById(R.id.mainFab);
        mNavigation = findViewById(R.id.navigation);
        mActionBar = getSupportActionBar();

        //Darkmode
        if (prefs.getBoolean("darkmode", false)) {
            mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(prefs.getString("custom_dark_darker", getString((int) R.color.background_dark_darker)))));
            mNavigation.setBackgroundColor(getResources().getColor(R.color.background_dark_darker));
            mNavigation.setAccentColor(getResources().getColor(R.color.background_light_lighter));
        } else {
            if (!prefs.getBoolean("colors", true)) {
                mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(prefs.getString("custom_light_lighter", getString((int) R.color.background_light_lighter)))));
                mActionBar.setTitle(Html.fromHtml("<font color=\"gray\">" + mActionBar.getTitle() + "</font>"));
                setOverflowButtonColor(this, Color.GRAY);
            }
        }


        //ViewPager - Pages
        //
        mViewPager = findViewById(R.id.viewPager);
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);


        //Navigation - Bottom bar
        //Items TODO: Make this customizable
        AHBottomNavigationItem nav_sun = new AHBottomNavigationItem(R.string.title_sun_short, R.drawable.ic_sun_black_24dp, R.color.tab_sun);
        AHBottomNavigationItem nav_stars = new AHBottomNavigationItem(R.string.title_stars_short, R.drawable.ic_stars_black_24dp, R.color.tab_stars);
        AHBottomNavigationItem nav_focus = new AHBottomNavigationItem(R.string.title_focus_short, R.drawable.ic_focus_black_24dp, R.color.tab_focus);
        AHBottomNavigationItem nav_nd = new AHBottomNavigationItem(R.string.title_nd_short, R.drawable.ic_nd_black_24dp, R.color.tab_nd);
        AHBottomNavigationItem nav_settings = new AHBottomNavigationItem(R.string.title_settings_short, R.drawable.ic_settings_black_24dp, R.color.tab_settings);

        nav_sun.setColor(Color.parseColor(prefs.getString("custom_tab_sun", getString((int) R.color.tab_sun))));
        nav_stars.setColor(Color.parseColor(prefs.getString("custom_tab_stars", getString((int) R.color.tab_stars))));
        nav_focus.setColor(Color.parseColor(prefs.getString("custom_tab_focus", getString((int) R.color.tab_focus))));
        nav_nd.setColor(Color.parseColor(prefs.getString("custom_tab_nd", getString((int) R.color.tab_nd))));
        nav_settings.setColor(Color.parseColor(prefs.getString("custom_tab_settings", getString((int) R.color.tab_settings))));

        mNavigation.addItem(nav_sun);
        mNavigation.addItem(nav_stars);
        mNavigation.addItem(nav_focus);
        mNavigation.addItem(nav_nd);
        mNavigation.addItem(nav_settings);

        //Default color
        if (prefs.getBoolean("darkmode", false)) {
            mNavigation.setDefaultBackgroundColor(getResources().getColor(R.color.background_dark_normal));
            mNavigation.setAccentColor(getResources().getColor(R.color.background_light_lighter));
        } else {
            mNavigation.setDefaultBackgroundColor(Color.parseColor(prefs.getString("custom_light_lighter", getString((int) R.color.background_light_lighter))));
        }
        //mNavigation.setTranslucentNavigationEnabled(true);
        if (prefs.getBoolean("colors", true)) {
            mNavigation.setColored(true);
        }
        if (prefs.getBoolean("save", true)) {
            mViewPager.setCurrentItem(prefs.getInt("lastTab", 0));
            mNavigation.setCurrentItem(prefs.getInt("lastTab", 0));
            if (prefs.getBoolean("colors", true)) {
                int tabColor;
                //@formatter:off
                switch (prefs.getInt("lastTab", 0)) {
                    case 0: tabColor = Color.parseColor(prefs.getString("custom_tab_sun", getString((int) R.color.tab_sun)));break;
                    case 1: tabColor = Color.parseColor(prefs.getString("custom_tab_stars", getString((int) R.color.tab_stars)));break;
                    case 2: tabColor = Color.parseColor(prefs.getString("custom_tab_focus", getString((int) R.color.tab_focus)));break;
                    case 3: tabColor = Color.parseColor(prefs.getString("custom_tab_nd", getString((int) R.color.tab_nd)));break;
                    case 4: tabColor = Color.parseColor(prefs.getString("custom_tab_settings", getString((int) R.color.tab_settings)));break;
                    default: tabColor = Color.parseColor(prefs.getString("custom_tab_settings", getString((int) R.color.tab_settings)));
                }
                //@formatter:on
                mActionBarColorFrom = tabColor;
                mActionBar.setBackgroundDrawable(new ColorDrawable(mActionBarColorFrom));
            }
        }
        if (prefs.getBoolean("labels", false)) {
            mNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        }
        if (!prefs.getBoolean("nav", true)) {
            mNavigation.hideBottomNavigation();
        }


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

        mNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @SuppressWarnings("RedundantCast")
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                if (!noUpdate) {
                    mViewPager.setCurrentItem(position);
                    invalidateOptionsMenu();
                    if (prefs.getBoolean("colors", true)) {
                        int tabColor;
                        //@formatter:off
                        switch (position) {
                            case 0: tabColor = Color.parseColor(prefs.getString("custom_tab_sun", getString((int) R.color.tab_sun)));break;
                            case 1: tabColor = Color.parseColor(prefs.getString("custom_tab_stars", getString((int) R.color.tab_stars)));break;
                            case 2: tabColor = Color.parseColor(prefs.getString("custom_tab_focus", getString((int) R.color.tab_focus)));break;
                            case 3: tabColor = Color.parseColor(prefs.getString("custom_tab_nd", getString((int) R.color.tab_nd)));break;
                            case 4: tabColor = Color.parseColor(prefs.getString("custom_tab_settings", getString((int) R.color.tab_settings)));break;
                            default: tabColor = Color.parseColor(prefs.getString("custom_tab_settings", getString((int) R.color.tab_settings)));
                        }
                        //@formatter:on
                        if (mActionBarColorFrom == 0) {
                            mActionBarColorFrom = getResources().getColor(R.color.tab_settings);
                        }
                        int colorFrom = mActionBarColorFrom;
                        int colorTo = tabColor;
                        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
                        colorAnimation.setDuration(300); // milliseconds
                        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animator) {
                                mActionBar.setBackgroundDrawable(new ColorDrawable((int) animator.getAnimatedValue()));
                                mActionBarColorFrom = (int) animator.getAnimatedValue();
                            }
                        });
                        colorAnimation.start();
                        mActionBar.setBackgroundDrawable(new ColorDrawable(tabColor));
                    }
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


        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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

                            if (prefs.getBoolean("colors", true)) {
                                int tabColor;
                                //@formatter:off
                                switch (position) {
                                    case 0: tabColor = Color.parseColor(prefs.getString("custom_tab_sun", getString((int) R.color.tab_sun)));break;
                                    case 1: tabColor = Color.parseColor(prefs.getString("custom_tab_stars", getString((int) R.color.tab_stars)));break;
                                    case 2: tabColor = Color.parseColor(prefs.getString("custom_tab_focus", getString((int) R.color.tab_focus)));break;
                                    case 3: tabColor = Color.parseColor(prefs.getString("custom_tab_nd", getString((int) R.color.tab_nd)));break;
                                    case 4: tabColor = Color.parseColor(prefs.getString("custom_tab_settings", getString((int) R.color.tab_settings)));break;
                                    default: tabColor = Color.parseColor(prefs.getString("custom_tab_settings", getString((int) R.color.tab_settings)));
                                }
                                //@formatter:on
                                if (mActionBarColorFrom == 0) {
                                    mActionBarColorFrom = getResources().getColor(R.color.tab_settings);
                                }
                                int colorFrom = mActionBarColorFrom;
                                int colorTo = tabColor;
                                ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
                                colorAnimation.setDuration(300); // milliseconds
                                colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                                    @Override
                                    public void onAnimationUpdate(ValueAnimator animator) {
                                        mActionBar.setBackgroundDrawable(new ColorDrawable((int) animator.getAnimatedValue()));
                                        mActionBarColorFrom = (int) animator.getAnimatedValue();

                                    }

                                });
                                colorAnimation.start();
                                mActionBar.setBackgroundDrawable(new ColorDrawable(tabColor));
                            }
                        }
                    }
                };

                Handler mHandler = new Handler();
                mHandler.postDelayed(r, 1);

                /* ToDo
                if (position == 3) {
                    mFab.setVisibility(View.VISIBLE);
                    try {
                        //noinspection ConstantConditions
                        Drawable mFabIcon = mFab.getDrawable().getConstantState().newDrawable();
                        if (prefs.getBoolean("colors", true)) {
                            mFab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.tab_nd)));
                            mFabIcon.mutate().setColorFilter(getResources().getColor(R.color.background_light_lighter), PorterDuff.Mode.SRC_IN);
                        } else if (prefs.getBoolean("darkmode", false)) {
                            mFab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.background_dark_normal)));
                            mFabIcon.mutate().setColorFilter(getResources().getColor(R.color.background_light_lighter), PorterDuff.Mode.SRC_IN);
                        } else {
                            mFab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.background_light_lighter)));
                            mFabIcon.mutate().setColorFilter(getResources().getColor(R.color.background_dark_darker), PorterDuff.Mode.MULTIPLY);
                        }
                        mFab.setImageDrawable(mFabIcon);
                    } catch (NullPointerException npe) {
                        Snackbar.make(mNavigation, "An Error occurred. Please don't try again.", Snackbar.LENGTH_SHORT).show();
                    }

                    mFab.setAlpha(0f);
                    mFab.setScaleX(0f);
                    mFab.setScaleY(0f);
                    mFab.animate()
                            .alpha(1)
                            .scaleX(1)
                            .scaleY(1)
                            .setDuration(300)
                            .setInterpolator(new OvershootInterpolator())
                            .setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    mFab.animate()
                                            .setInterpolator(new LinearOutSlowInInterpolator())
                                            .start();
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            })
                            .start();
                } else {
                    if (mFab.getVisibility() == View.VISIBLE) {
                        mFab.animate()
                                .alpha(0)
                                .scaleX(0)
                                .scaleY(0)
                                .setDuration(300)
                                .setInterpolator(new LinearOutSlowInInterpolator())
                                .setListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        mFab.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animation) {
                                        mFab.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animation) {

                                    }
                                })
                                .start();
                    }
                }
                */
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
                    return new SunFragment();
                case 1:
                    return new StarsFragment();
                case 2:
                    return new FocusFragment();
                case 3:
                    return new NDFragment();
                case 4:
                    return new SettingsFragment();
                default:
                    return new SunFragment();
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.title_sun_long);
                case 1:
                    return getString(R.string.title_stars_long);
                case 2:
                    return getString(R.string.title_focus_long);
                case 3:
                    return getString(R.string.title_nd_long);
                case 4:
                    return getString(R.string.title_settings_long);
            }
            return null;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mViewPager = findViewById(R.id.viewPager);
        //@formatter:off
        switch (mViewPager.getCurrentItem()) {
            case 0: getMenuInflater().inflate(R.menu.menu_sun, menu);return true; //Sun tab menu
            case 1: getMenuInflater().inflate(R.menu.menu_stars, menu);return true; //Stars tab menu
            case 2: getMenuInflater().inflate(R.menu.menu_focus, menu);return true; //Focus tab menu
            case 3: getMenuInflater().inflate(R.menu.menu_nd, menu);return true; //ND tab menu
            case 4: getMenuInflater().inflate(R.menu.menu_settings, menu);return true; //Settings tab menu
        }
        //@formatter:on
        getMenuInflater().inflate(R.menu.overflow_main, menu); //Menu for all tabs
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
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
        final String overflowDescription = activity.getString(R.string.overflow_description);
        final ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        final ViewTreeObserver viewTreeObserver = decorView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                final ArrayList<View> outViews = new ArrayList<>();
                decorView.findViewsWithText(outViews, overflowDescription, View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
                if (outViews.isEmpty()) {
                    System.out.print("");
                    return;
                }
                AppCompatImageView overflow = (AppCompatImageView) outViews.get(0);
                overflow.setColorFilter(color);
            }
        });
    }
}
