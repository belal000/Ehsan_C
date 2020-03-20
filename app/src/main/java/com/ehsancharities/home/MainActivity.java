package com.ehsancharities.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ehsancharities.R;
import com.ehsancharities.adapters.DonationsAdapter;
import com.ehsancharities.firebase.FirebaseServices;
import com.ehsancharities.login.LoginActivity;
import com.ehsancharities.model.Charity;
import com.ehsancharities.model.Donation;
import com.ehsancharities.utils.Const;
import com.ehsancharities.utils.Tools;
import com.ehsancharities.utils.UniversalImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    // double click to exit from app.
    boolean doubleBackToExitPressedOnce = false;

    private static final String TAG = "MainActivity";

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference documentReference;
    private Charity charity;
    private FirebaseServices firebaseServices;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private DonationsAdapter adapter;

    // Drawer Layout
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextView nameInHeader;
    private TextView emailInHeader;
    private ImageView profile_picture_header;
    private ProgressBar loadDonations;
    private RecyclerView donationsRecyclerView;

    private Uri filePath;
    private List<Donation> donations;


    // toolbar , show in the top page.
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initWidgets();
        setupToolbar();
        setupDrawerLayout();
        initFirebase();
        checkCurrentUser();

    }

    private void initWidgets() {
        donationsRecyclerView = findViewById(R.id.donations_recyvler_view);
        loadDonations = findViewById(R.id.load_donations);
        Tools.setSystemBarColor(this, R.color.blue_grey_900);
        Tools.initImageLoader(MainActivity.this);
        setTitle(getString(R.string.app_name));
    }

    private void checkCurrentUser() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            backToLogin();
        } else {
            initChairty(firebaseAuth.getCurrentUser().getUid());
            loadDonations.setVisibility(View.VISIBLE);
            getDonations();
        }
    }

    private void getDonations() {

        donations = new ArrayList<>();

        firebaseFirestore.collection(getString(R.string.firebase_charities))
                .document(firebaseAuth.getUid())
                .collection(getString(R.string.firebase_donations))
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                        List<Donation> donationsList = queryDocumentSnapshots.toObjects(Donation.class);

                        loadDonations.setVisibility(View.GONE);

                        setupRecyclerViewShops(donationsList);
                    }
                });
    }

    private void setupRecyclerViewShops(List<Donation> donationList) {

        adapter = new DonationsAdapter(this, donationList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        donationsRecyclerView.setLayoutManager(mLayoutManager);
        donationsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        donationsRecyclerView.setAdapter(adapter);

    }

    private void initChairty(String chairtyID) {
        firebaseFirestore = FirebaseFirestore.getInstance();
        documentReference = firebaseFirestore.collection(getString(R.string.firebase_charities)).document(chairtyID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {

                    charity = documentSnapshot.toObject(Charity.class);
                    updateUI(charity);

                    Log.e(TAG, "Current data: " + documentSnapshot.getData());
                } else {
                    if (charity == null) {
                        Tools.showMessage(getApplicationContext(), getString(R.string.you_are_not_charity));
                        signOutMethod();
                    }
                }
            }
        });
    }

    private void updateUI(Charity charity) {

        Log.e(TAG, "updateUI: " + charity.toString());
        View headerView = navigationView.getHeaderView(0);
        nameInHeader = headerView.findViewById(R.id.nameInHeader);
        emailInHeader = headerView.findViewById(R.id.emailInHeader);
        nameInHeader.setText(charity.getCharityName());
        emailInHeader.setText(charity.getCharityEmail());
        profile_picture_header = headerView.findViewById(R.id.profile_picture_header);

        UniversalImageLoader.setImage(charity.getChairtyImage(), profile_picture_header, null, "");

    }

    private void setupDrawerLayout() {

        // DrawerLayout in activity_main.xml
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }
        };
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    private void initFirebase() {
        firebaseServices = FirebaseServices.getFirebaseServicesInstance();
        firebaseServices.setContext(MainActivity.this);
    }

    private void setupToolbar() {
        toolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);
    }

    private void backToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void signOutMethod() {
        firebaseAuth.signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        this.finish();
    }

    @Override
    public void onBackPressed() {


        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Tools.showMessage(MainActivity.this, getString(R.string.confrim_exit));

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        int id = menuItem.getItemId();

        if (id == R.id.home) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else if (id == R.id.set_profile_picture) {
            chooseImage();
        }
//        else if (id == R.id.setting) {
//            this.drawerLayout.closeDrawer(GravityCompat.START);
//
//        } else if (id == R.id.contact_us) {
//            goToContactUs();
//            this.drawerLayout.closeDrawer(GravityCompat.START);
//
//        } else if (id == R.id.about) {
//            goToAboutUs();
//            this.drawerLayout.closeDrawer(GravityCompat.START);
//
//        }
        else if (id == R.id.signout) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
            signOutMethod();
        }

        return true;
    }


    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), Const.PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Const.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                profile_picture_header.setImageBitmap(bitmap);
                uploadImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {

        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle(getString(R.string.uploading));
            progressDialog.show();

            final StorageReference ref = storageReference.child(Const.CHARITIES_IMAGES + firebaseAuth.getCurrentUser().getUid());
            ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();

                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            Uri downloadUrl = uri;
                            String fileUrl = downloadUrl.toString();

                            Log.e(TAG, "onSuccess: " + fileUrl);
                            final String userID = firebaseAuth.getCurrentUser().getUid();

                            firebaseServices.updateProfile(fileUrl, userID);
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Tools.showMessage(MainActivity.this, getString(R.string.failed) + " " + e.getMessage());
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage("Uploaded " + (int) progress + "%");
                }
            });
        }


    }
}
