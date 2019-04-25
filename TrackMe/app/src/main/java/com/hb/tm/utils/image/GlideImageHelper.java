package com.hb.tm.utils.image;

import android.content.Context;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.hb.tm.R;


public class GlideImageHelper implements ImageHelper {

    private RequestOptions centerCrop;
    private RequestOptions fitCenter;
    private RequestOptions avatar;


    private Context mContext;

    public GlideImageHelper(Context context) {
        mContext = context;

        fitCenter = new RequestOptions()
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.drawable.no_image)
                .error(R.drawable.no_image)
                .priority(Priority.HIGH);

        centerCrop = new RequestOptions()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.drawable.no_image)
                .error(R.drawable.no_image)
                .priority(Priority.HIGH);


        avatar = new RequestOptions()
                .fitCenter()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.drawable.no_image)
                .error(R.drawable.no_image)
                .priority(Priority.HIGH);

    }

    @Override
    public void loadImage(ImageView view, Object path) {
        Glide.with(view)
                .load(path)
                .apply(centerCrop)
                .into(view);
    }


}
