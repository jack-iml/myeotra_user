package com.myeotra.user.ui.fragment.cancel_ride;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.myeotra.user.R;
import com.myeotra.user.data.network.model.CancelResponse;

import java.util.List;

class CustomAdapter extends BaseAdapter {
    Activity activity;
    List<CancelResponse> reasonlist;


    public CustomAdapter(Activity activity, List<CancelResponse> reasonlist) {
        this.activity = activity;
        this.reasonlist = reasonlist;
    }

    @Override
    public int getCount() {
        return reasonlist.size();
    }

    @Override
    public Object getItem(int position) {
        return reasonlist.get(position).getReason();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view = LayoutInflater.from(activity).inflate(R.layout.cancel_reasons_inflate, null);
        TextView names = view.findViewById(R.id.tvReason);
        names.setText(reasonlist.get(position).getReason());
        return view;
    }
}
