package com.myeotra.user.ui.fragment.map;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.myeotra.user.R;
import com.myeotra.user.base.BaseFragment;
import com.myeotra.user.data.SharedHelper;
import com.myeotra.user.ui.fragment.destination.DestinationFragment;

import java.util.Objects;

import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.myeotra.user.data.SharedHelper.key.SELECTED_SERVICE;


public class MapFragment extends BaseFragment implements MapIView, OnMapReadyCallback {

    Unbinder unbinder;


    /*@BindView(R.id.rltvLocation)
    RelativeLayout rltvLocation;*/

    private BottomSheetBehavior bsBehavior;


    private MapPresenter<MapIView> presenter = new MapPresenter<>();

    public static MapFragment newInstance(String param2) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_map;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected View initView(View view) {
        unbinder = ButterKnife.bind(this, view);
        presenter.attachView(this);
//        ((MainActivity) (Objects.requireNonNull(getActivity()))).changeFlow(new DestinationFragment());

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.map_container, new DestinationFragment());
        ft.commit();

        String serviceFor = getArguments().getString("servicefrom");

//        Toast.makeText(getActivity(), serviceFor, Toast.LENGTH_SHORT).show();

        SharedHelper.putKey(Objects.requireNonNull(getActivity()), SELECTED_SERVICE, serviceFor);

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }


//        ((MainActivity) (Objects.requireNonNull(getActivity()))).changeFlow("null", getActivity());


       /* bsBehavior = BottomSheetBehavior.from(container);
        bsBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, @BottomSheetBehavior.State int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        BottomSheetBehavior.from(container).setHideable(true);
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        BottomSheetBehavior.from(container).setHideable(true);
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        BottomSheetBehavior.from(container).setHideable(true);
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
*/

        return view;
    }

    /*public void changeFlow(String type) {
        if (type == null) {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container, new DestinationFragment(), "servicefrom");
            ft.commit();
        }
        else if(type == ){
        }
    }*/


    @Override
    public void onSuccess(Object object) {

    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }


}
