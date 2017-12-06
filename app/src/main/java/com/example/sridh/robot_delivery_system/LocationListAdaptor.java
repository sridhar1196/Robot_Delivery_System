package com.example.sridh.robot_delivery_system;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import io.realm.Realm;

/**
 * Created by sridh on 11/9/2017.
 */

public class LocationListAdaptor extends RecyclerView.Adapter<LocationListAdaptor.ViewHolder>  {
    MainActivity mainActivity;
    int location_list;
    int select;
    ArrayList<LocationDetails> locationDetails;
    public LocationListAdaptor(MainActivity mainActivity, int location_list, ArrayList<LocationDetails> locations) {
        this.mainActivity = mainActivity;
        this.location_list = location_list;
        this.locationDetails = locations;
        select = locations.size();
    }

    @Override
    public LocationListAdaptor.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.location_list, parent, false);
        ViewHolder viewHolder=new ViewHolder(view);

        return viewHolder;
    }
    @Override
    public void onBindViewHolder(LocationListAdaptor.ViewHolder holder, int position) {
        holder.pickUp.setText(locationDetails.get(position).getPickUp().trim());
        holder.drop.setText(locationDetails.get(position).getDrop().trim());
        holder.mainActivity = mainActivity;
        Log.d("demo","select blind:"+select);
        if(select == position){
            holder.star.setImageResource(android.R.drawable.btn_star_big_on);
        } else {
            holder.star.setImageResource(android.R.drawable.btn_star_big_off);
        }
        holder.locationDetails = locationDetails;
        holder.position = position;
        //holder.select = select;
    }
    @Override
    public int getItemCount() {
        if(locationDetails != null){
            return  locationDetails.size();
        } else {
            return 0;
        }
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView pickUp,drop;
        ImageView star;
        int position;
        MainActivity mainActivity;
        ArrayList<LocationDetails> locationDetails;
        public ViewHolder(View itemView) {
            super(itemView);
            pickUp = (TextView) itemView.findViewById(R.id.pickUp_Location);
            drop = (TextView) itemView.findViewById(R.id.drop_Location);
            star = (ImageView) itemView.findViewById(R.id.star);
            star.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("demo","select:"+select);
                    Log.d("demo","position:"+position);
                    select = position;
                    mainActivity.change_Location(select);
                    LocationListAdaptor.this.notifyDataSetChanged();
                }
            });
//            itemView.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View view) {
//                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
//                            mainActivity);
//
//                    // set title
//                    alertDialogBuilder.setTitle("Delete Course");
//
//                    // set dialog message
//                    alertDialogBuilder
//                            .setMessage("Are you sure ?")
//                            .setCancelable(false)
//                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    locationDetails.remove(position);
//                                }
//                            })
//                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//
//                                    dialog.cancel();
//                                }
//                            });
//
//                    // create alert dialog
//                    AlertDialog alertDialog = alertDialogBuilder.create();
//
//                    // show it
//                    alertDialog.show();
//                    return true;
//                }
//            });
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    mainActivity.edit_Location(locationDetails.get(position));
//                }
//            });
        }
    }

}
