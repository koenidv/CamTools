package com.koenidv.camtools;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.annotation.Nullable;
import io.feeeei.circleseekbar.CircleSeekBar;

public class TimerSheet extends BottomSheetDialogFragment {

    private final static String TAG = "TimerSheet";
    float startTime;

    private CircleSeekBar timeSeekbar;
    private TextView timeTextView;
    boolean startService = false;
    String tagName = "";

    public TimerSheet() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sheet_timer, container, false);
        TimerService mTimer = new TimerService();

        timeSeekbar = view.findViewById(R.id.timeSeekbar);
        timeTextView = view.findViewById(R.id.timeTextView);
        Button mCancelButton = view.findViewById(R.id.cancelButton);

        Activity mActivity = getActivity();

        int mMsTime = Math.round(startTime * 1000);
        timeSeekbar.setMaxProcess(mMsTime);

        Intent timerIntent = new Intent(mActivity, mTimer.getClass())
                .putExtra("time", mMsTime)
                .putExtra("name", tagName);
        if (startService) {
            getActivity().startService(timerIntent);
        }

        mCancelButton.setOnClickListener(v -> {
            getActivity().stopService(timerIntent);
            this.dismiss();
            Intent intent = new Intent("com.koenidv.camtools.timer");
            intent.putExtra("dismiss", true)
                    .putExtra("name", tagName);
            getActivity().sendBroadcast(intent);
        });

        return view;
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
        getActivity().registerReceiver(receiver, new IntentFilter(TimerService.ACTION));
        Log.i(TAG, "Registered broacast receiver");
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(receiver);
        Log.i(TAG, "Unregistered broacast receiver");
    }

    @Override
    public void onStop() {
        try {
            getActivity().unregisterReceiver(receiver);
        } catch (Exception e) {
            // Receiver was probably already stopped in onPause()
        }
        super.onStop();
    }

    private void updateGUI(Intent intent) {
        if (intent.getExtras() != null) {
            ModuleManager mModuleManager = new ModuleManager();

            long millisUntilFinished = intent.getLongExtra("remaining", 0);
            timeTextView.setText(mModuleManager.convertTime(getActivity(), ((float) Math.round(millisUntilFinished / 100) / 10), false));
            timeSeekbar.setCurProcess((int) millisUntilFinished);
        }
    }
}