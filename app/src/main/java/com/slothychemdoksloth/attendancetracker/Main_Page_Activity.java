package com.slothychemdoksloth.attendancetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.Toolbar;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.auth.api.Auth;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Main_Page_Activity extends AppCompatActivity {

    TextView textView_user_name, textView_no_data_to_display ;

    ImageView imageView_prof_pic;

    FirebaseAuth firebaseAuth;

    String string_photo_url = "", string_user_name = "";

    RelativeLayout relativeLayout_status, relativeLayout_update, relativeLayout_profile, relativeLayout_about;

    Button button_add_single_attendance, button_add_absent_date, button_about, button_status, button_update, button_profile ;

    ArrayList<String> subjects_for_selected_day = new ArrayList<>();

    ListView listView_status, listView_timetable;

    ProgressDialog dialog;

    AlarmManager alarmManager;

    Boolean network_connection_status, check_permission;

    Custom_litsView_Adapter_Inflater custom_litsView_adapter_inflater;

    DatabaseReference firebaseDatabase, profile_ref, subject_ref, status_ref, delete_user_ref, timetable_ref, base_ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__page_);
        //Bundle intent = getIntent().getExtras();
        //String string_email = intent.getString("email");

        dialog = new ProgressDialog(Main_Page_Activity.this);

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance().getReference();

        String user_id = firebaseAuth.getCurrentUser().getUid();

        string_photo_url = firebaseAuth.getCurrentUser().getPhotoUrl().toString();

        string_user_name = firebaseAuth.getCurrentUser().getDisplayName();

        status_ref = firebaseDatabase.child("users").child(user_id).child("subjects");
        profile_ref = firebaseDatabase.child("users").child(user_id).child("time_table");
        delete_user_ref = firebaseDatabase.child("users").child(user_id);
        timetable_ref = firebaseDatabase.child("users").child(user_id).child("time_table");
        base_ref = firebaseDatabase.child("users").child(user_id).child("absent_dates");


        //textView_profile = findViewById(R.id.text_profile);


        button_profile = findViewById(R.id.button_profile);
        button_status = findViewById(R.id.button_view_status);
        button_update = findViewById(R.id.button_enter_data);

        button_add_single_attendance = findViewById(R.id.button_add_attendance_for_single_sub);
        button_add_absent_date = findViewById(R.id.button_add_absent_date);

        button_about = findViewById(R.id.button_about);

        //button_reset_status = findViewById(R.id.button_reset_status);

        //button_show_absent_dates = findViewById(R.id.show_absent_dates_button);

        relativeLayout_profile = findViewById(R.id.layout_profile);
        relativeLayout_status = findViewById(R.id.layout_status);
        relativeLayout_update = findViewById(R.id.layout_update);
        relativeLayout_about = findViewById(R.id.layout_about);

        final Spinner staticSpinner = findViewById(R.id.static_spinner);

        textView_user_name = findViewById(R.id.text_user_name);
        imageView_prof_pic = findViewById(R.id.user_prof_image);

        check_app_permission();

        // Create an ArrayAdapter using the string array and a default spinner
        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter.createFromResource(Main_Page_Activity.this, R.array.day_of_week, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        staticAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        staticSpinner.setAdapter(staticAdapter);

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(Main_Page_Activity.this, android.R.layout.simple_list_item_1, subjects_for_selected_day);
        //listView_show_subjects.setAdapter(adapter);

        staticSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    Log.e("LOG", "Nothing");
                    load_large_ads(R.id.adView, "ca-app-pub-7717760204588800/6721563743");
                } else {
                    load_large_ads(R.id.adView, "ca-app-pub-7717760204588800/6721563743");
                    final String day_selected = staticSpinner.getSelectedItem().toString().toUpperCase().trim();
                    subject_ref = profile_ref.child(day_selected);
                    subjects_for_selected_day.clear();
                    dialog.setMessage("Loading Subjects for Selected Day, Please Wait...");
                    dialog.show();
                    subject_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    long child_count = dataSnapshot.getChildrenCount();
                                    String subject = ds.getKey();
                                    add_subject_to_list(day_selected, child_count, subject);
                                    adapter.notifyDataSetChanged();
                                }
                                dialog.dismiss();
                            } else {
                                Toast.makeText(Main_Page_Activity.this, "No Subject On Selected Day", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    //Log.v("item", (String) parent.getItemAtPosition(position));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        button_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Transition transition = new Slide(Gravity.TOP);
                transition.setDuration(400);
                transition.addTarget(R.id.layout_update);
                TransitionManager.beginDelayedTransition(relativeLayout_update, transition);
                relativeLayout_update.setVisibility(View.VISIBLE);
                relativeLayout_profile.setVisibility(View.INVISIBLE);
                relativeLayout_status.setVisibility(View.INVISIBLE);
                relativeLayout_about.setVisibility(View.INVISIBLE);
                button_update.setBackgroundResource(R.drawable.dark_border);
                button_status.setBackgroundResource(R.drawable.white_border);
                button_profile.setBackgroundResource(R.drawable.white_border);
                button_about.setBackgroundResource(R.drawable.white_border);
                //load_large_ads(R.id.adView, "ca-app-pub-7717760204588800/6721563743");
            }
        });

        button_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check_data_connection_is_available();
                if (network_connection_status) {
                    relativeLayout_update.setVisibility(View.INVISIBLE);
                    relativeLayout_profile.setVisibility(View.INVISIBLE);
                    Transition transition = new Slide(Gravity.TOP);
                    transition.setDuration(400);
                    transition.addTarget(R.id.layout_status);
                    TransitionManager.beginDelayedTransition(relativeLayout_status, transition);
                    relativeLayout_status.setVisibility(View.VISIBLE);
                    relativeLayout_about.setVisibility(View.INVISIBLE);
                    button_status.setBackgroundResource(R.drawable.dark_border);
                    button_update.setBackgroundResource(R.drawable.white_border);
                    button_about.setBackgroundResource(R.drawable.white_border);
                    button_profile.setBackgroundResource(R.drawable.white_border);
                    view_status();
                } else {
                    Toast toast = Toast.makeText(Main_Page_Activity.this, "No Connection Available", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }

            }
        });

        button_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                relativeLayout_update.setVisibility(View.INVISIBLE);
                Transition transition = new Slide(Gravity.TOP);
                transition.setDuration(400);
                transition.addTarget(R.id.layout_profile);
                TransitionManager.beginDelayedTransition(relativeLayout_profile, transition);
                relativeLayout_profile.setVisibility(View.VISIBLE);
                relativeLayout_status.setVisibility(View.INVISIBLE);
                relativeLayout_about.setVisibility(View.INVISIBLE);
                button_profile.setBackgroundResource(R.drawable.dark_border);
                button_status.setBackgroundResource(R.drawable.white_border);
                button_about.setBackgroundResource(R.drawable.white_border);
                button_update.setBackgroundResource(R.drawable.white_border);
                edit_profile(string_photo_url, string_user_name);

            }
        });

        button_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                relativeLayout_update.setVisibility(View.INVISIBLE);
                relativeLayout_profile.setVisibility(View.INVISIBLE);
                Transition transition = new Slide(Gravity.TOP);
                transition.setDuration(400);
                transition.addTarget(R.id.layout_about);
                TransitionManager.beginDelayedTransition(relativeLayout_about, transition);
                relativeLayout_about.setVisibility(View.VISIBLE);
                //relativeLayout_about.setVisibility(View.VISIBLE);
                relativeLayout_status.setVisibility(View.INVISIBLE);
                button_profile.setBackgroundResource(R.drawable.white_border);
                button_status.setBackgroundResource(R.drawable.white_border);
                button_about.setBackgroundResource(R.drawable.dark_border);
                button_update.setBackgroundResource(R.drawable.white_border);
            }
        });

        button_add_single_attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(Main_Page_Activity.this, "Clicked", Toast.LENGTH_LONG).show();
                Intent intent_add_single_sub = new Intent(Main_Page_Activity.this, Add_Single_Attendance_Activity.class);
                startActivity(intent_add_single_sub);
            }
        });

        button_add_absent_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(Main_Page_Activity.this, "Clicked", Toast.LENGTH_LONG).show();
                Intent intent_absent_date = new Intent(Main_Page_Activity.this, Add_Absent_Date_Activity.class);
                startActivity(intent_absent_date);
            }
        });

    }

    private void check_app_permission() {
        check_permission();
        if (check_permission) {

//If your app has access to the device’s storage, then print the following message to Android Studio’s Logcat//
            //set the notifictaion time
            set_notification_time();
            Log.e("permission", "Permission already granted.");
        } else {

//If your app doesn’t have permission to access external storage, then call requestPermission//

            requestPermission();
        }
    }

    private void check_permission() {
        int result = ContextCompat.checkSelfPermission(Main_Page_Activity.this, Manifest.permission.SET_ALARM);

        if (result == PackageManager.PERMISSION_GRANTED) {
            check_permission = true;
        } else {
            check_permission = false;
        }
    }


    private void requestPermission() {
        ActivityCompat.requestPermissions(Main_Page_Activity.this, new String[]{Manifest.permission.SET_ALARM}, 1);

    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(Main_Page_Activity.this,
                            "Permission Granted", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(Main_Page_Activity.this,
                            "Permission Denied", Toast.LENGTH_LONG).show();

                }
                break;
        }
    }

    private void add_subject_to_list(String day_selected, long count, String subject) {
        subjects_for_selected_day.add(subject);
        int size = (int) count;
        if (size == subjects_for_selected_day.size()) {
            Intent intent = new Intent(Main_Page_Activity.this, Submit_Attendance_Activity.class);
            intent.putStringArrayListExtra("subjects", subjects_for_selected_day);
            intent.putExtra("day_selected", day_selected);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_profile, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_edit_profile:
                Intent intent = new Intent(Main_Page_Activity.this, Edit_Profile_Activity.class);
                startActivity(intent);
                return true;

            case R.id.menu_logout:
                FirebaseAuth.getInstance().signOut();
                finish();
                Intent logout_intent = new Intent(Main_Page_Activity.this, Logout_Page_Activity.class);
                startActivity(logout_intent);
                return true;

            case R.id.wipe_data:
                show_confirmation_prompt();
                return true;

            case R.id.view_absent_dates:
                Intent absent_intent = new Intent(Main_Page_Activity.this, Show_Absent_Dates_Activity.class);
                startActivity(absent_intent);
                return true;

            default:
                super.onOptionsItemSelected(item);

        }
        return true;

    }

    private void show_confirmation_prompt() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Main_Page_Activity.this);

        builder.setTitle("All Attendance Data Will Be Deleted");
        builder.setMessage("Are you sure?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialogInterface, int which) {
                dialog.setMessage("Deleting Records, Please Wait...");
                dialog.show();
                delete_user_ref.child("subjects").removeValue();
                delete_user_ref.child("time_table").removeValue();
                base_ref.removeValue();
                dialog.dismiss();
                Toast.makeText(Main_Page_Activity.this, "All Records Have Been Deleted", Toast.LENGTH_SHORT).show();
                dialogInterface.dismiss();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                Toast.makeText(Main_Page_Activity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                dialogInterface.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void set_notification_time() {

        /*Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, 20);
        calendar.set(Calendar.MINUTE, 44);
        calendar.set(Calendar.SECOND, 1);

        Intent intent = new Intent(getApplicationContext(), Notification_Receiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        Log.e("ALARM", "SET");*/
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent alarmIntent = new Intent(Main_Page_Activity.this, Notification_Receiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(Main_Page_Activity.this, 0, alarmIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        alarmIntent.setData((Uri.parse("custom://"+System.currentTimeMillis())));
        alarmManager.cancel(pendingIntent);

        Calendar alarmStartTime = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        alarmStartTime.set(Calendar.HOUR_OF_DAY, 19);
        alarmStartTime.set(Calendar.MINUTE, 30);
        alarmStartTime.set(Calendar.SECOND, 0);
        if (now.after(alarmStartTime)) {
            Log.d("Hey","Added a day");
            alarmStartTime.add(Calendar.DATE, 1);
        }

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmStartTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        Log.d("Alarm","Alarms set for everyday 8 am.");
    }

    private void edit_profile(String string_photo_url, String string_user_name) {


        textView_user_name.setText(string_user_name);

        listView_status = findViewById(R.id.list_time_table);

        final ArrayList<String> time_table = new ArrayList<>();

        Picasso.with(Main_Page_Activity.this)
                .load(string_photo_url)
                .resize(200, 200).into(imageView_prof_pic);

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(Main_Page_Activity.this, android.R.layout.simple_list_item_1, time_table);
        listView_status.setAdapter(adapter);

        status_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String sub_name = ds.child("name").getValue().toString();
                    time_table.add(sub_name);
                    adapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void view_status() {

        //custom_status_adapter = new Custom_listView_Adapter();

        final ArrayList<Custom_listView_Adapter> status_list;
        final ArrayAdapter<Custom_listView_Adapter> arrayAdapter_status;

        listView_status = findViewById(R.id.listView_status);

        textView_no_data_to_display = findViewById(R.id.text_no_data_to_display);

        status_list = new ArrayList<>();

        final ProgressDialog status_dialog = new ProgressDialog(Main_Page_Activity.this);

        arrayAdapter_status = new ArrayAdapter<>(Main_Page_Activity.this, R.layout.custom_listview_status, status_list);

        status_dialog.setMessage("Updating The Details Please Wait...");
        status_dialog.show();

        status_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Custom_listView_Adapter cus_list = new Custom_listView_Adapter();
                        cus_list.setName(ds.getValue(Custom_listView_Adapter.class).getName());
                        cus_list.setTotal(ds.getValue(Custom_listView_Adapter.class).getTotal());
                        cus_list.setPresent(ds.getValue(Custom_listView_Adapter.class).getPresent());
                        cus_list.setAvg(ds.getValue(Custom_listView_Adapter.class).getAvg());
                        status_list.add(cus_list);
                    }
                    custom_litsView_adapter_inflater = new Custom_litsView_Adapter_Inflater
                            (Main_Page_Activity.this, status_list);
                    listView_status.setAdapter(custom_litsView_adapter_inflater);
                    arrayAdapter_status.notifyDataSetChanged();
                    status_dialog.dismiss();
                } else {
                    listView_status.setVisibility(View.INVISIBLE);
                    textView_no_data_to_display.setVisibility(View.VISIBLE);
                    arrayAdapter_status.notifyDataSetChanged();
                    status_dialog.dismiss();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                arrayAdapter_status.notifyDataSetChanged();
                status_dialog.dismiss();
            }
        });

    }


    private void load_large_ads(final int ad_view_id, final String ad_unit_id) {

        MobileAds.initialize(Main_Page_Activity.this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                final AdView adView = new AdView(Main_Page_Activity.this);
                //AdSize adSize = new AdSize(300, 250);
                adView.setAdSize(AdSize.BANNER);
                adView.setAdUnitId(ad_unit_id);
                //adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
                // TODO: Add adView to your view hierarchy.
                final AdView mAdView = findViewById(ad_view_id);
                final AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);

                mAdView.setAdListener(new AdListener() {
                    @Override
                    public void onAdLoaded() {
                        mAdView.loadAd(adRequest);
                        // Code to be executed when an ad finishes loading.
                    }

                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        //load_ads(R.id.adView);
                    }

                    @Override
                    public void onAdOpened() {
                        // Code to be executed when an ad opens an overlay that
                        // covers the screen.
                    }

                    @Override
                    public void onAdClicked() {
                        // Code to be executed when the user clicks on an ad.
                    }

                    @Override
                    public void onAdLeftApplication() {
                        // Code to be executed when the user has left the app.
                    }

                    @Override
                    public void onAdClosed() {
                        mAdView.loadAd(adRequest);
                        // Code to be executed when the user is about to return
                        // to the app after tapping on an ad.
                    }
                });

            }
        });

    }

    private void check_data_connection_is_available() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            network_connection_status = true;
        } else {
            network_connection_status = false;
        }

    }
}
