package com.example.spacr;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        MainActivity.calendar = Calendar.getInstance();
        int year = MainActivity.calendar.get(Calendar.YEAR);
        int month = MainActivity.calendar.get(Calendar.MONTH);
        int day = MainActivity.calendar.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(requireActivity(), (DatePickerDialog.OnDateSetListener) requireActivity(),
                year, month, day);
    }
}
