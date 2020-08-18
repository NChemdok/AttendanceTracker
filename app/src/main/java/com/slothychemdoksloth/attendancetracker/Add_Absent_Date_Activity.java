package com.slothychemdoksloth.attendancetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class Add_Absent_Date_Activity extends AppCompatActivity {

    TextView textView_set_date;

    EditText editText_specify_subjects;

    Button button_save_date;

    String date = "";

    private DatePickerDialog.OnDateSetListener mDateSetListener;

    FirebaseAuth firebaseAuth;

    DatabaseReference firebaseDatabase, base_ref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__absent__date_);

        textView_set_date = findViewById(R.id.pick_date_text_view);
        editText_specify_subjects = findViewById(R.id.edit_text_specify_subject);
        button_save_date = findViewById(R.id.save_absent_date_button);


        firebaseAuth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance().getReference();

        String user_id = firebaseAuth.getCurrentUser().getUid();

        base_ref = firebaseDatabase.child("users").child(user_id).child("absent_dates");

        textView_set_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog date_dialog = new DatePickerDialog(
                        Add_Absent_Date_Activity.this,
                        android.R.style.Theme_DeviceDefault_Light_DarkActionBar,
                        mDateSetListener,
                        year,month,day);
                //date_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                date_dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;

                date = day + "-" + month + "-" + year ;
                textView_set_date.setText(date);
            }
        };

        button_save_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String date_selected = date.trim();
                String subject_specify = editText_specify_subjects.getText().toString().toUpperCase().trim();

                if (subject_specify.equals("")) {
                    base_ref.child(date_selected).setValue(null);
                    base_ref.child(date_selected).setValue("Not Specified");
                }
                else
                {
                    base_ref.child(date_selected).setValue(null);
                    base_ref.child(date_selected).setValue(subject_specify);
                }
                Toast.makeText(Add_Absent_Date_Activity.this, "Date Added", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Add_Absent_Date_Activity.this, Main_Page_Activity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
