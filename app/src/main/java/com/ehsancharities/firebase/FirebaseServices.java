package com.ehsancharities.firebase;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ehsancharities.AdminActivity;
import com.ehsancharities.R;
import com.ehsancharities.home.MainActivity;
import com.ehsancharities.login.LoginActivity;
import com.ehsancharities.model.Charity;
import com.ehsancharities.utils.Session;
import com.ehsancharities.utils.Tools;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FirebaseServices {

    private static final String TAG = "FirebaseServices";
    private FirebaseAuth firebaseAuth;
    private String userID;
    private Context context;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference documentReference;
    private FirebaseStorage storage;

    private static FirebaseServices firebaseServicesInstance = null;

    private FirebaseServices() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    public void setContext(Context context) {
        this.context = context;
        if (firebaseAuth.getCurrentUser() != null) {
            userID = firebaseAuth.getCurrentUser().getUid();
        }

    }

    public static FirebaseServices getFirebaseServicesInstance() {

        if (firebaseServicesInstance == null) {
            firebaseServicesInstance = new FirebaseServices();
        }
        return firebaseServicesInstance;
    }


    public void createCharity(final Charity charity, String password, final ProgressBar signUpLoading) {

        firebaseAuth.createUserWithEmailAndPassword(charity.getCharityEmail(), password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            userID = firebaseAuth.getCurrentUser().getUid();
                            charity.setCharityID(userID);
                            addNewCharity(charity, signUpLoading);
                        } else {
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        signUpLoading.setVisibility(View.GONE);
                        Tools.showMessage(context, context.getString(R.string.error_sign_up));
                    }
                });

    }

    public void addNewCharity(Charity charity, final ProgressBar signUpLoading) {


        firebaseFirestore.collection(context.getString(R.string.firebase_charities)).document(this.userID)
                .set(charity)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        Tools.showMessage(context, context.getString(R.string.successfully_sign_up));
                        signUpLoading.setVisibility(View.GONE);
                        Intent intent = new Intent(context, LoginActivity.class);
                        context.startActivity(intent);
                        ((AppCompatActivity) context).finish();


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                        signUpLoading.setVisibility(View.GONE);
                        Tools.showMessage(context, context.getString(R.string.error_sign_up));

                    }
                });
    }

    public void login(String email, String password, final ProgressBar loginLoading) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {


                            firebaseFirestore.collection(context.getString(R.string.firebase_charities)).document(firebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                    Long stateAccount = task.getResult().get("stateAccount") == null ? 0 : (Long) task.getResult().get("stateAccount");


                                    String typeAccount = task.getResult().get("accountType").toString();

                                    Session.getInstance().setTYPE_ACCOUNT(context, typeAccount);

                                    if (typeAccount.equals("Admin")) {

                                        Intent intent = new Intent(context, AdminActivity.class);
                                        context.startActivity(intent);
                                        ((AppCompatActivity) context).finish();


                                    }


                                    if (stateAccount == 0)
                                        Toast.makeText(context, " Please wait for your request to be approved", Toast.LENGTH_LONG).show();
                                    else {


                                        Intent intent = new Intent(context, MainActivity.class);
                                        context.startActivity(intent);
                                        ((AppCompatActivity) context).finish();


                                    }

                                    loginLoading.setVisibility(View.GONE);

                                }
                            });


                        } else {
                            Tools.showMessage(context, context.getString(R.string.authentication_failed));
                            loginLoading.setVisibility(View.GONE);
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loginLoading.setVisibility(View.GONE);
                    }
                });
    }


    public void updateProfile(String url, String currentUserID) {

        documentReference = firebaseFirestore.collection(context.getString(R.string.firebase_charities))
                .document(currentUserID);


        documentReference.update("chairtyImage", url)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Tools.showMessage(context, context.getString(R.string.upload_image));
                        } else {
                            Tools.showMessage(context, context.getString(R.string.try_again));
                        }

                    }
                });
    }

}
