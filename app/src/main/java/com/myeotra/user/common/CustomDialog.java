package com.myeotra.user.common;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.myeotra.user.R;


public class CustomDialog extends Dialog {

    //    ImageView imageview;
//    GifImageView gifImageView;
    ImageView gifImageView;

    public CustomDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);
//        imageview = findViewById(R.id.imageview);

        gifImageView = findViewById(R.id.gifImageView);
//        gifImageView.setGifImageResource(R.raw.loader);

//        gifImageView.setImageResource(R.drawable.loader);

        Glide.with(context)
                .load(R.drawable.loader)
                .into(gifImageView);
        setCancelable(false);


    }
}