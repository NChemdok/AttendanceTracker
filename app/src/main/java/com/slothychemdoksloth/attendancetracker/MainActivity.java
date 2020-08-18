package com.slothychemdoksloth.attendancetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    final int RC_SIGN_IN = 0;
    FirebaseAuth auth;

    DatabaseReference mdatabase;

    FirebaseAuth firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();

        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build()
                //new AuthUI.IdpConfig.FacebookBuilder().build(),
                //new AuthUI.IdpConfig.PhoneBuilder().build()
        );

        if (auth.getCurrentUser() != null) {
            //user already Signed in
            Intent otp_intent = new Intent(MainActivity.this, Main_Page_Activity.class);
            startActivity(otp_intent);
            finish();
            Log.d("AUTH", auth.getCurrentUser().getEmail());
        } else {
            startActivityForResult(AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .setTheme(R.style.AppTheme)
                    .setLogo(R.mipmap.ic_launcher_round)
                    .build(), RC_SIGN_IN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @NotNull Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            //IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                finish();
                Intent main_page = new Intent(MainActivity.this, Main_Page_Activity.class);
                //Bundle bundle = new Bundle();
               // bundle.putString("email", auth.getCurrentUser().getEmail());
                //main_page.putExtras(bundle);
                startActivity(main_page);
                Log.d("AUTH", auth.getCurrentUser().getEmail());
            } else {
                Log.d("AUTH", "Not Authenticated");
            }
        }
    }
}
