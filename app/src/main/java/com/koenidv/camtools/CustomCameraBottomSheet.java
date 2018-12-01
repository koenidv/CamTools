package com.koenidv.camtools;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

//  Created by koenidv on 01.12.2018.
public class CustomCameraBottomSheet extends BottomSheetDialogFragment {

    public static CustomCameraBottomSheet newInstance() {
        return new CustomCameraBottomSheet();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_camera_add_custom, container, false);

        return view;
    }
}
