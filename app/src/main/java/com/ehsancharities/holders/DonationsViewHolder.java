package com.ehsancharities.holders;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ehsancharities.R;

public class DonationsViewHolder extends RecyclerView.ViewHolder {

    public ImageView donateImage;
    public ProgressBar donateImageLoad;
    public TextView donatorName;
    public TextView donationDescription;
    public TextView donationItems;
    public ImageButton callDonator;
    public ImageButton emailDonator;
    public ImageButton deleteDonator;


    public DonationsViewHolder(@NonNull View itemView) {
        super(itemView);

        donateImage = itemView.findViewById(R.id.donate_image);
        donateImageLoad = itemView.findViewById(R.id.donate_image_load);
        donatorName = itemView.findViewById(R.id.donator_name);
        donationDescription = itemView.findViewById(R.id.donation_description);
        donationItems = itemView.findViewById(R.id.donation_items);
        callDonator = itemView.findViewById(R.id.call_donation);
        emailDonator = itemView.findViewById(R.id.email_donation);
        deleteDonator = itemView.findViewById(R.id.delete_donation);
    }
}