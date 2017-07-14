package com.jeffreymor.googlehosts.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.jeffreymor.googlehosts.MyConstants;
import com.jeffreymor.googlehosts.HostsRetrofit;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import cz.msebera.android.httpclient.Header;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class DownloadUtil {
	private static final String TAG = "DownloadUtil";

	public static void downloadHostFile(final Context context, final DownloadListener downloadListener) {
		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl("https://raw.githubusercontent.com/")
				.build();
		HostsRetrofit rf = retrofit.create(HostsRetrofit.class);
		Call<ResponseBody> call = rf.getHosts();
		call.enqueue(new Callback<ResponseBody>() {

			@Override
			public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
				OutputStream os = null;
				try {
					byte[] bytes = response.body().bytes();
					File saveDir = context.getFilesDir();
					if (!saveDir.exists()) {
						saveDir.mkdirs();
					}

					File hostFile = new File(saveDir.getAbsolutePath() + File.separator + MyConstants.DOWNLOAD_HOST_NAME);
					os = new FileOutputStream(hostFile);
					os.write(bytes);
					Log.d(TAG, "onResponse: Download Success!");
                    downloadListener.success(hostFile);
				} catch (IOException e) {
					e.printStackTrace();
					downloadListener.error();
				} finally {
					if (os != null) {
						try {
							os.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}

			}

			@Override
			public void onFailure(Call<ResponseBody> call, Throwable t) {
				downloadListener.error();
			}
		});
	}



    public interface DownloadListener {
		void success(File file);

		void error();
	}

}
