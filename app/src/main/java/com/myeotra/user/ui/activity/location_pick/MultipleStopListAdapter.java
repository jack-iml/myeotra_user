package com.myeotra.user.ui.activity.location_pick;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.myeotra.user.R;
import com.myeotra.user.data.network.model.MultiLocationModel;

import java.util.ArrayList;

public class MultipleStopListAdapter extends RecyclerView.Adapter<MultipleStopListAdapter.MyViewHolder> {

    Activity activity;
    ArrayList<MultiLocationModel> multilocatonmodelArrayList;

    public MultipleStopListAdapter(Activity activity, ArrayList<MultiLocationModel> multilocatonmodelArrayList) {
        this.activity = activity;
        this.multilocatonmodelArrayList = multilocatonmodelArrayList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_multilocation, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MultiLocationModel model = multilocatonmodelArrayList.get(position);
        holder.tvStopname.setText(model.getS_address());

        holder.tvStoplbl.setText("Stop " + (position + 1) + ": ");
        holder.ivRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                multilocatonmodelArrayList.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return multilocatonmodelArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView ivRemove;
        private TextView tvStopname, tvStoplbl;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvStopname = itemView.findViewById(R.id.tvStopname);
            ivRemove = itemView.findViewById(R.id.ivRemove);
            tvStoplbl = itemView.findViewById(R.id.tvStoplbl);
        }
    }
}
