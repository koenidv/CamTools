package com.koenidv.camtools;
//  Created by koenidv on 19.01.2018.

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

public class FocusFragment extends Fragment {

    //Called when Fragment should create its View object hierarchy
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        //Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_focus, parent, false);
    }

    //Called after onCreateView(). View setup here.
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        @SuppressWarnings("ConstantConditions") final SharedPreferences prefs = getActivity().getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") final SharedPreferences.Editor prefsEditor = prefs.edit();

        TextView NearestTextView = view.findViewById(R.id.focusNearestTextView);
        TextView HyperTextView = view.findViewById(R.id.focusHyperTextView);
        TextView FurthestTextView = view.findViewById(R.id.focusFurthestTextView);
        final EditText lengthEditText = view.findViewById(R.id.focusLengthEditText);
        final SeekBar lengthSeekbar = view.findViewById(R.id.focusLengthSeekbar);
        final EditText apertureEditText = view.findViewById(R.id.focusApertureEditText);
        final SeekBar apertureSeekbar = view.findViewById(R.id.focusApertureSeekbar);
        final EditText distanceEditText = view.findViewById(R.id.focusDistanceEditText);
        final SeekBar distanceSeekbar = view.findViewById(R.id.focusDistanceSeekbar);

        final boolean[] lengthDisable = {false};
        final boolean[] apertureDisable = {false};
        final boolean[] distanceDisable = {false};

        lengthEditText.setText(prefs.getString("focusLength", "24"));
        apertureEditText.setText(prefs.getString("focusAperture", "3.5"));
        distanceEditText.setText(prefs.getString("focusDistance", "4"));


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
                calculate(FocusFragment.this.getView());
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
                        distanceSeekbar.setProgress(Integer.parseInt(s.toString()) - 8, true);
                    } else {
                        distanceSeekbar.setProgress(Integer.parseInt(s.toString()) - 8);
                    }
                    distanceDisable[0] = false;
                }

                prefsEditor.putString("focusDistance", s.toString()).apply();
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
    }

    private void calculate(View view) {
        @SuppressWarnings("ConstantConditions") final SharedPreferences prefs = getActivity().getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);

        TextView NearestTextView = view.findViewById(R.id.focusNearestTextView);
        TextView HyperTextView = view.findViewById(R.id.focusHyperTextView);
        TextView FurthestTextView = view.findViewById(R.id.focusFurthestTextView);
        final EditText lengthEditText = view.findViewById(R.id.focusLengthEditText);
        final EditText apertureEditText = view.findViewById(R.id.focusApertureEditText);
        final EditText distanceEditText = view.findViewById(R.id.focusDistanceEditText);

        float length = Float.valueOf(lengthEditText.getText().toString());
        float aperture = Float.valueOf(apertureEditText.getText().toString());
        float coc = prefs.getFloat("coc", 0.0029f);
        float distance = Float.valueOf(distanceEditText.getText().toString());

        float nearest = 0;
        float hyper = (length * length) / (aperture * coc) + length;
        float furthest = 0;

        NearestTextView.setText(String.valueOf(nearest));
        HyperTextView.setText(String.valueOf(hyper));
        FurthestTextView.setText(String.valueOf(furthest));
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


}
