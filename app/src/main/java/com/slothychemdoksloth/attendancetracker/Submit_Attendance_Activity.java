package com.slothychemdoksloth.attendancetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

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
import java.util.Locale;
import java.util.Map;

import static java.lang.Math.round;

public class Submit_Attendance_Activity extends AppCompatActivity {

    Button button_submit_attendance;

    TextView textView_selected_day;

    ArrayList<String> subjects_for_selected_day = new ArrayList<>();

    ArrayList<String> subject_present = new ArrayList<>();

    FirebaseAuth firebaseAuth;

    ProgressDialog dialog;

    private InterstitialAd mInterstitialAd;

    DatabaseReference databaseReference, base_ref, total_count_ref, present_count_ref, avg_value_ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit__attendance_);

        MobileAds.initialize(Submit_Attendance_Activity.this,
                "ca-app-pub-7717760204588800~9595139095");

        mInterstitialAd = new InterstitialAd(Submit_Attendance_Activity.this);
        mInterstitialAd.setAdUnitId("ca-app-pub-7717760204588800/1088939012");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        final Intent intent = getIntent();
        subjects_for_selected_day = intent.getStringArrayListExtra("subjects");
        final String day_selected = intent.getStringExtra("day_selected");

        button_submit_attendance = findViewById(R.id.button_submit_attendance);
        textView_selected_day = findViewById(R.id.day_selected_text);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        dialog = new ProgressDialog(Submit_Attendance_Activity.this);

        firebaseAuth = FirebaseAuth.getInstance();

        String user_id = firebaseAuth.getCurrentUser().getUid();

        base_ref = databaseReference.child("users").child(user_id).child("subjects");

        String day_selected_final = "Submitting Attendance For \n" + day_selected;
        textView_selected_day.setText(day_selected_final);

        load_large_ads(R.id.adView_submit_attendance, "ca-app-pub-7717760204588800/2782318738");
        load_inter_ad();
        display_the_attendance_sheet(subjects_for_selected_day);

        button_submit_attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button_submit_attendance.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                dialog.setMessage("Submitting Attendance, Please Wait...");
                dialog.show();

                if(subject_present.isEmpty())
                {
                    increment_total_count(subjects_for_selected_day);
                    update_average_attendance_value(subjects_for_selected_day);
                    dialog.dismiss();
                    finish();
                }
                else {
                    increment_total_count(subjects_for_selected_day);
                    increment_each_sub_count(subject_present);
                    update_average_attendance_value(subjects_for_selected_day);
                    dialog.dismiss();
                    finish();
                }
               /* Intent intent_go_to_main_activity = new Intent(Submit_Attendance_Activity.this, Main_Page_Activity.class);
                startActivity(intent_go_to_main_activity);*/
            }
        });
    }

    private void load_large_ads(final int ad_view_id, final String ad_unit_id) {

        MobileAds.initialize(Submit_Attendance_Activity.this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                final AdView adView = new AdView(Submit_Attendance_Activity.this);
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


    private void display_the_attendance_sheet(final ArrayList<String> subjects_for_selected_day) {
        final int[] subjects = {R.id.toggle_sub_1, R.id.toggle_sub_2, R.id.toggle_sub_3,
                R.id.toggle_sub_4, R.id.toggle_sub_5, R.id.toggle_sub_6, R.id.toggle_sub_7, R.id.toggle_sub_8,
                R.id.toggle_sub_9, R.id.toggle_sub_10, R.id.toggle_sub_11, R.id.toggle_sub_12, R.id.toggle_sub_13,
                R.id.toggle_sub_14, R.id.toggle_sub_15};

        ToggleButton[] toggleButton = new ToggleButton[subjects_for_selected_day.size()];

        for (int i = 0; i < subjects_for_selected_day.size(); i++) {
            final String subject_name = subjects_for_selected_day.get(i);
            toggleButton[i] = findViewById(subjects[i]);
            toggleButton[i].setText(subject_name);
            toggleButton[i].setTextOff(subject_name);
            toggleButton[i].setTextOn(subject_name);
            toggleButton[i].setVisibility(View.VISIBLE);

            toggleButton[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    switch (compoundButton.getId()) {
                        case R.id.toggle_sub_1: {
                            int sub_index = 0;
                            String sub_name = subjects_for_selected_day.get(sub_index);
                            if (b) {
                                subject_present.add(sub_name);
                                compoundButton.setChecked(true);
                            } else {
                                subject_present.remove(sub_name);
                                compoundButton.setChecked(false);
                            }
                            break;
                        }
                        case R.id.toggle_sub_2: {
                            int sub_index = 1;
                            String sub_name = subjects_for_selected_day.get(sub_index);
                            if (b) {
                                subject_present.add(sub_name);
                                compoundButton.setChecked(true);

                            } else {
                                subject_present.remove(sub_name);
                                compoundButton.setChecked(false);
                            }
                            break;
                        }

                        case R.id.toggle_sub_3: {
                            int sub_index = 2;
                            String sub_name = subjects_for_selected_day.get(sub_index);
                            if (b) {
                                subject_present.add(sub_name);
                                compoundButton.setChecked(true);

                            } else {
                                subject_present.remove(sub_name);
                                compoundButton.setChecked(false);

                            }
                            break;
                        }
                        case R.id.toggle_sub_4: {
                            int sub_index = 3;
                            String sub_name = subjects_for_selected_day.get(sub_index);
                            if (b) {
                                subject_present.add(sub_name);
                                compoundButton.setChecked(true);
                            } else {
                                subject_present.remove(sub_name);
                                compoundButton.setChecked(false);

                            }
                            break;
                        }
                        case R.id.toggle_sub_5: {
                            int sub_index = 4;
                            String sub_name = subjects_for_selected_day.get(sub_index);
                            if (b) {
                                subject_present.add(sub_name);
                                compoundButton.setChecked(true);

                            } else {
                                subject_present.remove(sub_name);
                                compoundButton.setChecked(false);

                            }
                            break;
                        }
                        case R.id.toggle_sub_6: {
                            int sub_index = 5;
                            String sub_name = subjects_for_selected_day.get(sub_index);
                            if (b) {
                                subject_present.add(sub_name);
                                compoundButton.setChecked(true);

                            } else {
                                subject_present.remove(sub_name);
                                compoundButton.setChecked(false);

                            }
                            break;
                        }
                        case R.id.toggle_sub_7: {
                            int sub_index = 6;
                            String sub_name = subjects_for_selected_day.get(sub_index);
                            if (b) {
                                subject_present.add(sub_name);
                                compoundButton.setChecked(true);
                            } else {
                                subject_present.remove(sub_name);
                                compoundButton.setChecked(false);

                            }
                            break;
                        }
                        case R.id.toggle_sub_8: {
                            int sub_index = 7;
                            String sub_name = subjects_for_selected_day.get(sub_index);
                            if (b) {
                                subject_present.add(sub_name);
                                compoundButton.setChecked(true);

                            } else {
                                subject_present.remove(sub_name);
                                compoundButton.setChecked(false);

                            }
                            break;
                        }
                        case R.id.toggle_sub_9: {
                            int sub_index = 8;
                            String sub_name = subjects_for_selected_day.get(sub_index);
                            if (b) {
                                subject_present.add(sub_name);
                                compoundButton.setChecked(true);

                            } else {
                                subject_present.remove(sub_name);
                                compoundButton.setChecked(false);
                            }
                            break;
                        }
                    }
                }
            });
        }

    }

    private void increment_total_count(final ArrayList<String> subject_for_selected_day) {

        int size = subject_for_selected_day.size();

        for (int i = 0; i < size; i++) {
            final String subject_name = subject_for_selected_day.get(i);
            total_count_ref = base_ref.child(subject_name).child("total");

            total_count_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String total = dataSnapshot.getValue(String.class);
                    int total_count = Integer.parseInt(total);
                    Log.e(subject_name, total);
                    int total_count_add = total_count + 1;
                    String incremented_count = String.valueOf(total_count_add).trim();
                    Log.e(subject_name + "Inc", incremented_count);
                    base_ref.child(subject_name).child("total").setValue(incremented_count);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("SLOWER", databaseError.toString());
                }
            });
        }
    }

    private void increment_each_sub_count(ArrayList<String> subject_present) {
        int size = subject_present.size();
        Log.e("SLOWER", subject_present.get(0));
        for (int i = 0; i < size; i++) {
            final String subject_name = subject_present.get(i);
            present_count_ref = base_ref.child(subject_name).child("present");

            present_count_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String total = dataSnapshot.getValue(String.class);
                    Log.e("Present", total);
                    int total_count = Integer.parseInt(total);
                    int total_count_add = total_count + 1;
                    String incremented_count = String.valueOf(total_count_add).trim();
                    base_ref.child(subject_name).child("present").setValue(incremented_count);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("SLOWER", databaseError.toString());
                }
            });
        }

    }

    private void update_average_attendance_value(final ArrayList<String> subject_for_selected_day) {

        int size = subject_for_selected_day.size();

        for (int i = 0; i < size; i++) {
            final String subject_name = subject_for_selected_day.get(i);
            avg_value_ref = base_ref.child(subject_name);

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
                    float avg_count = (present_count/total_count)*percent;
                    String incremented_avg = String.valueOf(Math.round(avg_count)).trim();
                    Log.e("SLOWERA", incremented_avg);
                    base_ref.child(subject_name).child("avg").setValue(incremented_avg);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("SLOWER", databaseError.toString());
                }
            });
        }
    }

}
