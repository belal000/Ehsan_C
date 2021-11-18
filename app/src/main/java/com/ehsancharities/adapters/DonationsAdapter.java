package com.ehsancharities.adapters;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ehsancharities.MapsActivity;
import com.ehsancharities.R;
import com.ehsancharities.holders.DonationsViewHolder;
import com.ehsancharities.model.Donation;
import com.ehsancharities.utils.Const;
import com.ehsancharities.utils.Tools;
import com.ehsancharities.utils.UniversalImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class DonationsAdapter extends RecyclerView.Adapter<DonationsViewHolder> {

    private Context context;
    private List<Donation> donationsList;

    public DonationsAdapter(Context context, List<Donation> donationsList) {
        this.context = context;
        this.donationsList = donationsList;
    }

    @Override
    public DonationsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.donation_item, parent, false);

        return new DonationsViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(DonationsViewHolder holder, final int position) {
        final Donation donation = donationsList.get(position);

        holder.donatorName.setText(donation.getDonationName());
        holder.donationDescription.setText(donation.getDescriptions());

        String listItem = "";

        for (String str : donation.getItemsDonations()) {
            listItem += "-" + str + "\n";
        }
        holder.donationItems.setText(listItem);

        UniversalImageLoader.setImage(donation.getDonationImage(), holder.donateImage, holder.donateImageLoad, "");

        holder.callDonator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                permissionCheckCall(donation.getDonationPhone());

            }
        });

        holder.emailDonator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendEmail(donation.getDonationEmail());

            }
        });

        holder.deleteDonator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseFirestore.getInstance()
                        .collection(context.getString(R.string.firebase_charities))
                        .document(donation.getCharityID())
                        .collection(context.getString(R.string.firebase_donations))
                        .document(donation.getDonationID()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            Tools.showMessage(context , "Donation was Deleted.");
                        }
                    }
                });



            }
        });
        holder.loc_donation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, MapsActivity.class);
                intent.putExtra("lat" ,donation.getLat());

                intent.putExtra("lng" ,donation.getLng());

                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return donationsList.size();
    }


    private void sendEmail(String email) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.donations_charities));
        intent.setPackage("com.google.android.gm");
        if (intent.resolveActivity(context.getPackageManager()) != null)
            context.startActivity(intent);
        else
            Toast.makeText(context, R.string.no_gmail, Toast.LENGTH_SHORT).show();
    }

    private void permissionCheckCall(String phoneNumber) {

        int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            makeCall(phoneNumber);
        } else {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE}, Const.MY_PERMISIONS_REQUEST_MAKE_CALL);
        }

    }

    private void makeCall(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        String phone_number = String.valueOf(phoneNumber);

        if (phone_number.trim().isEmpty()) {
            Tools.showMessage(context, context.getString(R.string.cannot_call));
        } else {
            intent.setData(Uri.parse("tel:" + phone_number));
        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            permissionCheckCall(phoneNumber);
        } else {
            context.startActivity(intent);
        }
    }

}
