package com.hb.tm.utils.http;


import android.content.Context;
import com.hb.tm.data.DataManager;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import javax.inject.Inject;
import java.io.IOException;

/**
 * Created by haibui on 2017-02-27.
 */

public class HttpHeaderInterceptor implements Interceptor {

    private final DataManager mDataManager;
    private final Context mContext;

    @Inject
    public HttpHeaderInterceptor(Context context, DataManager dataManager) {
        mContext = context;
        mDataManager = dataManager;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

//        String url = chain.request().url().toString().toLowerCase();

        Request original = chain.request();
        Headers headers = original.headers();
        Headers.Builder builder = original.headers().newBuilder();

        builder.removeAll("Authorization");
        builder.addAll(headers);

////        if (isHostVBD) {
////            builder.add("RegisterKey", getAberKey());
////        } else {
//        if (url.contains("login") || url.contains("version")) {
//
//        } else {
//            builder.add("Authorization", "Bearer " + mDataManager.getToken());
//        }
////        }


        Request request = original.newBuilder()
                .headers(builder.build())
                .method(original.method(), original.body())
                .build();

        return chain.proceed(request);


    }
}
