package com.koenidv.camtools;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

public class CalculateLimitsFragment extends Fragment {


    public CalculateLimitsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calculate_limits, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        @SuppressWarnings("ConstantConditions") final SharedPreferences prefs = getActivity().getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") final SharedPreferences.Editor prefsEditor = prefs.edit();


        final EditText lengthEditText = view.findViewById(R.id.focusLengthEditText);
        final SeekBar lengthSeekbar = view.findViewById(R.id.focusLengthSeekbar);
        final CardView apertureCard = view.findViewById(R.id.focusApertureCard);
        final EditText apertureEditText = view.findViewById(R.id.focusApertureEditText);
        final SeekBar apertureSeekbar = view.findViewById(R.id.focusApertureSeekbar);
        final CardView distanceCard = view.findViewById(R.id.focusDistanceCard);
        final EditText distanceEditText = view.findViewById(R.id.focusDistanceEditText);
        final SeekBar distanceSeekbar = view.findViewById(R.id.focusDistanceSeekbar);

        lengthEditText.setText("THIS IS A TEST", TextView.BufferType.EDITABLE);

        final boolean[] lengthDisable = {false};
        final boolean[] apertureDisable = {false};
        final boolean[] distanceDisable = {false};

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
                calculateLimits(CalculateLimitsFragment.this.getView());
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
        //apertureEditText.addTextChangedListener(new TextWatcher() {
        //    @Override
        //    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //    }
//
        //    @Override
        //    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //        if (!apertureDisable[0] && s.length() > 0) {
        //            apertureDisable[0] = true;
        //            if (Build.VERSION.SDK_INT >= 24) {
        //                apertureSeekbar.setProgress(apertureToSeekbar(s), true);
        //            } else {
        //                apertureSeekbar.setProgress(apertureToSeekbar(s));
        //            }
        //            apertureDisable[0] = false;
        //        }
//
        //        prefsEditor.putString("focusAperture", s.toString()).apply();
        //        if (prefs.getString("focusMode", "hyper").equals("hyper")) {
        //            calculateHyper(FocusFragment.this.getView());
        //        } else if (prefs.getString("focusMode", "hyper").equals("limits")) {
        //            calculateLimits(FocusFragment.this.getView());
        //        }
        //    }
//
        //    @Override
        //    public void afterTextChanged(Editable s) {
        //    }
        //});
        //apertureSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
        //    @Override
        //    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        //        if (!apertureDisable[0]) {
        //            apertureDisable[0] = true;
        //            apertureEditText.setText(seekbarToAperture(progress));
        //            apertureDisable[0] = false;
        //        }
        //    }
//
        //    @Override
        //    public void onStartTrackingTouch(SeekBar seekBar) {
        //    }
//
        //    @Override
        //    public void onStopTrackingTouch(SeekBar seekBar) {
        //    }
        //});
        //distanceEditText.addTextChangedListener(new TextWatcher() {
        //    @Override
        //    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //    }
//
        //    @Override
        //    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //        if (!distanceDisable[0] && s.length() > 0) {
        //            distanceDisable[0] = true;
        //            if (Build.VERSION.SDK_INT >= 24) {
        //                distanceSeekbar.setProgress(Math.round(Float.valueOf(s.toString())), true);
        //            } else {
        //                distanceSeekbar.setProgress(Integer.parseInt(s.toString()));
        //            }
        //            distanceDisable[0] = false;
        //        }
//
        //        prefsEditor.putString("focusDistance", s.toString()).apply();
        //        if (prefs.getString("focusMode", "hyper").equals("hyper")) {
        //            calculateHyper(FocusFragment.this.getView());
        //        } else if (prefs.getString("focusMode", "hyper").equals("limits")) {
        //            calculateLimits(FocusFragment.this.getView());
        //        }
        //    }
//
        //    @Override
        //    public void afterTextChanged(Editable s) {
        //    }
        //});
        //distanceSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
        //    @Override
        //    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        //        if (!distanceDisable[0]) {
        //            distanceDisable[0] = true;
        //            if (progress <= 22) {
        //                //noinspection UnnecessaryBoxing
        //                distanceEditText.setText(String.valueOf(Double.valueOf(progress * progress) / 100));
        //            } else {
        //                distanceEditText.setText(String.valueOf(progress * progress / 100));
        //            }
        //            distanceDisable[0] = false;
        //        }
        //    }
//
        //    @Override
        //    public void onStartTrackingTouch(SeekBar seekBar) {
        //    }
//
        //    @Override
        //    public void onStopTrackingTouch(SeekBar seekBar) {
        //    }
        //});
    }

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

        float hyper = length * length / (aperture * coc);
        float nearest = hyper * distance / (hyper + distance - length);
        float furthest = hyper * distance / (hyper - distance - length);
        float depth = furthest - nearest;

        Spanned nearestSpanned = Html.fromHtml(String.format("%.2f", nearest / 1000) + "<small>" + getString(R.string.meter) + "</small>");
        Spanned furthestSpanned = Html.fromHtml(String.format("%.2f", furthest / 1000) + "<small>" + getString(R.string.meter) + "</small>");
        Spanned depthSpanned = Html.fromHtml(getString(R.string.focus_depth_indicator) + " <b>" + String.format("%.2f", depth / 1000) + "</b>");

        if (furthest < 0) {
            furthestSpanned = Html.fromHtml("∞");
        }
        if (depth < 0) {
            depthSpanned = Html.fromHtml(getString(R.string.focus_depth_indicator) + " <b>∞</b>");
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

}
