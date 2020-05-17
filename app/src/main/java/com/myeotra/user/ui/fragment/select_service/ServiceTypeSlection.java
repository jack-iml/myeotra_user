package com.myeotra.user.ui.fragment.select_service;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.myeotra.user.R;
import com.myeotra.user.base.BaseFragment;
import com.myeotra.user.data.network.model.Service;
import com.myeotra.user.ui.activity.main.MainActivity;
import com.myeotra.user.ui.fragment.map.MapFragment;
import com.myeotra.user.ui.fragment.service.ServiceTypesIView;

import java.util.List;

public class ServiceTypeSlection extends BaseFragment implements ServiceTypesIView, View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    RelativeLayout rltvRideService, rltvloutDelivery, rltvcourier;
    ImageView menu;
    private String mParam1;
    private String mParam2;


    public ServiceTypeSlection() {
    }


    public static ServiceTypeSlection newInstance(String param1, String param2) {
        ServiceTypeSlection fragment = new ServiceTypeSlection();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_service_type_selection;
    }

    @Override
    protected View initView(View view) {

        rltvRideService = view.findViewById(R.id.rltvRideService);
        rltvloutDelivery = view.findViewById(R.id.rltvloutDelivery);
        rltvcourier = view.findViewById(R.id.rltvcourier);
        rltvcourier = view.findViewById(R.id.rltvcourier);
        menu = view.findViewById(R.id.menu);
        menu.setOnClickListener(this);

        rltvRideService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedservice(new MapFragment(), "RideService");
            }
        });

        rltvloutDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedservice(new MapFragment(), "delivery");


            }
        });

        rltvcourier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedservice(new MapFragment(), "Courier");
            }
        });

        return view;
    }

    private void selectedservice(Fragment sourceFragment, String servicefrom) {
        Bundle bundle = new Bundle();
        bundle.putString("servicefrom", servicefrom);
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        sourceFragment.setArguments(bundle);
        ft.replace(R.id.container_main, sourceFragment, servicefrom);
        ft.addToBackStack(null);
        ft.commit();
    }


    @Override
    public void onSuccess(List<Service> serviceList) {

    }

    @Override
    public void onSuccess(Object object) {

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menu:
                ((MainActivity) getActivity()).checknavigationdrawer();
                break;

        }

    }
}
