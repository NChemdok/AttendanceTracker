package com.slothychemdoksloth.attendancetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class Edit_old_timetable_Activity extends AppCompatActivity {

    ArrayList<String> updated_subjects = new ArrayList<>();

    FirebaseAuth firebaseAuth;

    ProgressDialog dialog;

    EditText editText_add_name_of_subject;

    Boolean check_if_array_empty, check_if_field_is_empty, check_if_array_exceed_limit, check_if_day_selected_is_null;

    String day_selected = "";

    Button button_save_changes, button_add_updated_subs;

    LinearLayout linearLayout_edit_timetable;

    ListView listView_updated_subs;

    DatabaseReference firebaseDatabase, profile_ref, subject_ref, status_ref, delete_user_ref, timetable_ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_old_timetable_);

        dialog = new ProgressDialog(Edit_old_timetable_Activity.this);

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance().getReference();

        String user_id = firebaseAuth.getCurrentUser().getUid();

        status_ref = firebaseDatabase.child("users").child(user_id).child("subjects");
        profile_ref = firebaseDatabase.child("users").child(user_id).child("time_table");
        delete_user_ref = firebaseDatabase.child("users").child(user_id);
        timetable_ref = firebaseDatabase.child("users").child(user_id);

        editText_add_name_of_subject = findViewById(R.id.edit_text_add_subject);

        listView_updated_subs = findViewById(R.id.updated_list_view);

        button_save_changes = findViewById(R.id.save_changes_button);
        button_add_updated_subs = findViewById(R.id.add_updated_subs_button);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(Edit_old_timetable_Activity.this, android.R.layout.simple_list_item_1, updated_subjects);

        listView_updated_subs.setAdapter(arrayAdapter);

        final Spinner staticSpinner = findViewById(R.id.static_spinner_edit);

        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter.createFromResource(Edit_old_timetable_Activity.this, R.array.day_of_week, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        staticAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        staticSpinner.setAdapter(staticAdapter);

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(Edit_old_timetable_Activity.this, android.R.layout.simple_list_item_1);
        //listView_show_subjects.setAdapter(adapter);

        staticSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    day_selected = "nothing";
                    button_save_changes.setVisibility(View.INVISIBLE);
                } else {
                    final String selected_day = staticSpinner.getSelectedItem().toString().toUpperCase().trim();
                    subject_ref = profile_ref.child(day_selected);
                    button_save_changes.setVisibility(View.VISIBLE);
                    day_selected = selected_day;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        listView_updated_subs.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                updated_subjects.remove(i);
                arrayAdapter.notifyDataSetChanged();
                return false;
            }
        });

        button_add_updated_subs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button_add_updated_subs.setBackgroundColor(getResources().getColor(R.color.light_blue));
                check_if_field_is_empty();
                if (check_if_field_is_empty) {
                    check_if_array_exceed_limit();
                    if (check_if_array_exceed_limit) {
                        String subject = editText_add_name_of_subject.getText().toString().toUpperCase();
                        updated_subjects.add(subject);
                        editText_add_name_of_subject.setText("");
                        arrayAdapter.notifyDataSetChanged();
                        button_add_updated_subs.setBackgroundColor(getResources().getColor(R.color.dark_blue));
                    } else {
                        button_add_updated_subs.setBackgroundColor(getResources().getColor(R.color.dark_blue));
                        Toast.makeText(Edit_old_timetable_Activity.this, "Sorry You Can Add 15 Subjects Max", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    button_add_updated_subs.setBackgroundColor(getResources().getColor(R.color.dark_blue));
                    Toast.makeText(Edit_old_timetable_Activity.this, "Field Cannot Be Empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        button_save_changes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button_add_updated_subs.setBackgroundColor(getResources().getColor(R.color.light_blue));
                check_if_array_empty();
                if (check_if_array_empty) {
                    update_the_timetable(updated_subjects, day_selected);
                    Toast.makeText(Edit_old_timetable_Activity.this, "Timetable Updated", Toast.LENGTH_LONG).show();
                    finish();
                    /*Intent intent = new Intent(Edit_old_timetable_Activity.this, Main_Page_Activity.class);
                    startActivity(intent);*/
                } else {
                    Toast.makeText(Edit_old_timetable_Activity.this, "List Cannot Be Empty", Toast.LENGTH_LONG).show();
                    button_add_updated_subs.setBackgroundColor(getResources().getColor(R.color.dark_blue));
                }
            }
        });

    }

    private void update_the_timetable(final ArrayList<String> updated_subjects, String day_selected) {

        final String timetable_selected = day_selected.trim();
        timetable_ref.child("time_table").child(timetable_selected).removeValue();
        for (int i = 0; i < updated_subjects.size(); i++) {
            String subject = updated_subjects.get(i).toUpperCase().trim();
            Log.e("Addedtodata", subject);
            timetable_ref.child("time_table").child(timetable_selected).child(subject).setValue("0");
        }
    }

    private void check_if_array_empty() {
        if (updated_subjects.isEmpty()) {
            check_if_array_empty = false;
        } else
            check_if_array_empty = true;

    }

    private void check_if_field_is_empty() {
        String subject = editText_add_name_of_subject.getText().toString();

        if (subject.isEmpty()) {
            check_if_field_is_empty = false;
        } else
            check_if_field_is_empty = true;
    }

    private void check_if_array_exceed_limit() {
        int size = updated_subjects.size();
        if (size < 15) {
            check_if_array_exceed_limit = true;
            //does not exceed limit
        } else
            check_if_array_exceed_limit = false;
    }

}
