package com.slothychemdoksloth.attendancetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Add_Single_Attendance_Activity extends AppCompatActivity {

    ListView listView_subjects;

    ArrayList<String> time_table = new ArrayList<>();

    FirebaseAuth firebaseAuth;

    private InterstitialAd mInterstitialAd;

    DatabaseReference firebaseDatabase, profile_ref, status_ref, delete_user_ref, timetable_ref, base_ref,
            total_count_ref, present_count_ref, avg_value_ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__single__attendance_);

        listView_subjects = findViewById(R.id.list_view_subjects_in_timetable);

        MobileAds.initialize(Add_Single_Attendance_Activity.this,
                "ca-app-pub-7717760204588800~9595139095");

        mInterstitialAd = new InterstitialAd(Add_Single_Attendance_Activity.this);
        mInterstitialAd.setAdUnitId("ca-app-pub-7717760204588800/1088939012");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());


        firebaseAuth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance().getReference();

        String user_id = firebaseAuth.getCurrentUser().getUid();

        status_ref = firebaseDatabase.child("users").child(user_id).child("subjects");
        profile_ref = firebaseDatabase.child("users").child(user_id).child("time_table");
        delete_user_ref = firebaseDatabase.child("users").child(user_id);
        base_ref = firebaseDatabase.child("users").child(user_id).child("subjects");
        timetable_ref = firebaseDatabase.child("users").child(user_id).child("time_table");


        load_ads(R.id.adView_single_sub, "ca-app-pub-7717760204588800/3168307443");
        load_inter_ad();

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(Add_Single_Attendance_Activity.this, android.R.layout.simple_list_item_1, time_table);
        listView_subjects.setAdapter(adapter);

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

        listView_subjects.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final String subject_name = time_table.get(i);

                AlertDialog.Builder builder = new AlertDialog.Builder(Add_Single_Attendance_Activity.this);

                builder.setTitle("Attendance");
                builder.setMessage("Select Status");
                builder.setPositiveButton("Present", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int which) {
                        increment_total_count(subject_name);
                        increment_each_sub_count(subject_name);
                        update_average_attendance_value(subject_name);
                        dialogInterface.dismiss();
                        Toast.makeText(Add_Single_Attendance_Activity.this, "Attendance Recorded", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });

                builder.setNegativeButton("Absent", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        increment_total_count(subject_name);
                        update_average_attendance_value(subject_name);
                        dialogInterface.dismiss();
                        Toast.makeText(Add_Single_Attendance_Activity.this, "Attendance Recorded", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
                return false;
            }
        });
    }

    private void load_ads(final int ad_view_id, final String ad_unit_id) {
        MobileAds.initialize(Add_Single_Attendance_Activity.this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                final AdView adView = new AdView(Add_Single_Attendance_Activity.this);
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

    private void increment_total_count(final String subject_for_selected_day) {

        total_count_ref = base_ref.child(subject_for_selected_day).child("total");
        Log.e("Present", subject_for_selected_day);
        total_count_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String total = dataSnapshot.getValue(String.class);
                int total_count = Integer.parseInt(total);
                Log.e(subject_for_selected_day, total);
                int total_count_add = total_count + 1;
                String incremented_count = String.valueOf(total_count_add).trim();
                Log.e(subject_for_selected_day + "Inc", incremented_count);
                base_ref.child(subject_for_selected_day).child("total").setValue(incremented_count);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("SLOWER", databaseError.toString());
            }
        });
    }

    private void increment_each_sub_count(final String subject_present) {

        present_count_ref = base_ref.child(subject_present).child("present");

        present_count_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String total = dataSnapshot.getValue(String.class);
                Log.e("Present", total);
                int total_count = Integer.parseInt(total);
                int total_count_add = total_count + 1;
                String incremented_count = String.valueOf(total_count_add).trim();
                base_ref.child(subject_present).child("present").setValue(incremented_count);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("SLOWER", databaseError.toString());
            }
        });
    }


    private void update_average_attendance_value(final String subject_for_selected_day) {

        avg_value_ref = base_ref.child(subject_for_selected_day);

        avg_value_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String total = dataSnapshot.child("total").getValue(String.class);
                float total_count = Float.parseFloat(total);
                Log.e("SLOWERT", String.valueOf(total_count));
                String present = dataSnapshot.child("present").getValue(String.class);
                float present_count = Float.parseFloat(present);
                Log.e("SLOWERP", String.valueOf(present_count));
                float percent = 100;
                float avg_count = (present_count / total_count) * percent;
                String incremented_avg = String.valueOf(Math.round(avg_count)).trim();
                Log.e("SLOWERA", incremented_avg);
                base_ref.child(subject_for_selected_day).child("avg").setValue(incremented_avg);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("SLOWER", databaseError.toString());
            }
        });
    }

    private void load_inter_ad() {
        /*if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
        }*/

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                mInterstitialAd.show();
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
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
                // Code to be executed when the interstitial ad is closed.
            }
        });
    }


}
