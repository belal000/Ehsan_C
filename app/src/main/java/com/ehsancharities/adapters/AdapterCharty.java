package com.ehsancharities.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ehsancharities.R;
import com.ehsancharities.model.Charity;
import com.ehsancharities.utils.Tools;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/**
 * Created by Belal Jaradat on 12/18/2020.
 */
public class AdapterCharty  extends RecyclerView.Adapter<AdapterCharty.holder> {


    List<Charity> charityList ;

    Context context ;

    public AdapterCharty(List<Charity> charityList, Context context) {
        this.charityList = charityList;
        this.context = context;
    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_charty , parent , false));
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, final int position) {


        holder.name.setText(charityList.get(position).getCharityName());
        holder.address.setText(charityList.get(position).getCharityAddress());
        holder.email.setText(charityList.get(position).getCharityEmail());


        holder.acbet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseFirestore.getInstance()
                        .collection(context.getString(R.string.firebase_charities))
                        .document(charityList.get(position).getCharityID())
                        .update("stateAccount", 1).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful())
                        {
                            Tools.showMessage(context,"Account was approved");
                            charityList.remove(position) ;
                            notifyDataSetChanged();

                        }

                    }
                });

            }
        });

        holder.not.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore.getInstance()
                        .collection(context.getString(R.string.firebase_charities))
                        .document(charityList.get(position).getCharityID())
                        .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful())
                        {
                            Tools.showMessage(context,"Account was not approved");
                            charityList.remove(position) ;
                            notifyDataSetChanged();

                        }

                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return charityList.size();
    }

    class  holder extends RecyclerView.ViewHolder {

        TextView name,address,email;
        ImageButton acbet,not;

        public holder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name) ;
            address=itemView.findViewById(R.id.address);
            email=itemView.findViewById(R.id.email);

            acbet = itemView.findViewById(R.id.acebt);
            not=itemView.findViewById(R.id.not);

        }
    }
}
