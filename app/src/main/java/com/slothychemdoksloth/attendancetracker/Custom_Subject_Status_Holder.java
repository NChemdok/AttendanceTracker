package com.slothychemdoksloth.attendancetracker;

import android.view.View;
import android.widget.TextView;

public class Custom_Subject_Status_Holder {
    TextView view_name, view_total, view_present, view_avg;

    public Custom_Subject_Status_Holder(View view)
    {
        view_name = view.findViewById(R.id.text_subject_name);
        view_total = view.findViewById(R.id.text_subject_total);
        view_present = view.findViewById(R.id.text_subject_present);
        view_avg = view.findViewById(R.id.text_subject_avg);
    }
}
