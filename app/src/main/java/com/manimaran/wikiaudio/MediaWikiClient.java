package com.manimaran.wikiaudio;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface MediaWikiClient {

    //@GET("action=query&meta=tokens&format=json&type=login")
    @GET("https://en.wikipedia.org/w/api.php?action=query&meta=tokens&format=json&type=login")
    Call<ResponseBody> getToken();

    @FormUrlEncoded
    @POST("https://en.wikipedia.org/w/api.php?")
    Call<ResponseBody> clientLogin(
            @Field("action") String action,
            @Field("format") String format,
            @Field("loginreturnurl") String url,
            @Field("logintoken") String token,
            @Field("username") String username,
            @Field("password") String password
    );

    @GET("https://ta.wiktionary.org/w/api.php?action=query&list=search&utf8=&format=json")
    Call<ResponseBody> fetchRecords(
            @Query("srsearch") String searchString,
            @Query("sroffset") Integer offSet
    );

}
