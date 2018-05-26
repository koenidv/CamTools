package com.koenidv.camtools;
//  Created by koenidv on 19.01.2018.

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;

import org.w3c.dom.Text;

public class SettingsFragment extends Fragment {

    //Called when Fragment should create its View object hierarchy
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        //Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_settings, parent, false);
    }


    private Space mSinglehandSpace;
    private ExpandableLinearLayout mExpandableLayout;
    private Switch mDarkmodeSwitch;
    private Switch mColorsSwitch;
    private Switch mNavbarSwitch;
    private Switch mLabelsSwitch;
    private Switch mSinglehandSwitch;
    private Switch mSwipeSwitch;
    private Switch mSaveSwitch;
    private ImageView mExpandSymbol;
    private AHBottomNavigation mNavigation;

    //Called after onCreateView(). View setup here.
    @SuppressWarnings("RedundantCast")
    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        @SuppressWarnings("ConstantConditions") final SharedPreferences prefs = getActivity().getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") final SharedPreferences.Editor prefsEdit = prefs.edit();

        LinearLayout mBackgroundLayout = view.findViewById(R.id.settingsLinearLayout);
        mSinglehandSpace = view.findViewById(R.id.settingsSinglehandSpace);
        final CardView mFunctionsCard = view.findViewById(R.id.functionsCard);
        CardView mCamCard = view.findViewById(R.id.settingsCamCard);
        ImageView mCamSymbol = view.findViewById(R.id.settingsCamSymbol);
        TextView mCamText = view.findViewById(R.id.settingsCamTextView);
        LinearLayout mChooseCamLayout = view.findViewById(R.id.settingsChooseCamLayout);
        ImageView mChooseCamSymbol = view.findViewById(R.id.settingsChooseCamSymbol);
        TextView mChooseCamText = view.findViewById(R.id.settingsChooseCamTextView);
        LinearLayout mCamCocLayout = view.findViewById(R.id.settingsCamCocLayout);
        ImageView mCamCocSymbol = view.findViewById(R.id.settingsCamCocSymbol);
        TextView mCamCocText = view.findViewById(R.id.settingsCamCocTextView);
        CardView mNdCard = view.findViewById(R.id.settingsNdCard);
        ImageView mNdSymbol = view.findViewById(R.id.settingsNdSymbol);
        TextView mNdText = view.findViewById(R.id.settingsNdTextView);
        LinearLayout mNdStrengthunitLayout = view.findViewById(R.id.settingsNdStrengthunitLayout);
        ImageView mNdStrengthunitSymbol = view.findViewById(R.id.settingsNdStrengthunitSymbol);
        TextView mNdStrengthunitText = view.findViewById(R.id.settingsNdStrengthunitTextView);
        CardView mAppearanceCard = view.findViewById(R.id.appearanceCard);
        final CardView mAboutCard = view.findViewById(R.id.aboutCard);
        TextView mAppearanceText = view.findViewById(R.id.appearanceTextView);
        LinearLayout mDarkmodeLayout = view.findViewById(R.id.darkmodeLayout);
        LinearLayout mColorsLayout = view.findViewById(R.id.colorLayout);
        LinearLayout mNavbarLayout = view.findViewById(R.id.navbarLayout);
        LinearLayout mLabelsLayout = view.findViewById(R.id.labelsLayout);
        LinearLayout mSinglehandLayout = view.findViewById(R.id.singlehandLayout);
        final LinearLayout mSwipeLayout = view.findViewById(R.id.swipeLayout);
        LinearLayout mSaveLayout = view.findViewById(R.id.saveLayout);
        LinearLayout mRateLayout = view.findViewById(R.id.rateLayout);
        LinearLayout mAboutmeLayout = view.findViewById(R.id.aboutmeLayout);
        LinearLayout mAboutappLayout = view.findViewById(R.id.aboutappLayout);
        LinearLayout mExpandLayout = view.findViewById(R.id.expandAppearanceLayout);
        TextView mAboutText = view.findViewById(R.id.aboutTextView);
        TextView mRateText = view.findViewById(R.id.rateTextView);
        TextView mAboutmeText = view.findViewById(R.id.aboutmeTextView);
        final TextView mAboutappText = view.findViewById(R.id.aboutappTextView);
        mExpandableLayout = view.findViewById(R.id.expandableAppearanceLayout);
        mDarkmodeSwitch = view.findViewById(R.id.darkmodeSwitch);
        mColorsSwitch = view.findViewById(R.id.colorSwitch);
        mNavbarSwitch = view.findViewById(R.id.navbarSwitch);
        mLabelsSwitch = view.findViewById(R.id.labelsSwitch);
        mSinglehandSwitch = view.findViewById(R.id.singlehandSwitch);
        mSwipeSwitch = view.findViewById(R.id.swipeSwitch);
        mSaveSwitch = view.findViewById(R.id.saveSwitch);
        ImageView mAppearanceSymbol = view.findViewById(R.id.appearanceSymbol);
        ImageView mDarkmodeSymbol = view.findViewById(R.id.darkmodeSymbol);
        ImageView mColorSymbol = view.findViewById(R.id.colorSymbol);
        ImageView mNavbarSymbol = view.findViewById(R.id.navbarSymbol);
        ImageView mLabelsSymbol = view.findViewById(R.id.labelsSymbol);
        ImageView mSinglehandSymbol = view.findViewById(R.id.singlehandSymbol);
        ImageView mSwipeSymbol = view.findViewById(R.id.swipeSymbol);
        ImageView mSaveSymbol = view.findViewById(R.id.saveSymbol);
        mExpandSymbol = view.findViewById(R.id.expandAppearanceSymbol);
        ImageView mAboutSymbol = view.findViewById(R.id.aboutSymbol);
        ImageView mRateSymbol = view.findViewById(R.id.rateSymbol);
        ImageView mAboutmeSymbol = view.findViewById(R.id.aboutmeSymbol);
        ImageView mAboutappSymbol = view.findViewById(R.id.aboutappSymbol);

        mSinglehandSpace.setVisibility(prefs.getBoolean("singlehand", true) ? View.VISIBLE : View.GONE);
        mDarkmodeSwitch.setChecked(prefs.getBoolean("darkmode", false));
        mColorsSwitch.setChecked(prefs.getBoolean("color", true));
        mNavbarSwitch.setChecked(prefs.getBoolean("nav", true));
        mLabelsSwitch.setChecked(prefs.getBoolean("labels", false));
        mSinglehandSwitch.setChecked(prefs.getBoolean("singlehand", true));
        mSwipeSwitch.setChecked(prefs.getBoolean("swipe", true));
        mSaveSwitch.setChecked(prefs.getBoolean("save", true));

        mNavigation = getActivity().findViewById(R.id.navigation);
        final ScrollView scrollView = view.findViewById(R.id.settingsScrollView);

        /* That's kind of annoying.
        Runnable run = new Runnable() {
            @Override
            public void run() {
                if (prefs.getBoolean("save", true) && prefs.getBoolean("appearanceExpanded", false)) {
                    mExpandableLayout.expand(0, null);
                    mExpandSymbol.setImageResource(R.drawable.avd_collapse);
                } else {
                    mExpandableLayout.collapse(0, null);
                }
            }
        };*/
        Runnable run2 = new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        };
        Handler mHandler = new Handler();
        //mHandler.postDelayed(run, 1);
        mHandler.postDelayed(run2, 2);


        //TODO: Remove when disabling swiping is available
        mSwipeLayout.setEnabled(false);
        mSwipeSymbol.setEnabled(false);
        mSwipeSwitch.setEnabled(false);

        if (prefs.getBoolean("advancedSettings", false)) {
            mNavbarLayout.setVisibility(View.VISIBLE);
            mSwipeLayout.setVisibility(View.VISIBLE);
            mSaveLayout.setVisibility(View.VISIBLE);
        }


        //Darkmode
        if (prefs.getBoolean("darkmode", false)) {
            int dark_lighter = getResources().getColor(R.color.background_dark_lighter);
            int dark_normal = getResources().getColor(R.color.background_dark_normal);
            int text_main = getResources().getColor(R.color.text_main_dark);
            int text_sub = getResources().getColor(R.color.text_sub_dark);

            try {
                dark_lighter = Color.parseColor(prefs.getString("custom_dark_lighter", getString((int) R.color.background_dark_lighter)));
                dark_normal = Color.parseColor(prefs.getString("custom_dark_normal", getString((int) R.color.background_dark_normal)));
                text_main = Color.parseColor(prefs.getString("custom_text_main_dark", getString((int) R.color.text_main_dark)));
                text_sub = Color.parseColor(prefs.getString("custom_text_sub_dark", getString((int) R.color.text_sub_dark)));
            } catch (IllegalArgumentException iae) {
                Toast.makeText(getActivity(), getString(R.string.fallback_colorparse), Toast.LENGTH_LONG).show();
                iae.printStackTrace();
            }

            ColorStateList icons_main = ColorStateList.valueOf(text_main);
            ColorStateList icons_sub = ColorStateList.valueOf(text_sub);

            mBackgroundLayout.setBackgroundColor(dark_lighter);
            mFunctionsCard.setBackgroundColor(dark_normal);
            mNdCard.setBackgroundColor(dark_normal);
            mAppearanceCard.setBackgroundColor(dark_normal);
            mAboutCard.setBackgroundColor(dark_normal);
            mNdSymbol.setImageTintList(icons_main);
            mNdText.setTextColor(text_sub);
            mNdStrengthunitSymbol.setImageTintList(icons_main);
            mNdStrengthunitText.setTextColor(text_sub);
            mAppearanceSymbol.setImageTintList(icons_main);
            mAppearanceText.setTextColor(text_main);
            mDarkmodeSymbol.setImageTintList(icons_sub);
            mDarkmodeSwitch.setTextColor(text_sub);
            mColorSymbol.setImageTintList(icons_sub);
            mColorsSwitch.setTextColor(text_sub);
            mNavbarSymbol.setImageTintList(icons_sub);
            mNavbarSwitch.setTextColor(text_sub);
            mLabelsSymbol.setImageTintList(icons_sub);
            mLabelsSwitch.setTextColor(text_sub);
            mSinglehandSymbol.setImageTintList(icons_sub);
            mSinglehandSwitch.setTextColor(text_sub);
            mSwipeSymbol.setImageTintList(icons_sub);
            mSwipeSwitch.setTextColor(text_sub);
            mSaveSymbol.setImageTintList(icons_sub);
            mSaveSwitch.setTextColor(text_sub);
            mExpandSymbol.setImageTintList(icons_sub);
            mAboutSymbol.setImageTintList(icons_main);
            mAboutText.setTextColor(text_main);
            mRateSymbol.setImageTintList(icons_sub);
            mRateText.setTextColor(text_sub);
            mAboutmeSymbol.setImageTintList(icons_sub);
            mAboutmeText.setTextColor(text_sub);
            mAboutappSymbol.setImageTintList(icons_sub);
            mAboutappText.setTextColor(text_sub);
        }


        mChooseCamLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                final AlertDialog.Builder dialog;
                if (prefs.getBoolean("darkmode", false)) {
                    dialog = new AlertDialog.Builder(getActivity(), R.style.darkDialog);
                } else {
                    dialog = new AlertDialog.Builder(getActivity());
                }
                dialog.setTitle(getString(R.string.settings_cam_choose))
                        .setNeutralButton(getString(R.string.offerHelp), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface mDialogInterface, int mI) {
                                //TODO: Help for sensor size
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface mDialogInterface, int mI) {

                            }
                        })
                        .setSingleChoiceItems(getResources().getStringArray(R.array.sensorFormats), prefs.getInt("chosenCamera", 0), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface mDialogInterface, int mChecked) {
                                prefsEdit.putInt("chosenCamera", mChecked);
                                prefsEdit.putFloat("coc", Float.parseFloat(getResources().getStringArray(R.array.sensorFormatValues)[mChecked])).apply();
                                new Handler().postDelayed(new Runnable() { //Post delayed so the user can see what he selected
                                    @Override
                                    public void run() {
                                        mDialogInterface.dismiss();
                                    }
                                }, 1);
                                Toast.makeText(getActivity(), String.valueOf(prefs.getFloat("coc", 0.0029f)), Toast.LENGTH_LONG).show();
                            }
                        })
                        .show();
            }
        });
        mCamCocLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {

            }
        });
        mNdStrengthunitLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                final AlertDialog.Builder dialog;
                if (prefs.getBoolean("darkmode", false)) {
                    dialog = new AlertDialog.Builder(getActivity(), R.style.darkDialog);
                } else {
                    dialog = new AlertDialog.Builder(getActivity());
                }
                dialog.setTitle(getString(R.string.settings_nd_strengthunit_dialog))
                        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface mDialogInterface, int mI) {

                            }
                        })
                        .setSingleChoiceItems(new CharSequence[]{getString(R.string.settings_nd_strengthunit_stops), getString(R.string.settings_nd_strengthunit_times)}, prefs.getBoolean("times_instead_stops", false) ? 1 : 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface mDialogInterface, int mChecked) {
                                prefsEdit.putBoolean("times_instead_stops", mChecked != 0).apply();
                                new Handler().postDelayed(new Runnable() { //Post delayed so the user can see what he selected
                                    @Override
                                    public void run() {
                                        mDialogInterface.dismiss();
                                    }
                                }, 100);
                            }
                        })
                        .show();
            }
        });
        mDarkmodeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                mDarkmodeSwitch.toggle();
            }
        });
        mDarkmodeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton mCompoundButton, boolean mB) {
                prefsEdit.putBoolean("darkmode", mDarkmodeSwitch.isChecked()).apply();
                //Snackbar.make(getActivity().findViewById(R.id.navigation), "Not ready yet", Snackbar.LENGTH_SHORT).show();
                try {
                    //new MainActivity().recreate();
                    new Handler().post(new Runnable() {

                        @Override
                        public void run() {
                            Intent intent = getActivity().getIntent();
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK
                                    | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            getActivity().overridePendingTransition(0, 0);
                            getActivity().finish();

                            getActivity().overridePendingTransition(0, 0);
                            startActivity(intent);
                        }
                    });
                } catch (NullPointerException npe) {
                    Snackbar.make(getActivity().findViewById(R.id.navigation), "ERROR.", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        mColorsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                mColorsSwitch.toggle();
            }
        });
        mColorsLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View mView) {
                startActivity(new Intent(getActivity(), CustomColorsActivity.class));
                return true;
            }
        });
        mColorsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton mCompoundButton, boolean mB) {
                prefsEdit.putBoolean("colors", mColorsSwitch.isChecked()).apply();
                mNavigation.setColored(mColorsSwitch.isChecked());
            }
        });
        mNavbarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                mNavbarSwitch.toggle();
            }
        });
        mNavbarSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton mCompoundButton, boolean mB) {
                prefsEdit.putBoolean("nav", mNavbarSwitch.isChecked()).apply();
                if (mNavbarSwitch.isChecked()) {
                    if (mNavigation.isHidden()) {
                        mNavigation.restoreBottomNavigation(true);
                    }
                } else {
                    if (mNavigation.isShown()) {
                        mNavigation.hideBottomNavigation(true);
                    }
                }
            }
        });
        mLabelsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                mLabelsSwitch.toggle();
            }
        });
        mLabelsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton mCompoundButton, boolean mB) {
                prefsEdit.putBoolean("labels", mLabelsSwitch.isChecked()).apply();
                mNavigation.setTitleState(mLabelsSwitch.isChecked() ? AHBottomNavigation.TitleState.ALWAYS_SHOW : AHBottomNavigation.TitleState.SHOW_WHEN_ACTIVE);
            }
        });
        mSinglehandLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                mSinglehandSwitch.toggle();
            }
        });
        mSinglehandSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton mCompoundButton, boolean mB) {
                prefsEdit.putBoolean("singlehand", mSinglehandSwitch.isChecked()).apply();
                mSinglehandSpace.setVisibility(mSinglehandSwitch.isChecked() ? View.VISIBLE : View.GONE);
            }
        });
        mSwipeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                mSwipeSwitch.toggle();
            }
        });
        mSwipeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton mCompoundButton, boolean mB) {
                prefsEdit.putBoolean("swipe", mSwipeSwitch.isChecked()).apply();
            }
        });
        mSaveLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                mSaveSwitch.toggle();
            }
        });
        mSaveSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton mCompoundButton, boolean mB) {
                prefsEdit.putBoolean("save", mSaveSwitch.isChecked()).apply();
            }
        });
        mExpandLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                mExpandableLayout.toggle();

                Drawable mExpandDrawable = mExpandSymbol.getDrawable();
                if (mExpandDrawable instanceof Animatable) {
                    ((Animatable) mExpandDrawable).start();

                    Runnable run = new Runnable() {
                        @Override
                        public void run() {
                            prefsEdit.putBoolean("appearanceExpanded", mExpandableLayout.isExpanded()).apply();

                            if (mExpandableLayout.isExpanded()) {
                                mExpandSymbol.setImageResource(R.drawable.avd_expand);
                            } else {
                                mExpandSymbol.setImageResource(R.drawable.avd_collapse);
                            }
                        }
                    };
                    Handler mHandler = new Handler();
                    mHandler.postDelayed(run, 300);
                }
            }
        });
        mRateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                final String appPackageName = getActivity().getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });
        mAboutmeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                startActivity(new Intent(getActivity(), FragmentActivity.class));
            }
        });
        mAboutappLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                //Intent i = new Intent(MainActivity.this, FragmentActivity.class);
                //startActivity(i);
                new LibsBuilder()
                        //provide a style (optional) (LIGHT, DARK, LIGHT_DARK_TOOLBAR)
                        .withActivityStyle(Libs.ActivityStyle.LIGHT)
                        //start the activity
                        .start(getActivity());
            }
        });

    }
}
