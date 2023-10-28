package com.hfad.smstestfinal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignIn extends AppCompatActivity {

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    SignInButton btn;
    FirebaseDatabase database;
    DatabaseReference reference;
    String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        checkPermission(Manifest.permission.READ_SMS, 101);



        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(this, gso);

        btn = findViewById(R.id.sign_in_button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        GoogleSignInAccount acc = GoogleSignIn.getLastSignedInAccount(this);
        if(acc!=null)
        {


            navigatetoHome();
        }


    }

    void signIn() {
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent, 1000);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == 1000) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                task.getResult(ApiException.class);
                GoogleSignInAccount acc = GoogleSignIn.getLastSignedInAccount(this);

                assert acc != null;
                user = acc.getEmail();
                assert user != null;
                user = user.substring(0, user.indexOf("@"));



                database = FirebaseDatabase.getInstance();
                reference = database.getReference(user);

                reference.child("index").setValue(0);
                reference.child("date").setValue(0);

                navigatetoHome();
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void navigatetoHome()
    {
        finish();


        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }
    // Function to check and request permission.
    public void checkPermission(String permission, int requestCode)
    {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(this, new String[] { permission }, requestCode);
        }
        else {
            //Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }
}

