package com.jeffreymor.googlehosts;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Mor on 2017/6/9.
 */

public interface HostsRetrofit {
    @GET("googlehosts/hosts/master/hosts-files/hosts")
    Call<ResponseBody> getHosts();
}
