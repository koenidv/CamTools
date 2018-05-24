package com.koenidv.camtools;
//  Created by koenidv on 19.01.2018.

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class FocusFragment extends Fragment {

    int selectedButton = 0;

    //Called when Fragment should create its View object hierarchy
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        //Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_focus, parent, false);
    }

    //Called after onCreateView(). View setup here.
    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        @SuppressWarnings("ConstantConditions") final SharedPreferences prefs = getActivity().getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") final SharedPreferences.Editor prefsEditor = prefs.edit();

        final LinearLayout scrollLayout = view.findViewById(R.id.focusScrollLayout);
        final LinearLayout hyperLayout = view.findViewById(R.id.focusHyperLayout);
        final LinearLayout limitsLayout = view.findViewById(R.id.focusLimitsLayout);
        final EditText lengthEditText = view.findViewById(R.id.focusLengthEditText);
        final SeekBar lengthSeekbar = view.findViewById(R.id.focusLengthSeekbar);
        final EditText apertureEditText = view.findViewById(R.id.focusApertureEditText);
        final SeekBar apertureSeekbar = view.findViewById(R.id.focusApertureSeekbar);
        final CardView distanceCard = view.findViewById(R.id.focusDistanceCard);
        final EditText distanceEditText = view.findViewById(R.id.focusDistanceEditText);
        final SeekBar distanceSeekbar = view.findViewById(R.id.focusDistanceSeekbar);
        final Button selectHyperButton = view.findViewById(R.id.focusSelectHyperButton);
        final Button selectLimitsButton = view.findViewById(R.id.focusSelectLimitButton);
        final Button selectReverseButton = view.findViewById(R.id.focusSelectReverseButton);


        final Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new FastOutSlowInInterpolator());
        fadeIn.setDuration(300);

        final Animation waitFadeIn = new AlphaAnimation(0, 1);
        waitFadeIn.setInterpolator(new FastOutSlowInInterpolator());
        waitFadeIn.setDuration(300);
        waitFadeIn.setStartOffset(300);

        final Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new FastOutSlowInInterpolator());
        fadeOut.setDuration(300);

        final AnimationSet fadeOutIn = new AnimationSet(false);
        fadeOutIn.addAnimation(fadeOut);
        fadeOutIn.addAnimation(waitFadeIn);


        final boolean[] lengthDisable = {false};
        final boolean[] apertureDisable = {false};
        final boolean[] distanceDisable = {false};

        /*
         *  Listeners
         */

        lengthEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!lengthDisable[0] && s.length() > 0) {
                    lengthDisable[0] = true;
                    if (Build.VERSION.SDK_INT >= 24) {
                        lengthSeekbar.setProgress(Integer.parseInt(s.toString()) - 8, true);
                    } else {
                        lengthSeekbar.setProgress(Integer.parseInt(s.toString()) - 8);
                    }
                    lengthDisable[0] = false;
                }

                prefsEditor.putString("focusLength", s.toString()).apply();
                if (prefs.getString("focusMode", "hyper").equals("hyper")) {
                    calculateHyper(FocusFragment.this.getView());
                } else if (prefs.getString("focusMode", "hyper").equals("limits")) {
                    calculateLimits(FocusFragment.this.getView());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        lengthSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!lengthDisable[0]) {
                    lengthDisable[0] = true;
                    lengthEditText.setText(String.valueOf(progress + 8));
                    lengthDisable[0] = false;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        apertureEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!apertureDisable[0] && s.length() > 0) {
                    apertureDisable[0] = true;
                    if (Build.VERSION.SDK_INT >= 24) {
                        apertureSeekbar.setProgress(apertureToSeekbar(s), true);
                    } else {
                        apertureSeekbar.setProgress(apertureToSeekbar(s));
                    }
                    apertureDisable[0] = false;
                }

                prefsEditor.putString("focusAperture", s.toString()).apply();
                if (prefs.getString("focusMode", "hyper").equals("hyper")) {
                    calculateHyper(FocusFragment.this.getView());
                } else if (prefs.getString("focusMode", "hyper").equals("limits")) {
                    calculateLimits(FocusFragment.this.getView());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        apertureSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!apertureDisable[0]) {
                    apertureDisable[0] = true;
                    apertureEditText.setText(seekbarToAperture(progress));
                    apertureDisable[0] = false;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        distanceEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!distanceDisable[0] && s.length() > 0) {
                    distanceDisable[0] = true;
                    if (Build.VERSION.SDK_INT >= 24) {
                        distanceSeekbar.setProgress(Math.round(Float.valueOf(s.toString())), true);
                    } else {
                        distanceSeekbar.setProgress(Integer.parseInt(s.toString()));
                    }
                    distanceDisable[0] = false;
                }

                prefsEditor.putString("focusDistance", s.toString()).apply();
                if (prefs.getString("focusMode", "hyper").equals("hyper")) {
                    calculateHyper(FocusFragment.this.getView());
                } else if (prefs.getString("focusMode", "hyper").equals("limits")) {
                    calculateLimits(FocusFragment.this.getView());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        distanceSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!distanceDisable[0]) {
                    distanceDisable[0] = true;
                    if (progress <= 22) {
                        //noinspection UnnecessaryBoxing
                        distanceEditText.setText(String.valueOf(Double.valueOf(progress * progress) / 100));
                    } else {
                        distanceEditText.setText(String.valueOf(progress * progress / 100));
                    }
                    distanceDisable[0] = false;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        selectHyperButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefsEditor.putString("focusMode", "hyper").apply();

                if (selectedButton == 1) {
                    limitsLayout.startAnimation(fadeOut);
                }
                hyperLayout.startAnimation(fadeIn);
                scrollLayout.startAnimation(fadeIn);
                limitsLayout.setVisibility(View.GONE);
                hyperLayout.setVisibility(View.VISIBLE);
                distanceCard.setVisibility(View.GONE);
                scrollLayout.setPadding(0, 800, 0, 60);
                calculateHyper(FocusFragment.this.getView());

                if (selectedButton == 1|| selectedButton == 2) {
                    animateButtonBackgroundColor(selectHyperButton, Color.WHITE, getResources().getColor(R.color.tab_focus), 300);
                    animateButtonTextColor(selectHyperButton, getResources().getColor(R.color.text_sub_light), Color.WHITE, 300);
                }
                if (selectedButton == 1) {
                    animateButtonBackgroundColor(selectLimitsButton, getResources().getColor(R.color.tab_focus), Color.WHITE, 100);
                    animateButtonTextColor(selectLimitsButton, Color.WHITE, getResources().getColor(R.color.text_sub_light), 100);
                } else if (selectedButton == 2) {
                    animateButtonBackgroundColor(selectReverseButton, getResources().getColor(R.color.tab_focus), Color.WHITE, 100);
                    animateButtonTextColor(selectReverseButton, Color.WHITE, getResources().getColor(R.color.text_sub_light), 100);
                }

                selectedButton = 0;

            }
        });
        selectLimitsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefsEditor.putString("focusMode", "limits").apply();

                if (selectedButton == 0) {
                    hyperLayout.startAnimation(fadeOut);
                }
                limitsLayout.startAnimation(fadeIn);
                scrollLayout.startAnimation(fadeIn);
                hyperLayout.setVisibility(View.GONE);
                limitsLayout.setVisibility(View.VISIBLE);
                distanceCard.setVisibility(View.VISIBLE);
                scrollLayout.setPadding(0, 512, 0, 60);
                calculateLimits(FocusFragment.this.getView());

                if (selectedButton == 0 || selectedButton == 2) {
                    animateButtonBackgroundColor(selectLimitsButton, Color.WHITE, getResources().getColor(R.color.tab_focus), 300);
                    animateButtonTextColor(selectLimitsButton, getResources().getColor(R.color.text_sub_light), Color.WHITE, 300);
                }
                if (selectedButton == 0) {
                    animateButtonBackgroundColor(selectHyperButton, getResources().getColor(R.color.tab_focus), Color.WHITE, 100);
                    animateButtonTextColor(selectHyperButton, Color.WHITE, getResources().getColor(R.color.text_sub_light), 100);
                } else if (selectedButton == 2) {
                    animateButtonBackgroundColor(selectReverseButton, getResources().getColor(R.color.tab_focus), Color.WHITE, 100);
                    animateButtonTextColor(selectReverseButton, Color.WHITE, getResources().getColor(R.color.text_sub_light), 100);
                }

                selectedButton = 1;
            }
        });
        selectReverseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefsEditor.putString("focusMode", "reverse").apply();

                scrollLayout.startAnimation(fadeIn);
                hyperLayout.setVisibility(View.GONE);
                limitsLayout.setVisibility(View.GONE);
                distanceCard.setVisibility(View.GONE);
                scrollLayout.setPadding(0, 60, 0, 260);

                if (selectedButton == 0 || selectedButton == 1) {
                    animateButtonBackgroundColor(selectReverseButton, Color.WHITE, getResources().getColor(R.color.tab_focus), 300);
                    animateButtonTextColor(selectReverseButton, getResources().getColor(R.color.text_sub_light), Color.WHITE, 300);
                }
                if (selectedButton == 0) {
                    animateButtonBackgroundColor(selectHyperButton, getResources().getColor(R.color.tab_focus), Color.WHITE, 100);
                    animateButtonTextColor(selectHyperButton, Color.WHITE, getResources().getColor(R.color.text_sub_light), 100);
                } else if (selectedButton == 1) {
                    animateButtonBackgroundColor(selectLimitsButton, getResources().getColor(R.color.tab_focus), Color.WHITE, 100);
                    animateButtonTextColor(selectLimitsButton, Color.WHITE, getResources().getColor(R.color.text_sub_light), 100);
                }

                selectedButton = 2;
            }
        });


        /*
         *  Setup
         */

        lengthEditText.setText(prefs.getString("focusLength", "24"));
        apertureEditText.setText(prefs.getString("focusAperture", "3.5"));
        distanceEditText.setText(prefs.getString("focusDistance", "4"));
        if (prefs.getString("focusMode", "hyper").equals("hyper")) {
            selectHyperButton.callOnClick();
        } else if (prefs.getString("focusMode", "hyper").equals("limits")) {
            selectLimitsButton.callOnClick();
        } else if (prefs.getString("focusMode", "hyper").equals("reverse")) {
            selectReverseButton.callOnClick();
        }
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void calculateHyper(View view) {
        @SuppressWarnings("ConstantConditions") final SharedPreferences prefs = getActivity().getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);

        TextView nearestTextView = view.findViewById(R.id.focusHyperNearestTextView);
        TextView hyperTextView = view.findViewById(R.id.focusHyperHyperTextView);
        TextView furthestTextView = view.findViewById(R.id.focusHyperFurthestTextView);
        TextView nearestIndicatorTextView = view.findViewById(R.id.focusHyperNearestIndicatorTextView);
        TextView furthestIndicatorTextView = view.findViewById(R.id.focusHyperFurthestIndicatorTextView);
        final EditText lengthEditText = view.findViewById(R.id.focusLengthEditText);
        final EditText apertureEditText = view.findViewById(R.id.focusApertureEditText);

        if (lengthEditText.getText().toString().equals("")
                || apertureEditText.getText().toString().equals("")) {
            return;
        }

        float length = Float.valueOf(lengthEditText.getText().toString());
        float aperture = Float.valueOf(apertureEditText.getText().toString());
        float coc = prefs.getFloat("coc", 0.0029f);

        float hyper = (((length * length) / (aperture * coc)) + length);
        float nearest = (hyper * hyper) / (hyper + (hyper - length));
        float furthest = (hyper * hyper) / (hyper - (hyper - length));

        Spanned hyperSpanned = Html.fromHtml(String.format("%.2f", hyper / 1000) + "<small>" + getString(R.string.focus_distance_indicator) + "</small>");
        Spanned nearestSpanned = Html.fromHtml(String.format("%.2f", nearest / 1000) + "<small>" + getString(R.string.focus_distance_indicator) + "</small>");
        Spanned furthestSpanned = Html.fromHtml(String.format("%.2f", furthest / 1000) + "<small>" + getString(R.string.focus_distance_indicator) + "</small>");

        if (hyper == Double.POSITIVE_INFINITY || String.valueOf(hyper).equals("NaN")) {
            hyperSpanned = Html.fromHtml("∞");
        }
        if (furthest < 0) {
            furthestSpanned = Html.fromHtml("∞");
        }

        nearestTextView.setText(nearestSpanned);
        hyperTextView.setText(hyperSpanned);
        furthestTextView.setText(furthestSpanned);

        if (nearest == 0 || String.valueOf(nearest).equals("NaN")) {
            nearestTextView.setVisibility(View.INVISIBLE);
            nearestIndicatorTextView.setVisibility(View.INVISIBLE);
        } else {
            nearestTextView.setVisibility(View.VISIBLE);
            nearestIndicatorTextView.setVisibility(View.VISIBLE);
        }
        if (furthest == 0 || String.valueOf(furthest).equals("NaN")) {
            furthestTextView.setVisibility(View.INVISIBLE);
            furthestIndicatorTextView.setVisibility(View.INVISIBLE);
        } else {
            furthestTextView.setVisibility(View.VISIBLE);
            furthestIndicatorTextView.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void calculateLimits(View view) {
        @SuppressWarnings("ConstantConditions") final SharedPreferences prefs = getActivity().getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);

        TextView nearTextView = view.findViewById(R.id.focusLimitsNearTextView);
        TextView farTextView = view.findViewById(R.id.focusLimitsFarTextView);
        TextView nearIndicatorTextView = view.findViewById(R.id.focusLimitsNearIndicatorTextView);
        TextView farIndicatorTextView = view.findViewById(R.id.focusLimitsFarIndicatorTextView);
        TextView depthTextView = view.findViewById(R.id.focusLimitsDepthTextView);
        final EditText lengthEditText = view.findViewById(R.id.focusLengthEditText);
        final EditText apertureEditText = view.findViewById(R.id.focusApertureEditText);
        final EditText distanceEditText = view.findViewById(R.id.focusDistanceEditText);

        if (lengthEditText.getText().toString().equals("")
                || apertureEditText.getText().toString().equals("")) {
            return;
        }

        float length = Float.valueOf(lengthEditText.getText().toString());
        float aperture = Float.valueOf(apertureEditText.getText().toString());
        float coc = prefs.getFloat("coc", 0.0029f);
        float distance = 0;
        try {
            distance = Float.valueOf(distanceEditText.getText().toString()) * 1000;
        } catch (NumberFormatException nfe) {
            //
        }

        float hyper = (((length * length) / (aperture * coc)) + length);
        float nearest = (hyper * distance) / (hyper + length);
        float furthest = (hyper * distance) / (hyper - length);
        float depth = furthest - nearest;

        Spanned nearestSpanned = Html.fromHtml(String.format("%.2f", nearest / 1000) + "<small>" + getString(R.string.focus_distance_indicator) + "</small>");
        Spanned furthestSpanned = Html.fromHtml(String.format("%.2f", furthest / 1000) + "<small>" + getString(R.string.focus_distance_indicator) + "</small>");
        Spanned depthSpanned = Html.fromHtml(getString(R.string.focus_limits_depth_indicator) + " <b>" + String.format("%.2f", depth / 1000) + "</b>");

        if (furthest < 0) {
            furthestSpanned = Html.fromHtml("∞");
        }
        if (depth < 0) {
            depthSpanned = Html.fromHtml(getString(R.string.focus_limits_depth_indicator) + " <b>∞</b>");
        }

        nearTextView.setText(nearestSpanned);
        farTextView.setText(furthestSpanned);
        depthTextView.setText(depthSpanned);

        if (nearest == 0 || String.valueOf(nearest).equals("NaN")) {
            nearTextView.setVisibility(View.INVISIBLE);
            nearIndicatorTextView.setVisibility(View.INVISIBLE);
        } else {
            nearTextView.setVisibility(View.VISIBLE);
            nearIndicatorTextView.setVisibility(View.VISIBLE);
        }
        if (furthest == 0 || String.valueOf(furthest).equals("NaN")) {
            farTextView.setVisibility(View.INVISIBLE);
            farIndicatorTextView.setVisibility(View.INVISIBLE);
        } else {
            farTextView.setVisibility(View.VISIBLE);
            farIndicatorTextView.setVisibility(View.VISIBLE);
        }
    }

    private String seekbarToAperture(int seekbarProgress) {
        String[] apertures = getResources().getStringArray(R.array.aperture_thirds);

        if (seekbarProgress > apertures.length) {
            return "";
        }

        return apertures[seekbarProgress];
    }

    private int apertureToSeekbar(CharSequence apertureString) {
        int seekbarProgress = 0;
        double aperture = Double.valueOf(apertureString.toString());
        String[] apertures = getResources().getStringArray(R.array.aperture_thirds);

        while (seekbarProgress < apertures.length && aperture > Double.valueOf(apertures[seekbarProgress])) {
            seekbarProgress++;
        }

        return seekbarProgress;
    }

    private void animateButtonBackgroundColor(final View view, int startColor, int endColor, int duration) {
        ValueAnimator colorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), startColor, endColor);
        colorAnimator.setDuration(duration); // milliseconds
        colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                view.setBackgroundColor((int) animator.getAnimatedValue());
            }

        });
        colorAnimator.start();
    }
    private void animateButtonTextColor(final Button view, int startColor, int endColor, int duration) {
        ValueAnimator colorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), startColor, endColor);
        colorAnimator.setDuration(duration);
        colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                view.setTextColor((int) animation.getAnimatedValue());
            }
        });
        colorAnimator.start();
    }


}
