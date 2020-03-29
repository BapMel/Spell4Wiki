package com.manimaran.wikiaudio.apis;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;

import android.text.TextUtils;
import android.util.Log;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.manimaran.wikiaudio.R;
import com.manimaran.wikiaudio.constants.Urls;
import com.manimaran.wikiaudio.utils.PrefManager;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static Retrofit retrofitCommons = null;
    private static Retrofit retrofitWiktionary = null;
    private static Retrofit retrofitApi = null;
    @SuppressLint("StaticFieldLeak")
    private static PrefManager pref;

    public static Retrofit getCommonsApi(Context context) {
        if (retrofitCommons == null) {
            retrofitCommons = new Retrofit.Builder()
                    .baseUrl(Urls.COMMONS)
                    .client(Objects.requireNonNull(getOkHttpClient(context)))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitCommons;
    }

    public static Retrofit getWiktionaryApi(Context context, String langCode) {
        retrofitWiktionary = new Retrofit.Builder()
                    .baseUrl(getWiktionaryApiUrl(langCode))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        return retrofitWiktionary;
    }

    public static Retrofit getApi(){
        if (retrofitApi == null) {
            retrofitApi = new Retrofit.Builder()
                    .baseUrl("https://github.com")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitApi;
    }

    private static String getWiktionaryApiUrl(String langCode){
        if(TextUtils.isEmpty(langCode))
            langCode = pref.getLanguageCodeSpell4Wiki();
        return  String.format(Urls.WIKTIONARY, langCode);
    }

    private static OkHttpClient getOkHttpClient(Context context) {

        pref = new PrefManager(context);
        OkHttpClient.Builder okHttpClient = new OkHttpClient().newBuilder();

        okHttpClient.retryOnConnectionFailure(true);
        /*okHttpClient.connectTimeout(60 * 5, TimeUnit.SECONDS);
        okHttpClient.readTimeout(60 * 5, TimeUnit.SECONDS);
        okHttpClient.writeTimeout(60 * 5, TimeUnit.SECONDS);
*/
        ClearableCookieJar cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));
        //okHttpClient.cookieJar(cookieJar);

        /*// Logging
        okHttpClient.interceptors().add(logging);

        // Query params
        okHttpClient.interceptors().add(queryParamInterceptor);

        // Add Cookies interceptors
        okHttpClient.interceptors().add(addCookiesInterceptor);

        // Received cookies interceptors
        okHttpClient.interceptors().add(receiveCookiesInterceptor);*/

        if (!okHttpClient.interceptors().contains(logging)) {
            okHttpClient.interceptors().add(logging);
        }

        if (!okHttpClient.interceptors().contains(queryParamInterceptor)) {
            okHttpClient.addInterceptor(queryParamInterceptor);
        }


        if (!okHttpClient.interceptors().contains(addCookiesInterceptor)) {
            okHttpClient.addInterceptor(addCookiesInterceptor);
        }


        if (!okHttpClient.interceptors().contains(receiveCookiesInterceptor)) {
            okHttpClient.addInterceptor(receiveCookiesInterceptor);
        }


        return okHttpClient.build();
    }

    // Logging method
    private static HttpLoggingInterceptor logging = new HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY); // Log the response body

    private static Interceptor queryParamInterceptor = chain -> {
        Request original = chain.request();
        HttpUrl originalHttpUrl = original.url();

        HttpUrl url = originalHttpUrl.newBuilder().addQueryParameter("format", "json").build();

        // Request customization: add request headers
        Request.Builder requestBuilder = original.newBuilder().url(url);

        Request request = requestBuilder.build();
        return chain.proceed(request);
    };

    private static Interceptor addCookiesInterceptor = new Interceptor() {
        @Override
        public Response intercept(@NonNull Chain chain) throws IOException {
            Request.Builder builder = chain.request().newBuilder();
            Set<String> preferences = pref.getCookies();
            if (preferences != null && preferences.size() > 0) {
                for (String cookie : preferences) {
                    builder.addHeader("Cookie", cookie);
                    Log.v("OkHttp", "Adding Header: " + cookie); // This is done so I know which headers are being added; this interceptor is used after the normal logging of OkHttp
                }
            }
            return chain.proceed(builder.build());
        }
    };

    private static Interceptor receiveCookiesInterceptor = new Interceptor() {
        @NotNull
        @Override
        public Response intercept(@NonNull Chain chain) throws IOException {
            Response originalResponse = chain.proceed(chain.request());
            if (!originalResponse.headers("Set-Cookie").isEmpty()) {
                HashSet<String> cookies = new HashSet<>(originalResponse.headers("Set-Cookie"));
                Log.v("OkHttp", "Receive Header: " + cookies.toString());
                pref.setCookies(cookies);
            }
            return originalResponse;
        }
    };
}
