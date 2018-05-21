package com.koenidv.camtools;
//  Created by koenidv on 19.01.2018.

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;


public class NDFragment extends Fragment {

    //Called when Fragment should create its View object hierarchy
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        //Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_nd, parent, false);
    }


    ConstraintLayout mBackgroundLayout;
    CardView mTimeCard;
    TextView mTimeTitleText;
    EditText mTimeEditText;
    TextView mTimeIndicatorText;
    SeekBar mTimeSeekbar;
    CardView mStrengthCard;
    TextView mStrengthTitleText;
    EditText mStrengthEditText;
    TextView mStrengthIndicatorText;
    SeekBar mStrengthSeekbar;
    TextView mResultTextView;

    boolean noUpdate = false;

    //Called after onCreateView(). View setup here.
    @SuppressWarnings("RedundantCast")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        @SuppressWarnings("ConstantConditions") final SharedPreferences prefs = getActivity().getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") final SharedPreferences.Editor prefsEditor = prefs.edit();

        mBackgroundLayout = view.findViewById(R.id.ndLayout);
        mTimeCard = view.findViewById(R.id.focusLengthCard);
        mTimeSeekbar = view.findViewById(R.id.ndTimeSeekbar);
        mTimeTitleText = view.findViewById(R.id.ndTimeTitleText);
        mTimeEditText = view.findViewById(R.id.ndTimeEditText);
        mTimeIndicatorText = view.findViewById(R.id.ndTimeIndicatorText);
        mStrengthCard = view.findViewById(R.id.ndStrengthCard);
        mStrengthTitleText = view.findViewById(R.id.ndStrengthTitle);
        mStrengthEditText = view.findViewById(R.id.ndStrengthEditText);
        mStrengthIndicatorText = view.findViewById(R.id.ndStrengthIndicatorText);
        mStrengthSeekbar = view.findViewById(R.id.ndStrengthSeekbar);
        mResultTextView = view.findViewById(R.id.ndResultTextView);


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

            mBackgroundLayout.setBackgroundColor(dark_lighter);
            mTimeCard.setBackgroundColor(dark_normal);
            mTimeTitleText.setTextColor(text_main);
            mTimeEditText.setTextColor(text_main);
            mTimeEditText.setHintTextColor(text_sub);
            mTimeIndicatorText.setTextColor(text_main);
            mStrengthCard.setBackgroundColor(dark_normal);
            mStrengthTitleText.setTextColor(text_main);
            mStrengthEditText.setTextColor(text_main);
            mStrengthEditText.setHintTextColor(text_sub);
            mStrengthIndicatorText.setTextColor(text_main);
            mResultTextView.setTextColor(text_main);
        }

        noUpdate = false;
        mTimeEditText.setText(prefs.getString("ndTime", "10"));
        if (!mTimeEditText.getText().toString().equals("")) {
            mTimeSeekbar.setProgress(secondsToSeekbar(Double.parseDouble(mTimeEditText.getText().toString())));
        }
        mStrengthEditText.setText(prefs.getString("ndStrength", "20"));
        if (!mStrengthEditText.getText().toString().equals("")) {
            mStrengthSeekbar.setProgress(strengthToSeekbar(Double.parseDouble(mStrengthEditText.getText().toString()), prefs.getBoolean("times_instead_stops", false)));
        }
        if (prefs.getBoolean("times_instead_stops", false)) {
            mStrengthIndicatorText.setText(getString(R.string.nd_strength_type_times));
        }
        if (!mStrengthEditText.getText().toString().equals("") && !mTimeEditText.getText().toString().equals("")) {
            String calculatedTime = calculateTime(prefs.getString("ndTime", "10"), prefs.getString("ndStrength", "20"), prefs.getBoolean("times_instead_stops", false), prefs.getInt("decimalPlaces", 6));
            mResultTextView.setText(rearrangeTime(calculatedTime));
        } else {
            mResultTextView.setText("");
        }


        mTimeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar mSeekBar, int mSeekbarValue, boolean mB) {
                String sts = seekbarToSeconds(mSeekbarValue);
                if (!noUpdate) {
                    noUpdate = true;
                    mTimeEditText.setText(sts);
                    noUpdate = false;
                }
                prefsEditor.putString("ndTime", sts).apply();
                String calculatedTime = calculateTime(sts, prefs.getString("ndStrength", "4"), prefs.getBoolean("times_instead_stops", false), prefs.getInt("decimalPlaces", 6));
                mResultTextView.setText(rearrangeTime(calculatedTime));
            }

            //@formatter:off
            @Override public void onStartTrackingTouch(SeekBar mSeekBar) {}
            @Override public void onStopTrackingTouch(SeekBar mSeekBar) {}
            //@formatter:on
        });
        mTimeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence mCharSequence, int mI, int mI1, int mI2) {
                if (!noUpdate) {
                    noUpdate = true;
                    if (!mTimeEditText.getText().toString().equals("")) {
                        mTimeSeekbar.setProgress(secondsToSeekbar(Double.parseDouble(mTimeEditText.getText().toString())));
                    }
                    noUpdate = false;
                }
            }

            //@formatter:off
            @Override public void beforeTextChanged(CharSequence mCharSequence, int mI, int mI1, int mI2) {}
            @Override public void afterTextChanged(Editable mEditable) {}
            //@formatter:on
        });

        mStrengthSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar mSeekBar, int mSeekbarValue, boolean mB) {
                String sts = seekbarToStrength(mSeekbarValue, prefs.getBoolean("times_instead_stops", false));
                if (!noUpdate) {
                    noUpdate = true;
                    mStrengthEditText.setText(sts);
                    noUpdate = false;
                }
                prefsEditor.putString("ndStrength", sts).apply();
                String calculatedTime = calculateTime(sts, prefs.getString("ndStrength", "4"), prefs.getBoolean("times_instead_stops", false), prefs.getInt("decimalPlaces", 6));
                mResultTextView.setText(rearrangeTime(calculatedTime));
            }

            //@formatter:off
            @Override public void onStartTrackingTouch(SeekBar mSeekBar) {}
            @Override public void onStopTrackingTouch(SeekBar mSeekBar) {}
            //@formatter:on
        });
        mStrengthEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence mCharSequence, int mI, int mI1, int mI2) {
                if (!noUpdate) {
                    noUpdate = true;
                    if (!mStrengthEditText.getText().toString().equals("")) {
                        mStrengthSeekbar.setProgress(strengthToSeekbar(Double.parseDouble(mStrengthEditText.getText().toString()), prefs.getBoolean("times_instead_stops", false)));
                    }
                    noUpdate = false;
                }
            }

            //@formatter:off
            @Override public void beforeTextChanged(CharSequence mCharSequence, int mI, int mI1, int mI2) {}
            @Override public void afterTextChanged(Editable mEditable) {}
            //@formatter:on
        });
    }

    private String calculateTime(String mTime, String mStrength, boolean timesInsteadStops, int decimalPlaces) {
        String outSecondsRaw;

        if (!timesInsteadStops) {
            mStrength = String.valueOf(1 / Math.pow(0.5, Double.parseDouble(mStrength)));
        }

        Double time = Double.parseDouble(mTime);
        Double strength = Double.parseDouble(mStrength);
        outSecondsRaw = String.valueOf(time * strength);

        if (Double.parseDouble(outSecondsRaw) > 4) {
            outSecondsRaw = String.valueOf(Math.round(Double.parseDouble(outSecondsRaw)));
        } else if (outSecondsRaw.length() > 8) {
            outSecondsRaw = String.format("%." + String.valueOf(decimalPlaces) + "f", Double.parseDouble(outSecondsRaw));
        }
        outSecondsRaw = outSecondsRaw.replace(",", ".");
        while ((outSecondsRaw.contains(".") && outSecondsRaw.endsWith("0")) || outSecondsRaw.endsWith(".")) {
            outSecondsRaw = outSecondsRaw.substring(0, outSecondsRaw.length() - 1);
        }

        return outSecondsRaw;
    }

    private String rearrangeTime(String mTime) {
        Double in = Double.parseDouble(mTime);
        int weeks = (int) (in - (in % 604800)) / 604800;
        in = in - (weeks * 604800);
        int days = (int) (in - (in % 86400)) / 86400;
        in = in - (days * 86400);
        int hours = (int) (in - (in % 3600)) / 3600;
        in = in - (hours * 3600);
        int minutes = (int) (in - (in % 60)) / 60;
        in = in - (minutes * 60);
        String seconds = String.valueOf(in);

        if (days >= 7) { //If the numbers is too big, the weeks aren't calculated exactly sometimes
            int addWeeks = (days - (days % 7)) / 7;
            days = days - (addWeeks * 7);
            weeks = weeks + addWeeks;
        }
        while ((seconds.contains(".") && seconds.endsWith("0")) || seconds.endsWith(".")) { //>.0< isn't nescesarry. >.4<, for example is.
            seconds = seconds.substring(0, seconds.length() - 1);
        }


        StringBuilder out = new StringBuilder();

        if (weeks != 0) {
            out.append(String.valueOf(weeks))
                    .append("w");
        }
        if (days != 0) {
            if (weeks != 0) {
                out.append(", ");
            }
            out.append(String.valueOf(days))
                    .append("d");
        }
        if (hours != 0) {
            if (days != 0) {
                out.append(", ");
            }
            out.append(String.valueOf(hours))
                    .append("h");
        }
        if (minutes != 0) {
            if (hours != 0) {
                out.append(", ");
            }
            out.append(String.valueOf(minutes))
                    .append("min");
        }
        if (!Objects.equals(seconds, "0")) {
            if (minutes != 0) {
                out.append(", ");
            }
            out.append(seconds)
                    .append("s");
        }

        return out.toString();
    }

    private String seekbarToSeconds(int SeekBarValue) {
        double out;
        int l;
        //@formatter:off
        switch (SeekBarValue) {
            case 0: out = 0.00025; l= 5;break;
            case 1: out = 0.0003125; l = 7;break;
            case 2: out = 0.0004; l = 4;break;
            case 3: out = 0.0005; l = 4;break;
            case 4: out = 0.000625; l = 6;break;
            case 5: out = 0.0008; l = 4;break;
            case 6: out = 0.001; l = 3;break;
            case 7: out = 0.00125; l = 5;break;
            case 8: out = 0.0015625; l = 7;break;
            case 9: out = 0.002; l = 3;break;
            case 10: out = 0.0025; l = 4;break;
            case 11: out = 0.003125; l = 6;break;
            case 12: out = 0.004; l = 3;break;
            case 13: out = 0.005; l = 3;break;
            case 14: out = 0.00625; l = 5;break;
            case 15: out = 0.008; l = 3;break;
            case 16: out = 0.01; l = 2;break;
            case 17: out = 0.0125; l = 4;break;
            case 18: out = 0.016; l = 3;break;
            case 19: out = 0.02; l = 2;break;
            case 20: out = 0.025; l = 3;break;
            case 21: out = 0.03; l = 2;break;
            case 22: out = 0.04; l = 2;break;
            case 23: out = 0.05; l = 2;break;
            case 24: out = 0.06; l = 2;break;
            case 25: out = 0.07; l = 2;break;
            case 26: out = 0.1; l = 1;break;
            case 27: out = 0.125; l = 3;break;
            case 28: out = 0.16; l = 2;break;
            case 29: out = 0.2; l = 1;break;
            case 30: out = 0.25; l = 2;break;
            case 31: out = 0.3; l = 1;break;
            case 32: out = 0.4; l = 1;break;
            case 33: out = 0.5; l = 1;break;
            case 34: out = 0.6; l = 1;break;
            case 35: out = 0.8; l = 1;break;
            case 36: out = 1; l = 0;break;
            case 37: out = 1.3; l = 1;break;
            case 38: out = 1.6; l = 1;break;
            case 39: out = 2; l = 0;break;
            case 40: out = 2.5; l = 1;break;
            case 41: out = 3.2; l = 1;break;
            case 42: out = 4; l = 0;break;
            case 43: out = 5; l = 0;break;
            case 44: out = 6; l = 0;break;
            case 45: out = 8; l = 0;break;
            case 46: out = 10; l = 0;break;
            case 47: out = 13; l = 0;break;
            case 48: out = 15; l = 0;break;
            case 49: out = 20; l = 0;break;
            case 50: out = 25; l = 0;break;
            case 51: out = 30; l = 0;break;
            default: out = 0.01; l = 2;
        }
        //@formatter:on

        return String.format("%." + String.valueOf(l) + "f", out).replace(",", ".");
    }

    private int secondsToSeekbar(Double seconds) {
        //@formatter:off
        if (seconds < 0.0003) {
            return 0;
        } else if (seconds < 0.00038) {
            return 1;
        } else if (seconds < 0.00046) {
            return 2;
        } else if (seconds < 0.00058) {
            return 3;
        } else if (seconds < 0.00072) {
            return 4;
        } else if (seconds < 0.0009) {
            return 5;
        } else if (seconds < 0.0013) {
            return 6;
        } else if (seconds < 0.001451) {
            return 7;
        } else if (seconds < 0.0018) {
            return 8;
        } else if (seconds < 0.002251) {
            return 9;
        } else if (seconds < 0.00281) {
            return 10;
        } else if (seconds < 0.003651) {
            return 11;
        } else if (seconds < 0.00451) {
            return 12;
        } else if (seconds < 0.00565) {
            return 13;
        } else if (seconds < 0.0077) {
            return 14;
        } else if (seconds <= 0.009) {
            return 15;
        } else if (seconds <= 0.0125) {
            return 16;
        } else if (seconds <= 0.0137) {
            return 17;
        } else if (seconds <= 0.018) {
            return 18;
        } else if (seconds <= 0.0225) {
            return 19;
        } else if (seconds <= 0.0275) {
            return 20;
        } else if (seconds <= 0.035) {
            return 21;
        } else if (seconds <= 0.045) {
            return 22;
        } else if (seconds <= 0.055) {
            return 23;
        } else if (seconds <= 0.065) {
            return 24;
        } else if (seconds <= 0.085) {
            return 25;
        } else if (seconds <= 0.125) {
            return 26;
        } else if (seconds <= 0.01425) {
            return 27;
        } else if (seconds <= 0.18) {
            return 28;
        } else if (seconds <= 0.225) {
            return 29;
        } else if (seconds <= 0.275) {
            return 30;
        } else if (seconds <= 0.35) {
            return 31;
        } else if (seconds <= 0.45) {
            return 32;
        } else if (seconds <= 0.55) {
            return 33;
        } else if (seconds <= 0.7) {
            return 34;
        } else if (seconds <= 0.9) {
            return 35;
        } else if (seconds <= 1.15) {
            return 36;
        } else if (seconds <= 1.45) {
            return 37;
        } else if (seconds <= 1.8) {
            return 38;
        } else if (seconds <= 2.25) {
            return 39;
        } else if (seconds <= 2.85) {
            return 40;
        } else if (seconds <= 3.6) {
            return 41;
        } else if (seconds <= 4.5) {
            return 42;
        } else if (seconds <= 5.5) {
            return 43;
        } else if (seconds <= 6.5) {
            return 44;
        } else if (seconds <= 7) {
            return 45;
        } else if (seconds <= 9) {
            return 46;
        } else if (seconds <= 11.5) {
            return 47;
        } else if (seconds <= 14) {
            return 48;
        } else if (seconds <= 17.5) {
            return 49;
        } else if (seconds <= 22.5) {
            return 50;
        } else if (seconds <= 27.5) {
            return 51;
        } else {
            return 51;
        }
        //@formatter:on
    }

    private String seekbarToStrength(int seekbarValue, boolean timesInsteadStops) {
        String out;
        if (!timesInsteadStops) { //use stops
            //@formatter:off
            switch (seekbarValue) {
                case 0: out = "1";break;
                case 1: out = "1.5";break;
                case 2: out = "2";break;
                case 3: out = "3";break;
                case 4: out = "3.3";break;
                case 5: out = "4";break;
                case 6: out = "6";break;
                case 7: out = "6.6";break;
                case 8: out = "10";break;
                case 9: out = "13";break;
                case 10: out = "17";break;
                case 11: out = "20";break;
                case 12: out = "23";break;
                case 13: out = "27";break;
                default: out = "10";
            }
            //@formatter:on
        } else { //use x-times
            //@formatter:off
            switch (seekbarValue) {
                case 0: out = "2";break;
                case 1: out = "3";break;
                case 2: out = "4";break;
                case 3: out = "8";break;
                case 4: out = "10";break;
                case 5: out = "16";break;
                case 6: out = "64";break;
                case 7: out = "100";break;
                case 8: out = "1000";break;
                case 9: out = "10000";break;
                case 10: out = "100000";break;
                case 11: out = "1000000";break;
                case 12: out = "10000000";break;
                case 13: out = "100000000";break;
                default: out = "1000";
            }
            //@formatter:on
        }
        return out;
    }

    private int strengthToSeekbar(Double seconds, boolean timesInsteadStops) {
        if (!timesInsteadStops) { //values for stops
            if (seconds <= 1.25) {
                return 0;
            } else if (seconds < 1.75) {
                return 1;
            } else if (seconds < 2.5) {
                return 2;
            } else if (seconds < 3.15) {
                return 3;
            } else if (seconds < 3.65) {
                return 4;
            } else if (seconds < 5) {
                return 5;
            } else if (seconds < 6.3) {
                return 6;
            } else if (seconds < 8.3) {
                return 7;
            } else if (seconds < 11.5) {
                return 8;
            } else if (seconds < 15) {
                return 9;
            } else if (seconds < 18.5) {
                return 10;
            } else if (seconds < 21.5) {
                return 11;
            } else if (seconds < 25) {
                return 12;
            } else {
                return 13;
            }
        } else { //values for factor
            if (seconds <= 2.5) {
                return 0;
            } else if (seconds < 3.5) {
                return 1;
            } else if (seconds < 6) {
                return 2;
            } else if (seconds < 9) {
                return 3;
            } else if (seconds < 13) {
                return 4;
            } else if (seconds < 40) {
                return 5;
            } else if (seconds < 96) {
                return 6;
            } else if (seconds < 640) {
                return 7;
            } else if (seconds < 5500) {
                return 8;
            } else if (seconds < 55000) {
                return 9;
            } else if (seconds < 550000) {
                return 10;
            } else if (seconds < 5500000) {
                return 11;
            } else if (seconds < 55000000) {
                return 12;
            } else {
                return 13;
            }
        }
    }

}
