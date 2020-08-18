package com.slothychemdoksloth.attendancetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
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

import java.util.ArrayList;

public class Show_Absent_Dates_Activity extends AppCompatActivity {

    ListView listView_absent_dates;

    ArrayList<String> absent_dates = new ArrayList<>();

    FirebaseAuth firebaseAuth;

    ProgressDialog dialog;

    DatabaseReference firebaseDatabase, base_ref;

    TextView textView_subject_specified;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show__absent__dates_);

        listView_absent_dates = findViewById(R.id.list_absent_dates);
        textView_subject_specified = findViewById(R.id.text_subject_specified);

        dialog = new ProgressDialog(Show_Absent_Dates_Activity.this);

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance().getReference();

        String user_id = firebaseAuth.getCurrentUser().getUid();

        base_ref = firebaseDatabase.child("users").child(user_id).child("absent_dates");

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(Show_Absent_Dates_Activity.this, android.R.layout.simple_list_item_1, absent_dates);
        listView_absent_dates.setAdapter(adapter);

        load_ads(R.id.adView_single_sub, "ca-app-pub-7717760204588800/4237399421");

        base_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    absent_dates.add(ds.getKey());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                adapter.notifyDataSetChanged();
            }
        });

        listView_absent_dates.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                base_ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String subject = absent_dates.get(i);
                        String subject_spef = dataSnapshot.child(subject).getValue().toString();
                        textView_subject_specified.setText(subject_spef);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    private void load_ads(final int ad_view_id, final String ad_unit_id) {
        MobileAds.initialize(Show_Absent_Dates_Activity.this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                final AdView adView = new AdView(Show_Absent_Dates_Activity.this);
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

}
