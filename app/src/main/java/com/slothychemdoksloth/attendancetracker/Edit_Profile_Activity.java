package com.slothychemdoksloth.attendancetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class Edit_Profile_Activity extends AppCompatActivity {

    Button button_add_subjects, button_add_subjects_to_list, button_edit_old_timetable;

    FirebaseAuth firebaseAuth;

    EditText editText_enter_subject;

    Boolean check_if_field_is_empty, check_if_array_empty, check_if_array_exceed_limit, check_if_data_exist;

    ListView listView_subject_list;

    DatabaseReference databaseReference, base_ref;

    ArrayList<String> arrayList_subjects = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit__profile_);
        //String string_email = firebaseAuth.getCurrentUser().getEmail().toString();

        button_add_subjects = findViewById(R.id.button_add_subjects);
        button_add_subjects_to_list = findViewById(R.id.button_add_subject_to_list);
        button_edit_old_timetable = findViewById(R.id.edit_old_time_table_button);

        editText_enter_subject = findViewById(R.id.edit_text_subject_name);
        listView_subject_list = findViewById(R.id.list_subject);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(Edit_Profile_Activity.this, android.R.layout.simple_list_item_1, arrayList_subjects);

        listView_subject_list.setAdapter(arrayAdapter);

        button_add_subjects_to_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check_if_field_is_empty();
                if (check_if_field_is_empty) {
                    check_if_array_exceed_limit();
                    if (check_if_array_exceed_limit) {
                        String subject = editText_enter_subject.getText().toString().toUpperCase();
                        arrayList_subjects.add(subject);
                        editText_enter_subject.setText("");
                        arrayAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(Edit_Profile_Activity.this, "Sorry You Can Add 15 Subjects Max", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(Edit_Profile_Activity.this, "Field Cannot Be Empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        listView_subject_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                arrayList_subjects.remove(i);
                arrayAdapter.notifyDataSetChanged();
                return false;
            }
        });

        button_add_subjects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button_edit_old_timetable.setBackgroundResource(R.drawable.dark_border);
                check_if_array_empty();
                if (check_if_array_empty) {
                    check_if_data_already_exist();
                } else {
                    button_edit_old_timetable.setBackgroundResource(R.drawable.white_border);
                    Toast.makeText(Edit_Profile_Activity.this, "List Cannot Be Empty", Toast.LENGTH_LONG).show();
                }
            }
        });

        button_edit_old_timetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check_if_time_table_exist();
                button_edit_old_timetable.setBackgroundResource(R.drawable.dark_border);
            }
        });
    }

    private void check_if_time_table_exist() {
        databaseReference = FirebaseDatabase.getInstance().getReference();

        firebaseAuth = FirebaseAuth.getInstance();

        String user_id = firebaseAuth.getCurrentUser().getUid();

        base_ref = databaseReference.child("users").child(user_id).child("subjects");

        base_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Intent intent = new Intent(Edit_Profile_Activity.this, Edit_old_timetable_Activity.class);
                    //intent.putStringArrayListExtra("subject_list", arrayList_subjects);
                    startActivity(intent);
                } else {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Edit_Profile_Activity.this, R.style.MyDialogTheme);
                    builder.setTitle("No Data Exist");
                    builder.setMessage(" Sorry no Timetable exist. Please add a new Timetable ");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialogInterface, int which) {
                            dialogInterface.dismiss();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void check_if_data_already_exist() {

        databaseReference = FirebaseDatabase.getInstance().getReference();

        firebaseAuth = FirebaseAuth.getInstance();

        String user_id = firebaseAuth.getCurrentUser().getUid();

        base_ref = databaseReference.child("users").child(user_id).child("subjects");

        base_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Edit_Profile_Activity.this);
                    builder.setTitle("Timetable Already Exist");
                    builder.setMessage("New Subjects will be added to Old Time Table Are you sure? " +
                            " \n* If you want to delete old records Selected the Delete Records option instead *");
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialogInterface, int which) {
                            dialogInterface.dismiss();
                            finish();
                            Intent intent = new Intent(Edit_Profile_Activity.this, Add_Subjects_For_Each_Day_Activity.class);
                            intent.putStringArrayListExtra("subject_list", arrayList_subjects);
                            startActivity(intent);
                        }
                    });

                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            dialogInterface.dismiss();
                        }
                    });
                    AlertDialog alert = builder.create();
                    if(!(Edit_Profile_Activity.this).isFinishing())
                    {
                        alert.show();
                    }
                } else {
                    finish();
                    Intent intent = new Intent(Edit_Profile_Activity.this, Add_Subjects_For_Each_Day_Activity.class);
                    intent.putStringArrayListExtra("subject_list", arrayList_subjects);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void check_if_array_exceed_limit() {
        int size = arrayList_subjects.size();
        if (size < 15) {
            check_if_array_exceed_limit = true;
            //does not exceed limit
        } else
            check_if_array_exceed_limit = false;
    }

    private void check_if_array_empty() {
        if (arrayList_subjects.isEmpty()) {
            check_if_array_empty = false;
        } else
            check_if_array_empty = true;

    }

    private void check_if_field_is_empty() {
        String subject = editText_enter_subject.getText().toString();

        if (subject.isEmpty()) {
            check_if_field_is_empty = false;
        } else
            check_if_field_is_empty = true;
    }

}
