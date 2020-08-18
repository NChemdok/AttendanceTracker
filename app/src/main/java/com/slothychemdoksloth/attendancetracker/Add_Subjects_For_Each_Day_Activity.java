package com.slothychemdoksloth.attendancetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.auth.api.Auth;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;

public class Add_Subjects_For_Each_Day_Activity extends AppCompatActivity {

    ArrayList<String> arrayList_subject = new ArrayList<>();


    ArrayList<String> arrayList_monday = new ArrayList<>();
    ArrayList<String> arrayList_tuesday = new ArrayList<>();
    ArrayList<String> arrayList_wednesday = new ArrayList<>();
    ArrayList<String> arrayList_thursday = new ArrayList<>();
    ArrayList<String> arrayList_friday = new ArrayList<>();
    ArrayList<String> arrayList_saturday = new ArrayList<>();

    Button button_save_data_to_database;

    FirebaseAuth firebaseAuth;

    Boolean network_connection_status;

    ProgressDialog saving_dialog;

    DatabaseReference base_ref, days_ref, subs_ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__subjects__for__each__day_);

        Intent intent = getIntent();
        // String no_of_days = intent.getStringExtra("no_of_days");
        arrayList_subject = intent.getStringArrayListExtra("subject_list");

        firebaseAuth = FirebaseAuth.getInstance();

        saving_dialog = new ProgressDialog(Add_Subjects_For_Each_Day_Activity.this);

        base_ref = FirebaseDatabase.getInstance().getReference();

        button_save_data_to_database = findViewById(R.id.button_add_data_to_database);

        String current_user = firebaseAuth.getCurrentUser().getUid();

        subs_ref = base_ref.child("users").child(current_user).child("subjects");
        days_ref = base_ref.child("users").child(current_user).child("time_table");


        set_toggle_buttons_visibility_day_1(arrayList_subject);
        set_toggle_buttons_visibility_day_2(arrayList_subject);
        set_toggle_buttons_visibility_day_3(arrayList_subject);
        set_toggle_buttons_visibility_day_4(arrayList_subject);
        set_toggle_buttons_visibility_day_5(arrayList_subject);
        set_toggle_buttons_visibility_day_6(arrayList_subject);


        button_save_data_to_database.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check_data_connection_is_available();
                if(network_connection_status) {
                    saving_dialog.setMessage("Adding Timetable Please Wait");
                    saving_dialog.show();
                    add_total_subjects(arrayList_subject);
                    add_subject_for_each_day("DAY 1", arrayList_monday);
                    add_subject_for_each_day("DAY 2", arrayList_tuesday);
                    add_subject_for_each_day("DAY 3", arrayList_wednesday);
                    add_subject_for_each_day("DAY 4", arrayList_thursday);
                    add_subject_for_each_day("DAY 5", arrayList_friday);
                    add_subject_for_each_day("DAY 6", arrayList_saturday);
                    saving_dialog.dismiss();
                    Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Added Successfully", Toast.LENGTH_LONG).show();
                    finish();
                    /*Intent intent_go_back_to_main = new Intent(Add_Subjects_For_Each_Day_Activity.this, Main_Page_Activity.class);
                    startActivity(intent_go_back_to_main);*/
                }
                else
                {
                    Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "No Connection Available", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void check_data_connection_is_available() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected())
        {
            network_connection_status = true;
        }
        else
        {
            network_connection_status = false;
        }

    }

    private void add_total_subjects(ArrayList<String> arrayList_subject) {

        for(int i = 0; i<arrayList_subject.size();i++)
        {
            String subject = arrayList_subject.get(i);
            subs_ref.child(subject).setValue(null);
            subs_ref.child(subject).child("name").setValue(subject);
            subs_ref.child(subject).child("total").setValue("0");
            subs_ref.child(subject).child("present").setValue("0");
            subs_ref.child(subject).child("avg").setValue("0");
            Log.e("Slowth", subject + subject);
        }
    }

    private void add_subject_for_each_day( String day_of_week ,ArrayList<String> arrayList_current_day) {
        String week_day = day_of_week.trim();

        for(int i = 0; i<arrayList_current_day.size();i++)
        {
            String subject = arrayList_current_day.get(i);
            days_ref.child(week_day).child(subject).setValue("0");
            Log.e("Slowth", week_day + subject);
        }
    }

    private void set_toggle_buttons_visibility_day_1(final ArrayList<String> arrayList_subject) {

        final int[] subjects_day_1 = {R.id.toggle_day_1_sub_1, R.id.toggle_day_1_sub_2, R.id.toggle_day_1_sub_3,
                R.id.toggle_day_1_sub_4, R.id.toggle_day_1_sub_5, R.id.toggle_day_1_sub_6, R.id.toggle_day_1_sub_7, R.id.toggle_day_1_sub_8,
                R.id.toggle_day_1_sub_9, R.id.toggle_day_1_sub_10, R.id.toggle_day_1_sub_11, R.id.toggle_day_1_sub_12,
                R.id.toggle_day_1_sub_13, R.id.toggle_day_1_sub_14, R.id.toggle_day_1_sub_15};

        ToggleButton[] toggleButton = new ToggleButton[arrayList_subject.size()];

        for (int i = 0; i < arrayList_subject.size(); i++) {
            final String subject_name = arrayList_subject.get(i);
            toggleButton[i] = findViewById(subjects_day_1[i]);
            toggleButton[i].setText(subject_name);
            toggleButton[i].setTextOff(subject_name);
            toggleButton[i].setTextOn(subject_name);
            toggleButton[i].setVisibility(View.VISIBLE);

            toggleButton[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    switch (compoundButton.getId()) {
                        case R.id.toggle_day_1_sub_1: {
                            int sub_index = 0;
                            String sub_name = arrayList_subject.get(sub_index);
                            if (b) {
                                arrayList_monday.add(sub_name);
                                compoundButton.setChecked(true);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Added" + sub_name, Toast.LENGTH_LONG).show();
                            } else {
                                arrayList_monday.remove(sub_name);
                                compoundButton.setChecked(false);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Removed" + sub_name, Toast.LENGTH_LONG).show();
                            }
                            break;
                        }
                        case R.id.toggle_day_1_sub_2: {
                            int sub_index = 1;
                            String sub_name = arrayList_subject.get(sub_index);
                            if (b) {
                                arrayList_monday.add(sub_name);
                                compoundButton.setChecked(true);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Added" + sub_name, Toast.LENGTH_LONG).show();
                            } else {
                                arrayList_monday.remove(sub_name);
                                compoundButton.setChecked(false);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Removed" + sub_name, Toast.LENGTH_LONG).show();
                            }
                            break;
                        }

                        case R.id.toggle_day_1_sub_3: {
                            int sub_index = 2;
                            String sub_name = arrayList_subject.get(sub_index);
                            if (b) {
                                arrayList_monday.add(sub_name);
                                compoundButton.setChecked(true);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Added" + sub_name, Toast.LENGTH_LONG).show();
                            } else {
                                arrayList_monday.remove(sub_name);
                                compoundButton.setChecked(false);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Removed" + sub_name, Toast.LENGTH_LONG).show();
                            }
                            break;
                        }
                        case R.id.toggle_day_1_sub_4: {
                            int sub_index = 3;
                            String sub_name = arrayList_subject.get(sub_index);
                            if (b) {
                                arrayList_monday.add(sub_name);
                                compoundButton.setChecked(true);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Added" + sub_name, Toast.LENGTH_LONG).show();
                            } else {
                                arrayList_monday.remove(sub_name);
                                compoundButton.setChecked(false);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Removed" + sub_name, Toast.LENGTH_LONG).show();
                            }
                            break;
                        }
                        case R.id.toggle_day_1_sub_5: {
                            int sub_index = 4;
                            String sub_name = arrayList_subject.get(sub_index);
                            if (b) {
                                arrayList_monday.add(sub_name);
                                compoundButton.setChecked(true);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Added" + sub_name, Toast.LENGTH_LONG).show();
                            } else {
                                arrayList_monday.remove(sub_name);
                                compoundButton.setChecked(false);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Removed" + sub_name, Toast.LENGTH_LONG).show();
                            }
                            break;
                        }
                        case R.id.toggle_day_1_sub_6: {
                            int sub_index = 5;
                            String sub_name = arrayList_subject.get(sub_index);
                            if (b) {
                                arrayList_monday.add(sub_name);
                                compoundButton.setChecked(true);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Added" + sub_name, Toast.LENGTH_LONG).show();
                            } else {
                                arrayList_monday.remove(sub_name);
                                compoundButton.setChecked(false);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Removed" + sub_name, Toast.LENGTH_LONG).show();
                            }
                            break;
                        }
                        case R.id.toggle_day_1_sub_7: {
                            int sub_index = 6;
                            String sub_name = arrayList_subject.get(sub_index);
                            if (b) {
                                arrayList_monday.add(sub_name);
                                compoundButton.setChecked(true);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Added" + sub_name, Toast.LENGTH_LONG).show();
                            } else {
                                arrayList_monday.remove(sub_name);
                                compoundButton.setChecked(false);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Removed" + sub_name, Toast.LENGTH_LONG).show();
                            }
                            break;
                        }
                        case R.id.toggle_day_1_sub_8: {
                            int sub_index = 7;
                            String sub_name = arrayList_subject.get(sub_index);
                            if (b) {
                                arrayList_monday.add(sub_name);
                                compoundButton.setChecked(true);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Added" + sub_name, Toast.LENGTH_LONG).show();
                            } else {
                                arrayList_monday.remove(sub_name);
                                compoundButton.setChecked(false);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Removed" + sub_name, Toast.LENGTH_LONG).show();
                            }
                            break;
                        }
                        case R.id.toggle_day_1_sub_9: {
                            int sub_index = 8;
                            String sub_name = arrayList_subject.get(sub_index);
                            if (b) {
                                arrayList_monday.add(sub_name);
                                compoundButton.setChecked(true);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Added" + sub_name, Toast.LENGTH_LONG).show();
                            } else {
                                arrayList_monday.remove(sub_name);
                                compoundButton.setChecked(false);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Removed" + sub_name, Toast.LENGTH_LONG).show();
                            }
                            break;
                        }
                        case R.id.toggle_day_1_sub_10: {
                            int sub_index = 9;
                            String sub_name = arrayList_subject.get(sub_index);
                            if (b) {
                                arrayList_monday.add(sub_name);
                                compoundButton.setChecked(true);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Added" + sub_name, Toast.LENGTH_LONG).show();
                            } else {
                                arrayList_monday.remove(sub_name);
                                compoundButton.setChecked(false);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Removed" + sub_name, Toast.LENGTH_LONG).show();
                            }
                            break;
                        }
                        case R.id.toggle_day_1_sub_11: {
                            int sub_index = 10;
                            String sub_name = arrayList_subject.get(sub_index);
                            if (b) {
                                arrayList_monday.add(sub_name);
                                compoundButton.setChecked(true);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Added" + sub_name, Toast.LENGTH_LONG).show();
                            } else {
                                arrayList_monday.remove(sub_name);
                                compoundButton.setChecked(false);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Removed" + sub_name, Toast.LENGTH_LONG).show();
                            }
                            break;
                        }

                        case R.id.toggle_day_1_sub_12: {
                            int sub_index = 11;
                            String sub_name = arrayList_subject.get(sub_index);
                            if (b) {
                                arrayList_monday.add(sub_name);
                                compoundButton.setChecked(true);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Added" + sub_name, Toast.LENGTH_LONG).show();
                            } else {
                                arrayList_monday.remove(sub_name);
                                compoundButton.setChecked(false);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Removed" + sub_name, Toast.LENGTH_LONG).show();
                            }
                            break;
                        }

                        case R.id.toggle_day_1_sub_13: {
                            int sub_index = 12;
                            String sub_name = arrayList_subject.get(sub_index);
                            if (b) {
                                arrayList_monday.add(sub_name);
                                compoundButton.setChecked(true);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Added" + sub_name, Toast.LENGTH_LONG).show();
                            } else {
                                arrayList_monday.remove(sub_name);
                                compoundButton.setChecked(false);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Removed" + sub_name, Toast.LENGTH_LONG).show();
                            }
                            break;
                        }

                        case R.id.toggle_day_1_sub_14: {
                            int sub_index = 13;
                            String sub_name = arrayList_subject.get(sub_index);
                            if (b) {
                                arrayList_monday.add(sub_name);
                                compoundButton.setChecked(true);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Added" + sub_name, Toast.LENGTH_LONG).show();
                            } else {
                                arrayList_monday.remove(sub_name);
                                compoundButton.setChecked(false);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Removed" + sub_name, Toast.LENGTH_LONG).show();
                            }
                            break;
                        }

                        case R.id.toggle_day_1_sub_15: {
                            int sub_index = 14;
                            String sub_name = arrayList_subject.get(sub_index);
                            if (b) {
                                arrayList_monday.add(sub_name);
                                compoundButton.setChecked(true);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Added" + sub_name, Toast.LENGTH_LONG).show();
                            } else {
                                arrayList_monday.remove(sub_name);
                                compoundButton.setChecked(false);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Removed" + sub_name, Toast.LENGTH_LONG).show();
                            }
                            break;
                        }
                    }
                }
            });
        }
    }

    private void set_toggle_buttons_visibility_day_2(final ArrayList<String> arrayList_subject) {
        final int[] subjects_day_2 = {R.id.toggle_day_2_sub_1, R.id.toggle_day_2_sub_2, R.id.toggle_day_2_sub_3,
                R.id.toggle_day_2_sub_4, R.id.toggle_day_2_sub_5, R.id.toggle_day_2_sub_6, R.id.toggle_day_2_sub_7, R.id.toggle_day_2_sub_8,
                R.id.toggle_day_2_sub_9};

        ToggleButton[] toggleButton = new ToggleButton[arrayList_subject.size()];

        for (int i = 0; i < arrayList_subject.size(); i++) {
            final String subject_name = arrayList_subject.get(i);
            toggleButton[i] = findViewById(subjects_day_2[i]);
            toggleButton[i].setText(subject_name);
            toggleButton[i].setTextOff(subject_name);
            toggleButton[i].setTextOn(subject_name);
            toggleButton[i].setVisibility(View.VISIBLE);

            toggleButton[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    switch (compoundButton.getId()) {
                        case R.id.toggle_day_2_sub_1: {
                            int sub_index = 0;
                            String sub_name = arrayList_subject.get(sub_index);
                            if (b) {
                                arrayList_tuesday.add(sub_name);
                                compoundButton.setChecked(true);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Added" + sub_name, Toast.LENGTH_LONG).show();
                            } else {
                                arrayList_tuesday.remove(sub_name);
                                compoundButton.setChecked(false);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Removed" + sub_name, Toast.LENGTH_LONG).show();
                            }
                            break;
                        }
                        case R.id.toggle_day_2_sub_2: {
                            int sub_index = 1;
                            String sub_name = arrayList_subject.get(sub_index);
                            if (b) {
                                arrayList_tuesday.add(sub_name);
                                compoundButton.setChecked(true);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Added" + sub_name, Toast.LENGTH_LONG).show();
                            } else {
                                arrayList_tuesday.remove(sub_name);
                                compoundButton.setChecked(false);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Removed" + sub_name, Toast.LENGTH_LONG).show();
                            }
                            break;
                        }

                        case R.id.toggle_day_2_sub_3: {
                            int sub_index = 2;
                            String sub_name = arrayList_subject.get(sub_index);
                            if (b) {
                                arrayList_tuesday.add(sub_name);
                                compoundButton.setChecked(true);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Added" + sub_name, Toast.LENGTH_LONG).show();
                            } else {
                                arrayList_tuesday.remove(sub_name);
                                compoundButton.setChecked(false);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Removed" + sub_name, Toast.LENGTH_LONG).show();
                            }
                            break;
                        }
                        case R.id.toggle_day_2_sub_4: {
                            int sub_index = 3;
                            String sub_name = arrayList_subject.get(sub_index);
                            if (b) {
                                arrayList_tuesday.add(sub_name);
                                compoundButton.setChecked(true);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Added" + sub_name, Toast.LENGTH_LONG).show();
                            } else {
                                arrayList_tuesday.remove(sub_name);
                                compoundButton.setChecked(false);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Removed" + sub_name, Toast.LENGTH_LONG).show();
                            }
                            break;
                        }
                        case R.id.toggle_day_2_sub_5: {
                            int sub_index = 4;
                            String sub_name = arrayList_subject.get(sub_index);
                            if (b) {
                                arrayList_tuesday.add(sub_name);
                                compoundButton.setChecked(true);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Added" + sub_name, Toast.LENGTH_LONG).show();
                            } else {
                                arrayList_tuesday.remove(sub_name);
                                compoundButton.setChecked(false);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Removed" + sub_name, Toast.LENGTH_LONG).show();
                            }
                            break;
                        }
                        case R.id.toggle_day_2_sub_6: {
                            int sub_index = 5;
                            String sub_name = arrayList_subject.get(sub_index);
                            if (b) {
                                arrayList_tuesday.add(sub_name);
                                compoundButton.setChecked(true);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Added" + sub_name, Toast.LENGTH_LONG).show();
                            } else {
                                arrayList_tuesday.remove(sub_name);
                                compoundButton.setChecked(false);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Removed" + sub_name, Toast.LENGTH_LONG).show();
                            }
                            break;
                        }
                        case R.id.toggle_day_2_sub_7: {
                            int sub_index = 6;
                            String sub_name = arrayList_subject.get(sub_index);
                            if (b) {
                                arrayList_tuesday.add(sub_name);
                                compoundButton.setChecked(true);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Added" + sub_name, Toast.LENGTH_LONG).show();
                            } else {
                                arrayList_tuesday.remove(sub_name);
                                compoundButton.setChecked(false);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Removed" + sub_name, Toast.LENGTH_LONG).show();
                            }
                            break;
                        }
                        case R.id.toggle_day_2_sub_8: {
                            int sub_index = 7;
                            String sub_name = arrayList_subject.get(sub_index);
                            if (b) {
                                arrayList_tuesday.add(sub_name);
                                compoundButton.setChecked(true);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Added" + sub_name, Toast.LENGTH_LONG).show();
                            } else {
                                arrayList_tuesday.remove(sub_name);
                                compoundButton.setChecked(false);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Removed" + sub_name, Toast.LENGTH_LONG).show();
                            }
                            break;
                        }
                        case R.id.toggle_day_2_sub_9: {
                            int sub_index = 8;
                            String sub_name = arrayList_subject.get(sub_index);
                            if (b) {
                                arrayList_tuesday.add(sub_name);
                                compoundButton.setChecked(true);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Added" + sub_name, Toast.LENGTH_LONG).show();
                            } else {
                                arrayList_tuesday.remove(sub_name);
                                compoundButton.setChecked(false);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Removed" + sub_name, Toast.LENGTH_LONG).show();
                            }
                            break;
                        }
                    }
                }
            });
        }
    }

    private void set_toggle_buttons_visibility_day_3(final ArrayList<String> arrayList_subject) {

        final int[] subjects_day_3 = {R.id.toggle_day_3_sub_1, R.id.toggle_day_3_sub_2, R.id.toggle_day_3_sub_3,
                R.id.toggle_day_3_sub_4, R.id.toggle_day_3_sub_5, R.id.toggle_day_3_sub_6, R.id.toggle_day_3_sub_7, R.id.toggle_day_3_sub_8,
                R.id.toggle_day_3_sub_9};

        ToggleButton[] toggleButton = new ToggleButton[arrayList_subject.size()];

        for (int i = 0; i < arrayList_subject.size(); i++) {
            final String subject_name = arrayList_subject.get(i);
            toggleButton[i] = findViewById(subjects_day_3[i]);
            toggleButton[i].setText(subject_name);
            toggleButton[i].setTextOff(subject_name);
            toggleButton[i].setTextOn(subject_name);
            toggleButton[i].setVisibility(View.VISIBLE);

            toggleButton[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    switch (compoundButton.getId()) {
                        case R.id.toggle_day_3_sub_1: {
                            int sub_index = 0;
                            String sub_name = arrayList_subject.get(sub_index);
                            if (b) {
                                arrayList_wednesday.add(sub_name);
                                compoundButton.setChecked(true);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Added" + sub_name, Toast.LENGTH_LONG).show();
                            } else {
                                arrayList_wednesday.remove(sub_name);
                                compoundButton.setChecked(false);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Removed" + sub_name, Toast.LENGTH_LONG).show();
                            }
                            break;
                        }
                        case R.id.toggle_day_3_sub_2: {
                            int sub_index = 1;
                            String sub_name = arrayList_subject.get(sub_index);
                            if (b) {
                                arrayList_wednesday.add(sub_name);
                                compoundButton.setChecked(true);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Added" + sub_name, Toast.LENGTH_LONG).show();
                            } else {
                                arrayList_wednesday.remove(sub_name);
                                compoundButton.setChecked(false);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Removed" + sub_name, Toast.LENGTH_LONG).show();
                            }
                            break;
                        }

                        case R.id.toggle_day_3_sub_3: {
                            int sub_index = 2;
                            String sub_name = arrayList_subject.get(sub_index);
                            if (b) {
                                arrayList_wednesday.add(sub_name);
                                compoundButton.setChecked(true);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Added" + sub_name, Toast.LENGTH_LONG).show();
                            } else {
                                arrayList_wednesday.remove(sub_name);
                                compoundButton.setChecked(false);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Removed" + sub_name, Toast.LENGTH_LONG).show();
                            }
                            break;
                        }
                        case R.id.toggle_day_3_sub_4: {
                            int sub_index = 3;
                            String sub_name = arrayList_subject.get(sub_index);
                            if (b) {
                                arrayList_wednesday.add(sub_name);
                                compoundButton.setChecked(true);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Added" + sub_name, Toast.LENGTH_LONG).show();
                            } else {
                                arrayList_wednesday.remove(sub_name);
                                compoundButton.setChecked(false);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Removed" + sub_name, Toast.LENGTH_LONG).show();
                            }
                            break;
                        }
                        case R.id.toggle_day_3_sub_5: {
                            int sub_index = 4;
                            String sub_name = arrayList_subject.get(sub_index);
                            if (b) {
                                arrayList_wednesday.add(sub_name);
                                compoundButton.setChecked(true);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Added" + sub_name, Toast.LENGTH_LONG).show();
                            } else {
                                arrayList_wednesday.remove(sub_name);
                                compoundButton.setChecked(false);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Removed" + sub_name, Toast.LENGTH_LONG).show();
                            }
                            break;
                        }
                        case R.id.toggle_day_3_sub_6: {
                            int sub_index = 5;
                            String sub_name = arrayList_subject.get(sub_index);
                            if (b) {
                                arrayList_wednesday.add(sub_name);
                                compoundButton.setChecked(true);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Added" + sub_name, Toast.LENGTH_LONG).show();
                            } else {
                                arrayList_wednesday.remove(sub_name);
                                compoundButton.setChecked(false);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Removed" + sub_name, Toast.LENGTH_LONG).show();
                            }
                            break;
                        }
                        case R.id.toggle_day_3_sub_7: {
                            int sub_index = 6;
                            String sub_name = arrayList_subject.get(sub_index);
                            if (b) {
                                arrayList_wednesday.add(sub_name);
                                compoundButton.setChecked(true);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Added" + sub_name, Toast.LENGTH_LONG).show();
                            } else {
                                arrayList_wednesday.remove(sub_name);
                                compoundButton.setChecked(false);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Removed" + sub_name, Toast.LENGTH_LONG).show();
                            }
                            break;
                        }
                        case R.id.toggle_day_3_sub_8: {
                            int sub_index = 7;
                            String sub_name = arrayList_subject.get(sub_index);
                            if (b) {
                                arrayList_wednesday.add(sub_name);
                                compoundButton.setChecked(true);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Added" + sub_name, Toast.LENGTH_LONG).show();
                            } else {
                                arrayList_wednesday.remove(sub_name);
                                compoundButton.setChecked(false);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Removed" + sub_name, Toast.LENGTH_LONG).show();
                            }
                            break;
                        }
                        case R.id.toggle_day_3_sub_9: {
                            int sub_index = 8;
                            String sub_name = arrayList_subject.get(sub_index);
                            if (b) {
                                arrayList_wednesday.add(sub_name);
                                compoundButton.setChecked(true);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Added" + sub_name, Toast.LENGTH_LONG).show();
                            } else {
                                arrayList_wednesday.remove(sub_name);
                                compoundButton.setChecked(false);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Removed" + sub_name, Toast.LENGTH_LONG).show();
                            }
                            break;
                        }
                    }
                }
            });
        }

    }

    private void set_toggle_buttons_visibility_day_4(final ArrayList<String> arrayList_subject) {
        final int[] subjects_day_4 = {R.id.toggle_day_4_sub_1, R.id.toggle_day_4_sub_2, R.id.toggle_day_4_sub_3,
                R.id.toggle_day_4_sub_4, R.id.toggle_day_4_sub_5, R.id.toggle_day_4_sub_6, R.id.toggle_day_4_sub_7, R.id.toggle_day_4_sub_8,
                R.id.toggle_day_4_sub_9};

        ToggleButton[] toggleButton = new ToggleButton[arrayList_subject.size()];

        for (int i = 0; i < arrayList_subject.size(); i++) {
            final String subject_name = arrayList_subject.get(i);
            toggleButton[i] = findViewById(subjects_day_4[i]);
            toggleButton[i].setText(subject_name);
            toggleButton[i].setTextOff(subject_name);
            toggleButton[i].setTextOn(subject_name);
            toggleButton[i].setVisibility(View.VISIBLE);

            toggleButton[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    switch (compoundButton.getId()) {
                        case R.id.toggle_day_4_sub_1: {
                            int sub_index = 0;
                            String sub_name = arrayList_subject.get(sub_index);
                            if (b) {
                                arrayList_thursday.add(sub_name);
                                compoundButton.setChecked(true);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Added" + sub_name, Toast.LENGTH_LONG).show();
                            } else {
                                arrayList_thursday.remove(sub_name);
                                compoundButton.setChecked(false);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Removed" + sub_name, Toast.LENGTH_LONG).show();
                            }
                            break;
                        }
                        case R.id.toggle_day_4_sub_2: {
                            int sub_index = 1;
                            String sub_name = arrayList_subject.get(sub_index);
                            if (b) {
                                arrayList_thursday.add(sub_name);
                                compoundButton.setChecked(true);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Added" + sub_name, Toast.LENGTH_LONG).show();
                            } else {
                                arrayList_thursday.remove(sub_name);
                                compoundButton.setChecked(false);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Removed" + sub_name, Toast.LENGTH_LONG).show();
                            }
                            break;
                        }

                        case R.id.toggle_day_4_sub_3: {
                            int sub_index = 2;
                            String sub_name = arrayList_subject.get(sub_index);
                            if (b) {
                                arrayList_thursday.add(sub_name);
                                compoundButton.setChecked(true);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Added" + sub_name, Toast.LENGTH_LONG).show();
                            } else {
                                arrayList_thursday.remove(sub_name);
                                compoundButton.setChecked(false);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Removed" + sub_name, Toast.LENGTH_LONG).show();
                            }
                            break;
                        }
                        case R.id.toggle_day_4_sub_4: {
                            int sub_index = 3;
                            String sub_name = arrayList_subject.get(sub_index);
                            if (b) {
                                arrayList_thursday.add(sub_name);
                                compoundButton.setChecked(true);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Added" + sub_name, Toast.LENGTH_LONG).show();
                            } else {
                                arrayList_thursday.remove(sub_name);
                                compoundButton.setChecked(false);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Removed" + sub_name, Toast.LENGTH_LONG).show();
                            }
                            break;
                        }
                        case R.id.toggle_day_4_sub_5: {
                            int sub_index = 4;
                            String sub_name = arrayList_subject.get(sub_index);
                            if (b) {
                                arrayList_thursday.add(sub_name);
                                compoundButton.setChecked(true);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Added" + sub_name, Toast.LENGTH_LONG).show();
                            } else {
                                arrayList_thursday.remove(sub_name);
                                compoundButton.setChecked(false);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Removed" + sub_name, Toast.LENGTH_LONG).show();
                            }
                            break;
                        }
                        case R.id.toggle_day_4_sub_6: {
                            int sub_index = 5;
                            String sub_name = arrayList_subject.get(sub_index);
                            if (b) {
                                arrayList_thursday.add(sub_name);
                                compoundButton.setChecked(true);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Added" + sub_name, Toast.LENGTH_LONG).show();
                            } else {
                                arrayList_thursday.remove(sub_name);
                                compoundButton.setChecked(false);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Removed" + sub_name, Toast.LENGTH_LONG).show();
                            }
                            break;
                        }
                        case R.id.toggle_day_4_sub_7: {
                            int sub_index = 6;
                            String sub_name = arrayList_subject.get(sub_index);
                            if (b) {
                                arrayList_thursday.add(sub_name);
                                compoundButton.setChecked(true);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Added" + sub_name, Toast.LENGTH_LONG).show();
                            } else {
                                arrayList_thursday.remove(sub_name);
                                compoundButton.setChecked(false);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Removed" + sub_name, Toast.LENGTH_LONG).show();
                            }
                            break;
                        }
                        case R.id.toggle_day_4_sub_8: {
                            int sub_index = 7;
                            String sub_name = arrayList_subject.get(sub_index);
                            if (b) {
                                arrayList_thursday.add(sub_name);
                                compoundButton.setChecked(true);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Added" + sub_name, Toast.LENGTH_LONG).show();
                            } else {
                                arrayList_thursday.remove(sub_name);
                                compoundButton.setChecked(false);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Removed" + sub_name, Toast.LENGTH_LONG).show();
                            }
                            break;
                        }
                        case R.id.toggle_day_4_sub_9: {
                            int sub_index = 8;
                            String sub_name = arrayList_subject.get(sub_index);
                            if (b) {
                                arrayList_thursday.add(sub_name);
                                compoundButton.setChecked(true);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Added" + sub_name, Toast.LENGTH_LONG).show();
                            } else {
                                arrayList_thursday.remove(sub_name);
                                compoundButton.setChecked(false);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Removed" + sub_name, Toast.LENGTH_LONG).show();
                            }
                            break;
                        }
                    }
                }
            });
        }

    }

    private void set_toggle_buttons_visibility_day_5(final ArrayList<String> arrayList_subject) {

        final int[] subjects_day_5 = {R.id.toggle_day_5_sub_1, R.id.toggle_day_5_sub_2, R.id.toggle_day_5_sub_3,
                R.id.toggle_day_5_sub_4, R.id.toggle_day_5_sub_5, R.id.toggle_day_5_sub_6, R.id.toggle_day_5_sub_7, R.id.toggle_day_5_sub_8,
                R.id.toggle_day_5_sub_9};

        ToggleButton[] toggleButton = new ToggleButton[arrayList_subject.size()];

        for (int i = 0; i < arrayList_subject.size(); i++) {
            final String subject_name = arrayList_subject.get(i);
            toggleButton[i] = findViewById(subjects_day_5[i]);
            toggleButton[i].setText(subject_name);
            toggleButton[i].setTextOff(subject_name);
            toggleButton[i].setTextOn(subject_name);
            toggleButton[i].setVisibility(View.VISIBLE);

            toggleButton[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    switch (compoundButton.getId()) {
                        case R.id.toggle_day_5_sub_1: {
                            int sub_index = 0;
                            String sub_name = arrayList_subject.get(sub_index);
                            if (b) {
                                arrayList_friday.add(sub_name);
                                compoundButton.setChecked(true);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Added" + sub_name, Toast.LENGTH_LONG).show();
                            } else {
                                arrayList_friday.remove(sub_name);
                                compoundButton.setChecked(false);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Removed" + sub_name, Toast.LENGTH_LONG).show();
                            }
                            break;
                        }
                        case R.id.toggle_day_5_sub_2: {
                            int sub_index = 1;
                            String sub_name = arrayList_subject.get(sub_index);
                            if (b) {
                                arrayList_friday.add(sub_name);
                                compoundButton.setChecked(true);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Added" + sub_name, Toast.LENGTH_LONG).show();
                            } else {
                                arrayList_friday.remove(sub_name);
                                compoundButton.setChecked(false);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Removed" + sub_name, Toast.LENGTH_LONG).show();
                            }
                            break;
                        }

                        case R.id.toggle_day_5_sub_3: {
                            int sub_index = 2;
                            String sub_name = arrayList_subject.get(sub_index);
                            if (b) {
                                arrayList_friday.add(sub_name);
                                compoundButton.setChecked(true);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Added" + sub_name, Toast.LENGTH_LONG).show();
                            } else {
                                arrayList_friday.remove(sub_name);
                                compoundButton.setChecked(false);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Removed" + sub_name, Toast.LENGTH_LONG).show();
                            }
                            break;
                        }
                        case R.id.toggle_day_5_sub_4: {
                            int sub_index = 3;
                            String sub_name = arrayList_subject.get(sub_index);
                            if (b) {
                                arrayList_friday.add(sub_name);
                                compoundButton.setChecked(true);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Added" + sub_name, Toast.LENGTH_LONG).show();
                            } else {
                                arrayList_friday.remove(sub_name);
                                compoundButton.setChecked(false);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Removed" + sub_name, Toast.LENGTH_LONG).show();
                            }
                            break;
                        }
                        case R.id.toggle_day_5_sub_5: {
                            int sub_index = 4;
                            String sub_name = arrayList_subject.get(sub_index);
                            if (b) {
                                arrayList_friday.add(sub_name);
                                compoundButton.setChecked(true);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Added" + sub_name, Toast.LENGTH_LONG).show();
                            } else {
                                arrayList_friday.remove(sub_name);
                                compoundButton.setChecked(false);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Removed" + sub_name, Toast.LENGTH_LONG).show();
                            }
                            break;
                        }
                        case R.id.toggle_day_5_sub_6: {
                            int sub_index = 5;
                            String sub_name = arrayList_subject.get(sub_index);
                            if (b) {
                                arrayList_friday.add(sub_name);
                                compoundButton.setChecked(true);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Added" + sub_name, Toast.LENGTH_LONG).show();
                            } else {
                                arrayList_friday.remove(sub_name);
                                compoundButton.setChecked(false);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Removed" + sub_name, Toast.LENGTH_LONG).show();
                            }
                            break;
                        }
                        case R.id.toggle_day_5_sub_7: {
                            int sub_index = 6;
                            String sub_name = arrayList_subject.get(sub_index);
                            if (b) {
                                arrayList_friday.add(sub_name);
                                compoundButton.setChecked(true);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Added" + sub_name, Toast.LENGTH_LONG).show();
                            } else {
                                arrayList_friday.remove(sub_name);
                                compoundButton.setChecked(false);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Removed" + sub_name, Toast.LENGTH_LONG).show();
                            }
                            break;
                        }
                        case R.id.toggle_day_5_sub_8: {
                            int sub_index = 7;
                            String sub_name = arrayList_subject.get(sub_index);
                            if (b) {
                                arrayList_friday.add(sub_name);
                                compoundButton.setChecked(true);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Added" + sub_name, Toast.LENGTH_LONG).show();
                            } else {
                                arrayList_friday.remove(sub_name);
                                compoundButton.setChecked(false);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Removed" + sub_name, Toast.LENGTH_LONG).show();
                            }
                            break;
                        }
                        case R.id.toggle_day_5_sub_9: {
                            int sub_index = 8;
                            String sub_name = arrayList_subject.get(sub_index);
                            if (b) {
                                arrayList_friday.add(sub_name);
                                compoundButton.setChecked(true);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Added" + sub_name, Toast.LENGTH_LONG).show();
                            } else {
                                arrayList_friday.remove(sub_name);
                                compoundButton.setChecked(false);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Removed" + sub_name, Toast.LENGTH_LONG).show();
                            }
                            break;
                        }
                    }
                }
            });
        }

    }

    private void set_toggle_buttons_visibility_day_6(final ArrayList<String> arrayList_subject) {
        final int[] subjects_day_6 = {R.id.toggle_day_6_sub_1, R.id.toggle_day_6_sub_2, R.id.toggle_day_6_sub_3,
                R.id.toggle_day_6_sub_4, R.id.toggle_day_6_sub_5, R.id.toggle_day_6_sub_6, R.id.toggle_day_6_sub_7, R.id.toggle_day_6_sub_8,
                R.id.toggle_day_6_sub_9};

        ToggleButton[] toggleButton = new ToggleButton[arrayList_subject.size()];

        for (int i = 0; i < arrayList_subject.size(); i++) {
            final String subject_name = arrayList_subject.get(i);
            toggleButton[i] = findViewById(subjects_day_6[i]);
            toggleButton[i].setText(subject_name);
            toggleButton[i].setTextOff(subject_name);
            toggleButton[i].setTextOn(subject_name);
            toggleButton[i].setVisibility(View.VISIBLE);

            toggleButton[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    switch (compoundButton.getId()) {
                        case R.id.toggle_day_6_sub_1: {
                            int sub_index = 0;
                            String sub_name = arrayList_subject.get(sub_index);
                            if (b) {
                                arrayList_saturday.add(sub_name);
                                compoundButton.setChecked(true);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Added" + sub_name, Toast.LENGTH_LONG).show();
                            } else {
                                arrayList_saturday.remove(sub_name);
                                compoundButton.setChecked(false);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Removed" + sub_name, Toast.LENGTH_LONG).show();
                            }
                            break;
                        }
                        case R.id.toggle_day_6_sub_2: {
                            int sub_index = 1;
                            String sub_name = arrayList_subject.get(sub_index);
                            if (b) {
                                arrayList_saturday.add(sub_name);
                                compoundButton.setChecked(true);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Added" + sub_name, Toast.LENGTH_LONG).show();
                            } else {
                                arrayList_saturday.remove(sub_name);
                                compoundButton.setChecked(false);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Removed" + sub_name, Toast.LENGTH_LONG).show();
                            }
                            break;
                        }

                        case R.id.toggle_day_6_sub_3: {
                            int sub_index = 2;
                            String sub_name = arrayList_subject.get(sub_index);
                            if (b) {
                                arrayList_saturday.add(sub_name);
                                compoundButton.setChecked(true);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Added" + sub_name, Toast.LENGTH_LONG).show();
                            } else {
                                arrayList_saturday.remove(sub_name);
                                compoundButton.setChecked(false);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Removed" + sub_name, Toast.LENGTH_LONG).show();
                            }
                            break;
                        }
                        case R.id.toggle_day_6_sub_4: {
                            int sub_index = 3;
                            String sub_name = arrayList_subject.get(sub_index);
                            if (b) {
                                arrayList_saturday.add(sub_name);
                                compoundButton.setChecked(true);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Added" + sub_name, Toast.LENGTH_LONG).show();
                            } else {
                                arrayList_saturday.remove(sub_name);
                                compoundButton.setChecked(false);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Removed" + sub_name, Toast.LENGTH_LONG).show();
                            }
                            break;
                        }
                        case R.id.toggle_day_6_sub_5: {
                            int sub_index = 4;
                            String sub_name = arrayList_subject.get(sub_index);
                            if (b) {
                                arrayList_saturday.add(sub_name);
                                compoundButton.setChecked(true);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Added" + sub_name, Toast.LENGTH_LONG).show();
                            } else {
                                arrayList_saturday.remove(sub_name);
                                compoundButton.setChecked(false);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Removed" + sub_name, Toast.LENGTH_LONG).show();
                            }
                            break;
                        }
                        case R.id.toggle_day_6_sub_6: {
                            int sub_index = 5;
                            String sub_name = arrayList_subject.get(sub_index);
                            if (b) {
                                arrayList_saturday.add(sub_name);
                                compoundButton.setChecked(true);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Added" + sub_name, Toast.LENGTH_LONG).show();
                            } else {
                                arrayList_saturday.remove(sub_name);
                                compoundButton.setChecked(false);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Removed" + sub_name, Toast.LENGTH_LONG).show();
                            }
                            break;
                        }
                        case R.id.toggle_day_6_sub_7: {
                            int sub_index = 6;
                            String sub_name = arrayList_subject.get(sub_index);
                            if (b) {
                                arrayList_saturday.add(sub_name);
                                compoundButton.setChecked(true);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Added" + sub_name, Toast.LENGTH_LONG).show();
                            } else {
                                arrayList_saturday.remove(sub_name);
                                compoundButton.setChecked(false);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Removed" + sub_name, Toast.LENGTH_LONG).show();
                            }
                            break;
                        }
                        case R.id.toggle_day_6_sub_8: {
                            int sub_index = 7;
                            String sub_name = arrayList_subject.get(sub_index);
                            if (b) {
                                arrayList_saturday.add(sub_name);
                                compoundButton.setChecked(true);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Added" + sub_name, Toast.LENGTH_LONG).show();
                            } else {
                                arrayList_saturday.remove(sub_name);
                                compoundButton.setChecked(false);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Removed" + sub_name, Toast.LENGTH_LONG).show();
                            }
                            break;
                        }
                        case R.id.toggle_day_6_sub_9: {
                            int sub_index = 8;
                            String sub_name = arrayList_subject.get(sub_index);
                            if (b) {
                                arrayList_saturday.add(sub_name);
                                compoundButton.setChecked(true);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Added" + sub_name, Toast.LENGTH_LONG).show();
                            } else {
                                arrayList_saturday.remove(sub_name);
                                compoundButton.setChecked(false);
                                Toast.makeText(Add_Subjects_For_Each_Day_Activity.this, "Removed" + sub_name, Toast.LENGTH_LONG).show();
                            }
                            break;
                        }
                    }
                }
            });
        }
    }
}
