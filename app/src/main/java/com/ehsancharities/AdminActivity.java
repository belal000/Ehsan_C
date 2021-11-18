package com.ehsancharities;

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
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ehsancharities.adapters.AdapterCharty;
import com.ehsancharities.adapters.DonationsAdapter;
import com.ehsancharities.firebase.FirebaseServices;
import com.ehsancharities.home.MainActivity;
import com.ehsancharities.login.LoginActivity;
import com.ehsancharities.model.Charity;
import com.ehsancharities.model.Donation;
import com.ehsancharities.utils.Const;
import com.ehsancharities.utils.Tools;
import com.ehsancharities.utils.UniversalImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class AdminActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    RecyclerView rec_charty;
    AdapterCharty adapterCharty;
    private DrawerLayout drawer_layout_admin;
    private NavigationView navigationView;
    private TextView nameInHeader;
    private TextView emailInHeader;
    private ImageView profile_picture_header;
    private ProgressBar loadCharity;

    List<Charity> charityList;

    private Toolbar admin_toolbar;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference documentReference;
    private FirebaseServices firebaseServices;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private Charity charity;
    private static final String TAG = "MainActivity";

    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        initWidgets();
        setupToolbar();
        setupDrawerLayout();
        initFirebase();
        checkCurrentUser();


    }

    private void initWidgets() {
        rec_charty = findViewById(R.id.rec_charty);

        loadCharity = findViewById(R.id.loadCharity);
        Tools.setSystemBarColor(this, R.color.blue_grey_900);
        Tools.initImageLoader(AdminActivity.this);
        setTitle("Admin Page");
    }

    private void setupToolbar() {
        admin_toolbar = findViewById(R.id.admin_toolbar);
        setSupportActionBar(admin_toolbar);
    }

    private void setupDrawerLayout() {

        // DrawerLayout in activity_main.xml
        drawer_layout_admin = findViewById(R.id.drawer_layout_admin);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer_layout_admin, admin_toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }
        };
        drawer_layout_admin.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
                        //signOutMethod();
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

    private void initFirebase() {
        firebaseServices = FirebaseServices.getFirebaseServicesInstance();
        firebaseServices.setContext(AdminActivity.this);
    }

    private void checkCurrentUser() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            //backToLogin();
        } else {
            initChairty(firebaseAuth.getCurrentUser().getUid());
            loadCharity.setVisibility(View.VISIBLE);
            getCharty();
        }
    }

    private void signOutMethod() {
        firebaseAuth.signOut();
        Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
        startActivity(intent);
        this.finish();
    }

    private void setupRecyclerViewShops(List<Charity> charityList) {

        adapterCharty = new AdapterCharty(charityList, getBaseContext());
        rec_charty.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        rec_charty.setAdapter(adapterCharty);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        int id = menuItem.getItemId();

        if (id == R.id.home) {
            this.drawer_layout_admin.closeDrawer(GravityCompat.START);
        } else if (id == R.id.set_profile_picture) {
            chooseImage();
        }
//        else if (id == R.id.set_profile_loc) {
//            selectLocation();
//
//        }
        //else if (id == R.id.contact_us) {
//            goToContactUs();
//            this.drawerLayout.closeDrawer(GravityCompat.START);
//
//        } else if (id == R.id.about) {
//            goToAboutUs();
//            this.drawerLayout.closeDrawer(GravityCompat.START);
//
//        }
        else if (id == R.id.signout) {
            this.drawer_layout_admin.closeDrawer(GravityCompat.START);
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
                    Tools.showMessage(AdminActivity.this, getString(R.string.failed) + " " + e.getMessage());
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

    private void getCharty() {

        charityList = new ArrayList<>();


        Query query = FirebaseFirestore.getInstance().collection(getString(R.string.firebase_charities)).whereEqualTo("stateAccount", 0);


        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {


                    if (documentChange.getType() == DocumentChange.Type.ADDED) {


                        Charity charity = documentChange.getDocument().toObject(Charity.class);

                        charityList.add(charity);
                        setupRecyclerViewShops(charityList);
                        adapterCharty.notifyDataSetChanged();
                    }


                }
                loadCharity.setVisibility(View.GONE);
            }
        });


    }
}