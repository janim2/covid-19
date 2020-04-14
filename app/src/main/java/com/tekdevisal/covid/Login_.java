package com.tekdevisal.covid;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.core.Tag;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class Login_ extends AppCompatActivity {

    private CountryCodePicker ccp;
    private EditText phonetext;
    private EditText codeText;
    private Button continueNextButton;
    private String checker="", phoneNumber="";
    private RelativeLayout layout;
    private Snackbar snackbar;
    private ProgressDialog loadingbar;

    //firebase phone number verification
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private FirebaseAuth mAuth;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendingToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_);

        mAuth = FirebaseAuth.getInstance();
        loadingbar = new ProgressDialog(this);

        phonetext = findViewById(R.id.phoneText);
        codeText = findViewById(R.id.codeText);
        continueNextButton = findViewById(R.id.continueNextButton);
        layout = findViewById(R.id.phoneAuth);

        ccp = findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(phonetext);

        continueNextButton.setOnClickListener(v -> {
            if(continueNextButton.getText().equals("Submit") || checker.equals("Code Sent")){
                String verificationcode = codeText.getText().toString().trim();

                if(verificationcode.equals("")){
                    snackbar = Snackbar.make(findViewById(android.R.id.content),
                            "Code required", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }else{
                    loadingbar.setTitle("Code Verification");
                    loadingbar.setMessage("Please Wait. We are verifying your phone number");
                    loadingbar.setCanceledOnTouchOutside(false);
                    loadingbar.show();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationcode);
                    signInWithPhoneAuthCredential(credential);
                }
            }else{
                phoneNumber = ccp.getFullNumberWithPlus();
                if(!phoneNumber.equals("")){
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            this,               // Activity (for callback binding)
                            callbacks);        // OnVerificationStateChangedCallbacks
                }else{
                    snackbar = Snackbar.make(findViewById(android.R.id.content),
                            "Number Invalid", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                loadingbar.dismiss();
                snackbar = Snackbar.make(findViewById(android.R.id.content),
                        "Number Invalid", Snackbar.LENGTH_LONG);
                snackbar.show();
                layout.setVisibility(View.VISIBLE);

                continueNextButton.setText("Continue");
                codeText.setVisibility(View.GONE);
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                mVerificationId = s;
                mResendingToken = forceResendingToken;

                layout.setVisibility(View.GONE);
                checker = "Code Sent";
                continueNextButton.setText("Submit");
                codeText.setVisibility(View.VISIBLE);
                loadingbar.dismiss();
                snackbar = Snackbar.make(findViewById(android.R.id.content),
                        "Code has been sent.", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            loadingbar.dismiss();
                            snackbar = Snackbar.make(findViewById(android.R.id.content),
                                    "Login successful", Snackbar.LENGTH_LONG);
                            snackbar.show();
                            startActivity(new Intent(Login_.this, MainActivity.class));
                            finish();
                        } else {
                            // Sign in failed, display a message and update the UI
//                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }
}
